package com.walletka.app.io.client

import android.content.SharedPreferences
import android.util.Log
import arrow.core.getOrElse
import com.walletka.app.usecases.GetMnemonicSeedUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nostr_sdk.Client
import nostr_sdk.Contact
import nostr_sdk.Event
import nostr_sdk.EventBuilder
import nostr_sdk.EventId
import nostr_sdk.Filter
import nostr_sdk.HandleNotification
import nostr_sdk.Keys
import nostr_sdk.Metadata
import nostr_sdk.PublicKey
import nostr_sdk.RelayMessage
import nostr_sdk.TagEnum
import nostr_sdk.Timestamp
import nostr_sdk.nip04Decrypt
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class NostrClient @Inject constructor(
    private val getMnemonicSeed: GetMnemonicSeedUseCase,
    private val sharedPreferences: SharedPreferences
) : CoroutineScope {
    private val keys by lazy {
        val mnemonic = getMnemonicSeed().getOrElse { throw Exception("Missing mnemonic key!!!") }
        Keys.fromMnemonic(mnemonic)
    }

    val TAG = "NostrRepository"

    private val client by lazy {
        Client(keys)
    }

    private var _connected = false
    fun isConnected() = _connected

    fun getNpub(): String {
        return keys.publicKey().toBech32()
    }

    fun getPubKey(): PublicKey {
        return keys.publicKey()
    }

    var contacts = mutableListOf<Contact>()
    val contactsChannel: Channel<List<Contact>> = Channel()

    val messagesChannel: Channel<Pair<Event, String>> = Channel()


    fun start() {
        client.addRelay("wss://nostr.tchaicap.space")
        client.addRelay("wss://nostr.oxtr.dev")

        client.connect()
        listenEvents()

        subscribeMessages()

        _connected = true

        val alias = sharedPreferences.getString("lsp_alias", null)

        val initialized = sharedPreferences.getBoolean("nostr_initialized", false)
        if (!initialized) {

            val metadata = nostr_sdk.Metadata()
                .setName(alias ?: "Walletka user")
                .setDisplayName(alias ?: "Walletka user")

            // Update metadata
            client.setMetadata(metadata)
            if (alias != null)
                sharedPreferences.edit().putBoolean("nostr_initialized", true).apply()
        }


        Log.i(TAG, "npub: ${getNpub()}")
        Log.i(TAG, "pubkey: ${getPubKey().toHex()}")
    }

    fun listenEvents() {
        try {
            client.handleNotifications(object : HandleNotification {
                override fun handle(relayUrl: String, event: Event) {
                    // todo handle events
                }

                override fun handleMsg(relayUrl: String, msg: RelayMessage) {
                    if (msg is RelayMessage.Ev) {
                        val event = Event.fromJson(msg.event)
                        if (event.kind() == 4u.toULong()) {
                            // Decode content
                            val decryptedMsg = nip04Decrypt(
                                keys.secretKey(),
                                event.pubkey(),
                                event.content()
                            )
                            Log.i(TAG, "Decrypted msg: $decryptedMsg")

                            launch {
                                messagesChannel.send(Pair(event, decryptedMsg))
                            }
                        } else if (event.kind() == 3u.toULong()) {
                            handleNip02Event(event)
                        }

                    } else if (msg is RelayMessage.Ok) {
                        Log.i(TAG, "Received OK event\n${msg.message}")
                    } else if (msg is RelayMessage.Notice) {
                        Log.i(TAG, "Received some notice\n${msg.message}")
                    } else if (msg is RelayMessage.EndOfStoredEvents) {
                        Log.i(TAG, "Received EndOfStoredEvents event\n${msg.subscriptionId}")
                    } else if (msg is RelayMessage.NegMsg) {
                        Log.i(TAG, "Received new msg ${msg.message}")
                    }
                }

            })
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
        }
    }

    fun decodeNip04Message(msg: String): String? {
        return try {
            nip04Decrypt(
                keys.secretKey(),
                getPubKey(),
                msg
            )
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage)
            null
        }
    }

    fun getEvents(vararg filters: Filter): List<Event> {
        return client.getEventsOf(filters.toList(), Duration.ofSeconds(50))
    }

    fun subscribeEvents(vararg filters: Filter) {
        client.subscribe(filters.toList()) // Warning!! Crashing
    }

    private fun subscribeMessages() {
        val privateMessagesFilter =
            Filter().kind(4u).pubkey(getPubKey()).since(Timestamp.now())

        subscribeEvents(privateMessagesFilter)
    }

    private fun subscribeContacts() {
        subscribeEvents(Filter().kind(3u).author(getPubKey()))
    }

    fun getContactList(): List<Contact> {
        val events = getEvents(Filter().kind(3u).author(getPubKey()))
        val event = events.firstOrNull()

        contacts.clear()

        event?.let { event ->
            contacts.addAll(handleNip02Event(event))

            launch {
                contactsChannel.send(contacts)
            }
        }

        return contacts
    }

    private fun handleNip02Event(event: Event): List<Contact> {
        val contacts = mutableListOf<Contact>()
        event.asJson().let {
            try {
                Log.i(TAG, it)
                contacts.addAll(event.tags().map { tag ->
                    val c = tag.asEnum() as TagEnum.ContactList
                    Contact(PublicKey.fromHex(c.pk), c.relayUrl, c.alias)
                })
            } catch (e: Exception) {
                Log.e(TAG, e.localizedMessage ?: "Unexpected error")
            }
        }

        return contacts
    }

    fun addContact(npub: String) {
        val contact = Contact(PublicKey.fromBech32(npub))

        contacts.add(contact)

        updateContacts(contacts)
    }

    fun removeContact(npub: String) {
        val index = contacts.indexOfFirst { it.publicKey().toBech32() == npub }

        contacts.removeAt(index)

        updateContacts(contacts)
    }

    private fun updateContacts(contacts: List<Contact>) {
        val event = EventBuilder.setContactList(contacts).toEvent(keys)
        client.sendEvent(event)
        Log.i(TAG, "Published contacts event:\n${event.asJson()}")
        getContactList()
    }

    fun sendNip04Message(recipientNpub: String, content: String, replyTo: String? = null) {
        val event =
            EventBuilder.newEncryptedDirectMsg(
                keys,
                PublicKey.fromBech32(recipientNpub),
                content,
                replyTo?.let { EventId.fromBech32(it) }
            ).toEvent(keys)

        publishEvent(event)
        Log.i(TAG, "Encrypted message event sent:\n${event.asJson()}")
    }

    private fun getMetadata(npub: String): Metadata? {
        try {
            val filter = Filter().author(PublicKey.fromBech32(npub)).kind(0u)
            val events = getEvents(filter)
            if (events.isEmpty())
                return null

            return Metadata.fromJson(events[0].content())
        } catch (e: Exception) {
            Log.e(TAG, "Can't get npub $npub metadata: ${e.localizedMessage}")
            return null
        }
    }

    fun getProfile(npub: String): Metadata? {
        if (!cache.containsKey(npub)) {
            getMetadata(npub)?.let {

                cache[npub] = it
            }
        }

        return if (cache.containsKey(npub)) cache[npub] else null
    }

    private fun publishEvent(event: Event) {
        client.sendEvent(event)
        client.clearAlreadySeenEvents()
    }

    private val cache: MutableMap<String, Metadata> = mutableMapOf()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job
}
