package com.walletka.app.wallet

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lightningdevkit.ldknode.Bolt11Invoice
import org.lightningdevkit.ldknode.ChannelConfig
import org.lightningdevkit.ldknode.ChannelDetails
import org.lightningdevkit.ldknode.NetAddress
import org.lightningdevkit.ldknode.PaymentDetails
import org.lightningdevkit.ldknode.PaymentHash
import org.lightningdevkit.ldknode.PeerDetails
import org.lightningdevkit.ldknode.PublicKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class LightningWallet @Inject constructor(
    private val lightningNodeFactory: LightningNodeFactory
) : CoroutineScope {
    private val _events = LightningWalletEvents()
    val events = _events.events


    private val TAG = "Lightning wallet"

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    val node by lazy {
        lightningNodeFactory.instance
    }

    private val _channels: MutableStateFlow<List<ChannelDetails>> by lazy { MutableStateFlow(node.listChannels()) }
    val channels by lazy {  _channels.asStateFlow() }

    private val _peers: MutableStateFlow<List<PeerDetails>> by lazy { MutableStateFlow(node.listPeers()) }
    val peers by lazy {  _peers.asStateFlow() }

    private val _transactions: MutableStateFlow<List<PaymentDetails>> by lazy { MutableStateFlow(node.listPayments()) }
    val transactions by lazy {  _transactions.asStateFlow() }

    private val _spendableBalance: MutableStateFlow<ULong> by lazy {  MutableStateFlow(getSpendableBalance()) }
    val spendableBalance by lazy {  _spendableBalance.asStateFlow() }

    fun start() {
        launch {
            refreshDataLoop()
        }
        launch(Dispatchers.Default) {
            listenEvents()
        }
    }

    fun createInvoice(
        amount: ULong? = null,
        description: String = "",
        expiration: UInt = 86_400u
    ): Bolt11Invoice {
        if (amount == null) {
            return node.receiveVariableAmountPayment(description, expiration)
        }
        return node.receivePayment(amount, description, expiration)
    }

    suspend fun openChannel(
        nodeId: PublicKey,
        address: NetAddress,
        amountSats: ULong,
        pushAmountMsats: ULong = 0u,
        announce: Boolean = true
    ) {
        withContext(Dispatchers.IO) {
            val channelConfig = ChannelConfig()
            channelConfig.setAcceptUnderpayingHtlcs(true)

            node.connectOpenChannel(
                nodeId, address, amountSats, pushAmountMsats, channelConfig, announce
            )
        }
    }

    suspend fun closeChannel(channelId: String, nodeId: String) = withContext(Dispatchers.IO) {
        node.closeChannel(channelId, nodeId)
    }

    suspend fun connectPeer(nodeId: PublicKey, address: NetAddress, persist: Boolean = true) {
        withContext(Dispatchers.IO) {
            node.connect(nodeId, address, persist)
        }
    }

    suspend fun payInvoice(invoice: Bolt11Invoice, amount: ULong? = null): PaymentHash =
        withContext(Dispatchers.IO) {
            if (amount == null) {
                return@withContext node.sendPayment(invoice)
            } else {
                return@withContext node.sendPaymentUsingAmount(invoice, amount)
            }
        }

    private fun refresh() {
        var refreshed = false

        val channels = node.listChannels()
        if (_channels.value != channels) {
            _channels.value = channels
            refreshed = true
        }

        val spendableBalance = getSpendableBalance()
        if (_spendableBalance.value != spendableBalance) {
            _spendableBalance.value = spendableBalance
            refreshed = true
        }

        val peers = getPeers()
        if (_peers.value != peers) {
            _peers.value = getPeers()
            refreshed = true
        }

        val payments = getPayments()
        if (_transactions.value != payments){
            _transactions.value = payments
            refreshed = true
        }

        if (refreshed) {
            launch {
                _events.invokeEvent(LightningWalletEvent.Synced)
            }
        }
    }

    private suspend fun refreshDataLoop() {
        Log.d(TAG, "Starting refreshing data")
        while (true) {
            refresh()

            delay(3000)
        }
    }

    private suspend fun listenEvents() {
        Log.d(TAG, "Listening to the events")
        while (true) {
            if (node.nextEvent() == null) {
                delay(1000)
            }
            node.nextEvent()?.let { event ->
                when (event) {
                    //is Event.PaymentSuccessful -> TODO()
                    //is Event.PaymentFailed -> TODO()
                    //is Event.PaymentReceived -> TODO()
                    //is Event.ChannelPending -> TODO()
                    //is Event.ChannelReady -> TODO()
                    //is Event.ChannelClosed -> TODO()
                    else -> {
                        Log.d(TAG, "Unexpected event $event")
                    }
                }
                refresh()
                node.eventHandled()
            }
        }
    }

    fun getSpendableBalance(): ULong {
        val chans = node.listChannels().filter { channel ->
            channel.isUsable
        }
        val balance = chans.sumOf { channel -> channel.outboundCapacityMsat }
        return balance
    }

    fun getPeers(): List<PeerDetails> {
        return node.listPeers()
    }

    fun getPayments(): List<PaymentDetails> {
        _transactions.value = node.listPayments()
        return _transactions.value
    }

    fun stop() {
        node.stop()
        job.cancel()
    }
}

class LightningWalletEvents {
    private val _events = MutableSharedFlow<LightningWalletEvent>()
    val events = _events.asSharedFlow()

    suspend fun invokeEvent(event: LightningWalletEvent) = _events.emit(event)
}

sealed class LightningWalletEvent {
    object Synced : LightningWalletEvent()
    data class TransactionSent(val txId: String) : LightningWalletEvent()
}
