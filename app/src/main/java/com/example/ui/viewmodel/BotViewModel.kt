package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BotDatabase
import com.example.data.local.entity.AutoReplyRule
import com.example.data.local.entity.BotChatMessage
import com.example.data.local.entity.BotSettings
import com.example.data.local.entity.CustomerOrder
import com.example.data.local.entity.MessageSender
import com.example.data.local.entity.ProductItem
import com.example.data.repository.BotRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

enum class BotStep {
    IDLE,
    AWAITING_PRODUCT_CHOICE,
    AWAITING_QUANTITY,
    AWAITING_ADDRESS,
    AWAITING_PAYMENT_METHOD,
    AWAITING_CONFIRMATION,
    ORDER_COMPLETED
}

data class CurrentOrderDraft(
    val selectedProduct: ProductItem? = null,
    val quantity: Int = 1,
    val buyerName: String = "",
    val buyerPhone: String = "",
    val deliveryAddress: String = "",
    val pincode: String = "",
    val paymentMethod: String = "Cash on Delivery (COD)"
)

class BotViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BotDatabase.getDatabase(application)
    private val repository = BotRepository(db)

    val settings: StateFlow<BotSettings> = repository.settingsFlow
        .map { it ?: BotSettings() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BotSettings()
        )

    val products: StateFlow<List<ProductItem>> = repository.productsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val autoReplyRules: StateFlow<List<AutoReplyRule>> = repository.rulesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val orders: StateFlow<List<CustomerOrder>> = repository.ordersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Chat Simulator State
    private val _chatMessages = MutableStateFlow<List<BotChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<BotChatMessage>> = _chatMessages.asStateFlow()

    private val _currentStep = MutableStateFlow(BotStep.IDLE)
    val currentStep: StateFlow<BotStep> = _currentStep.asStateFlow()

    private val _orderDraft = MutableStateFlow(CurrentOrderDraft())
    val orderDraft: StateFlow<CurrentOrderDraft> = _orderDraft.asStateFlow()

    private val _isBotTyping = MutableStateFlow(false)
    val isBotTyping: StateFlow<Boolean> = _isBotTyping.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    init {
        initializeBotChat()
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun showSnackBar(msg: String) {
        _snackBarMessage.value = msg
    }

    // --- Settings Actions ---
    fun updateSettings(newSettings: BotSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
            showSnackBar("Settings saved successfully!")
        }
    }

    fun toggleBotActive(active: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            repository.saveSettings(current.copy(botActive = active))
            showSnackBar(if (active) "Bot is now Active 🟢" else "Bot is Paused ⏸️")
        }
    }

    // --- Product Actions ---
    fun addProduct(product: ProductItem) {
        viewModelScope.launch {
            repository.addProduct(product)
            showSnackBar("Product added to catalog!")
        }
    }

    fun updateProduct(product: ProductItem) {
        viewModelScope.launch {
            repository.updateProduct(product)
            showSnackBar("Product updated!")
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
            showSnackBar("Product deleted.")
        }
    }

    // --- Auto Reply Rules ---
    fun addRule(rule: AutoReplyRule) {
        viewModelScope.launch {
            repository.addRule(rule)
            showSnackBar("Auto-reply rule added!")
        }
    }

    fun updateRule(rule: AutoReplyRule) {
        viewModelScope.launch {
            repository.updateRule(rule)
            showSnackBar("Rule updated!")
        }
    }

    fun deleteRule(id: Long) {
        viewModelScope.launch {
            repository.deleteRule(id)
            showSnackBar("Rule deleted.")
        }
    }

    // --- Order Actions ---
    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            showSnackBar("Order status changed to $status")
        }
    }

    fun deleteOrder(id: Long) {
        viewModelScope.launch {
            repository.deleteOrder(id)
            showSnackBar("Order deleted.")
        }
    }

    // --- Bot Chat Simulator Engine ---
    fun initializeBotChat() {
        _chatMessages.value = listOf(
            BotChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.SYSTEM,
                text = "💬 WhatsApp Bot Live Sandbox\n(Niche 'Hi' bhej kar ya button dabakar bot test karein)",
                quickOptions = listOf("Hi", "Hello", "View Catalog 🛍️", "Special Offers 🎉")
            )
        )
        _currentStep.value = BotStep.IDLE
        _orderDraft.value = CurrentOrderDraft()
    }

    fun resetChat() {
        initializeBotChat()
    }

    fun sendBuyerMessage(input: String) {
        val cleanInput = input.trim()
        if (cleanInput.isEmpty()) return

        // Add buyer message to chat
        val buyerMsg = BotChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.BUYER,
            text = cleanInput
        )
        _chatMessages.value = _chatMessages.value + buyerMsg

        viewModelScope.launch {
            _isBotTyping.value = true
            delay(600) // realistic typing delay
            _isBotTyping.value = false
            processBotResponse(cleanInput)
        }
    }

    private suspend fun processBotResponse(input: String) {
        val currentSett = repository.getSettings()
        if (!currentSett.botActive) {
            addBotMessage("⏸️ *Bot is currently paused.* Seller will reply to you shortly.")
            return
        }

        val lower = input.lowercase()
        val step = _currentStep.value
        val availableProdList = repository.getAvailableProducts()
        val activeRules = repository.getActiveRules()

        // Check custom keyword rules first if user asked specific queries
        val matchedRule = activeRules.firstOrNull { rule ->
            val keywords = rule.keywords.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
            keywords.any { kw -> lower.contains(kw) }
        }

        if (matchedRule != null && step == BotStep.IDLE) {
            addBotMessage(
                text = matchedRule.response,
                quickOptions = listOf("🛍️ View Products", "Order Now", "Help")
            )
            return
        }

        when (step) {
            BotStep.IDLE -> {
                // Greeting / Welcome flow
                val welcome = currentSett.welcomeMessage.ifBlank {
                    "नमस्ते! 🙏 हमारे स्टोर में आपका स्वागत है।"
                }

                val catalogText = buildCatalogString(currentSett, availableProdList)
                val fullWelcome = "$welcome\n\n$catalogText"

                val productOptions = availableProdList.map { "${it.iconKey} ${it.name} - ₹${it.price.toInt()}" }
                    .take(4)

                addBotMessage(
                    text = fullWelcome,
                    quickOptions = if (productOptions.isNotEmpty()) productOptions else listOf("Hi", "Help")
                )
                _currentStep.value = BotStep.AWAITING_PRODUCT_CHOICE
            }

            BotStep.AWAITING_PRODUCT_CHOICE -> {
                // Find product by name or index
                var selected: ProductItem? = null

                // Match with available products
                for (p in availableProdList) {
                    if (lower.contains(p.name.lowercase()) || input.contains(p.name)) {
                        selected = p
                        break
                    }
                }

                // If not matched by name, check if user entered number 1, 2, 3...
                if (selected == null && lower.toIntOrNull() != null) {
                    val idx = (lower.toIntOrNull() ?: 1) - 1
                    if (idx in availableProdList.indices) {
                        selected = availableProdList[idx]
                    }
                }

                if (selected != null) {
                    _orderDraft.value = _orderDraft.value.copy(selectedProduct = selected)
                    val prodMsg = "✅ *${selected.name}* chuna gaya!\n" +
                            "💵 Price: *₹${selected.price.toInt()}* per ${selected.unit}\n" +
                            (if (selected.description.isNotBlank()) "📝 ${selected.description}\n" else "") +
                            "\n🔢 *Aapko kitni quantity (मात्रा) chahiye?* (1, 2, 3...)"

                    addBotMessage(
                        text = prodMsg,
                        quickOptions = listOf("1", "2", "3", "5", "❌ Cancel")
                    )
                    _currentStep.value = BotStep.AWAITING_QUANTITY
                } else if (lower.contains("hi") || lower.contains("hello") || lower.contains("menu") || lower.contains("catalog")) {
                    val catalogText = buildCatalogString(currentSett, availableProdList)
                    addBotMessage(
                        text = "🛍️ *Catalog:* \n\n$catalogText\n\nKripya kisi product ka naam ya number chunein:",
                        quickOptions = availableProdList.map { "${it.iconKey} ${it.name}" }.take(4)
                    )
                } else {
                    addBotMessage(
                        text = "Maaf kijiye, mujhe yeh product nahi mila. Kripya niche diye gaye options me se chunein:",
                        quickOptions = availableProdList.map { "${it.iconKey} ${it.name}" }.take(4)
                    )
                }
            }

            BotStep.AWAITING_QUANTITY -> {
                if (lower.contains("cancel") || lower.contains("radd")) {
                    _currentStep.value = BotStep.IDLE
                    _orderDraft.value = CurrentOrderDraft()
                    addBotMessage("❌ Order cancel kar diya gaya hai. Dobara shuru karne ke liye 'Hi' likhein.", listOf("Hi"))
                    return
                }

                val qty = lower.filter { it.isDigit() }.toIntOrNull() ?: 1
                val product = _orderDraft.value.selectedProduct ?: availableProdList.firstOrNull()

                if (product != null) {
                    _orderDraft.value = _orderDraft.value.copy(quantity = qty)
                    val subtotal = product.price * qty
                    val deliveryFee = if (subtotal >= currentSett.freeDeliveryMinAmount) 0.0 else currentSett.deliveryCharges
                    val total = subtotal + deliveryFee

                    val priceCalcMsg = "📊 *Order Details:*\n" +
                            "• ${product.name} x $qty = ₹${subtotal.toInt()}\n" +
                            "• Delivery Charges = ${if (deliveryFee == 0.0) "FREE 🚚" else "₹${deliveryFee.toInt()}"}\n" +
                            "💰 *Total Payable: ₹${total.toInt()}*\n\n" +
                            "📍 *Kripya Delivery Details likh kar bhejiye:*\n" +
                            "1. Aapka Naam (Full Name)\n" +
                            "2. Mobile Number\n" +
                            "3. Delivery Address with Pincode"

                    addBotMessage(
                        text = priceCalcMsg,
                        quickOptions = listOf("Ravi Kumar, 9876543210, H.No 12, Main Road, Delhi - 110001", "❌ Cancel")
                    )
                    _currentStep.value = BotStep.AWAITING_ADDRESS
                } else {
                    _currentStep.value = BotStep.IDLE
                    addBotMessage("Kuch samasya aayi. Kripya 'Hi' bhej kar dobara shuru karein.", listOf("Hi"))
                }
            }

            BotStep.AWAITING_ADDRESS -> {
                if (lower.contains("cancel")) {
                    _currentStep.value = BotStep.IDLE
                    _orderDraft.value = CurrentOrderDraft()
                    addBotMessage("❌ Order cancel kar diya gaya. Naya order ke liye 'Hi' bhein.", listOf("Hi"))
                    return
                }

                // Extract name / phone / address loosely
                val fullAddress = input.trim()
                var buyerName = "Customer"
                var buyerPhone = ""

                val parts = fullAddress.split(",")
                if (parts.size >= 2) {
                    buyerName = parts[0].trim()
                    buyerPhone = parts.getOrNull(1)?.filter { it.isDigit() } ?: ""
                }

                _orderDraft.value = _orderDraft.value.copy(
                    buyerName = buyerName,
                    buyerPhone = buyerPhone,
                    deliveryAddress = fullAddress
                )

                val paymentMsg = "✅ Address note kar liya gaya hai!\n\n" +
                        "💳 *Payment Mode chunein:*\n" +
                        "1️⃣ Cash on Delivery (COD)\n" +
                        "2️⃣ UPI / Online Payment"

                val paymentOptions = mutableListOf<String>()
                if (currentSett.codAvailable) paymentOptions.add("💵 Cash on Delivery (COD)")
                if (currentSett.upiAvailable) paymentOptions.add("📱 UPI / Online Payment")
                paymentOptions.add("❌ Cancel")

                addBotMessage(
                    text = paymentMsg,
                    quickOptions = paymentOptions
                )
                _currentStep.value = BotStep.AWAITING_PAYMENT_METHOD
            }

            BotStep.AWAITING_PAYMENT_METHOD -> {
                if (lower.contains("cancel")) {
                    _currentStep.value = BotStep.IDLE
                    _orderDraft.value = CurrentOrderDraft()
                    addBotMessage("❌ Order cancel kar diya gaya. 'Hi' bhej kar shuru karein.", listOf("Hi"))
                    return
                }

                val paymentMethod = if (lower.contains("upi") || lower.contains("online") || lower.contains("qr")) {
                    "UPI / Online Payment"
                } else {
                    "Cash on Delivery (COD)"
                }

                _orderDraft.value = _orderDraft.value.copy(paymentMethod = paymentMethod)

                val draft = _orderDraft.value
                val prod = draft.selectedProduct ?: availableProdList.first()
                val subtotal = prod.price * draft.quantity
                val deliveryFee = if (subtotal >= currentSett.freeDeliveryMinAmount) 0.0 else currentSett.deliveryCharges
                val total = subtotal + deliveryFee

                val summaryMsg = "🧾 *FINAL ORDER SUMMARY*\n" +
                        "━━━━━━━━━━━━━━━━━━\n" +
                        "🛍️ *Product:* ${prod.name} (${draft.quantity} ${prod.unit})\n" +
                        "💵 *Subtotal:* ₹${subtotal.toInt()}\n" +
                        "🚚 *Delivery:* ${if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}"}\n" +
                        "💰 *Grand Total:* ₹${total.toInt()}\n" +
                        "💳 *Payment:* $paymentMethod\n" +
                        (if (paymentMethod.contains("UPI") && currentSett.upiId.isNotBlank()) "📲 UPI ID: ${currentSett.upiId}\n" else "") +
                        "📍 *Address:* ${draft.deliveryAddress}\n" +
                        "━━━━━━━━━━━━━━━━━━\n\n" +
                        "👉 Order Confirm karne ke liye *'Confirm'* ya *'Done'* dabayein."

                addBotMessage(
                    text = summaryMsg,
                    quickOptions = listOf("✅ Confirm / Done", "❌ Cancel")
                )
                _currentStep.value = BotStep.AWAITING_CONFIRMATION
            }

            BotStep.AWAITING_CONFIRMATION -> {
                if (lower.contains("done") || lower.contains("confirm") || lower.contains("yes") || lower.contains("haan") || lower.contains("ok")) {
                    // Complete and save order
                    val draft = _orderDraft.value
                    val prod = draft.selectedProduct ?: availableProdList.first()
                    val subtotal = prod.price * draft.quantity
                    val deliveryFee = if (subtotal >= currentSett.freeDeliveryMinAmount) 0.0 else currentSett.deliveryCharges
                    val total = subtotal + deliveryFee

                    val orderNumber = "WA-" + (1000 + (1..9000).random())
                    val newOrder = CustomerOrder(
                        orderNumber = orderNumber,
                        buyerName = draft.buyerName.ifBlank { "WhatsApp Buyer" },
                        buyerPhone = draft.buyerPhone.ifBlank { "+91XXXXXXXXXX" },
                        deliveryAddress = draft.deliveryAddress,
                        pincode = draft.pincode,
                        itemsSummary = "${prod.name} (x${draft.quantity})",
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        totalAmount = total,
                        paymentMethod = draft.paymentMethod,
                        orderStatus = "Pending",
                        createdAt = System.currentTimeMillis(),
                        notes = "Auto-collected via WhatsApp Bot"
                    )

                    repository.saveOrder(newOrder)

                    val confirmationMsg = "🎉 *Shandaar! Aapka Order Confirm ho gaya hai!*\n\n" +
                            "📋 Order ID: *$orderNumber*\n" +
                            "⏱️ Estimated Delivery: ${currentSett.estimatedDeliveryTime}\n" +
                            "📞 Seller: ${currentSett.sellerName} (${currentSett.sellerPhone})\n\n" +
                            "Aapke order ki sari details Seller ko bhej di gayi hain. Dhanyawad! 🙏"

                    addBotMessage(
                        text = confirmationMsg,
                        quickOptions = listOf("📲 Open in WhatsApp", "Naya Order Karein 🛍️"),
                        isOrderSummary = true,
                        orderPayload = newOrder
                    )

                    _currentStep.value = BotStep.ORDER_COMPLETED
                } else {
                    _currentStep.value = BotStep.IDLE
                    _orderDraft.value = CurrentOrderDraft()
                    addBotMessage("❌ Order cancel ho gaya. Naya order ke liye 'Hi' likhein.", listOf("Hi"))
                }
            }

            BotStep.ORDER_COMPLETED -> {
                if (lower.contains("naya") || lower.contains("new") || lower.contains("hi") || lower.contains("order")) {
                    _currentStep.value = BotStep.IDLE
                    _orderDraft.value = CurrentOrderDraft()
                    processBotResponse("Hi")
                } else {
                    addBotMessage(
                        text = "Aapka order process ho raha hai. Kisi aur sahayata ke liye seller se sampark karein: ${currentSett.sellerPhone}",
                        quickOptions = listOf("Naya Order Karein 🛍️", "Hi")
                    )
                }
            }
        }
    }

    private fun addBotMessage(
        text: String,
        quickOptions: List<String> = emptyList(),
        isOrderSummary: Boolean = false,
        orderPayload: CustomerOrder? = null
    ) {
        val botMsg = BotChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.BOT,
            text = text,
            quickOptions = quickOptions,
            isOrderSummary = isOrderSummary,
            orderPayload = orderPayload
        )
        _chatMessages.value = _chatMessages.value + botMsg
    }

    private fun buildCatalogString(settings: BotSettings, productList: List<ProductItem>): String {
        if (productList.isEmpty()) {
            return "Filhaal koi product uplabdh nahi hai."
        }
        val sb = StringBuilder()
        sb.append(settings.catalogHeader).append("\n")
        productList.forEachIndexed { index, item ->
            sb.append("${index + 1}. ${item.iconKey} *${item.name}* - ₹${item.price.toInt()}/${item.unit}")
            if (item.originalPrice > item.price) {
                sb.append(" ~(₹${item.originalPrice.toInt()})~")
            }
            if (item.description.isNotBlank()) {
                sb.append("\n   _${item.description}_")
            }
            sb.append("\n")
        }
        sb.append("\n🚚 Delivery: ₹${settings.deliveryCharges.toInt()} (Free above ₹${settings.freeDeliveryMinAmount.toInt()})")
        return sb.toString()
    }

    // --- Helper to Launch WhatsApp with formatted order or message ---
    fun openWhatsAppChat(context: Context, phone: String, messageText: String) {
        try {
            val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
            val encodedMsg = URLEncoder.encode(messageText, StandardCharsets.UTF_8.toString())
            val uri = Uri.parse("https://wa.me/$cleanPhone?text=$encodedMsg")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            showSnackBar("Could not open WhatsApp. Please check if WhatsApp is installed.")
        }
    }

    fun shareOrderText(context: Context, orderText: String) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, orderText)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Order via").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            showSnackBar("Sharing not available on this device.")
        }
    }

    fun formatOrderForWhatsApp(order: CustomerOrder, settings: BotSettings): String {
        return "🛍️ *NEW ORDER RECEIVED - ${order.orderNumber}*\n" +
                "━━━━━━━━━━━━━━━━━━━━\n" +
                "👤 *Customer:* ${order.buyerName}\n" +
                "📞 *Phone:* ${order.buyerPhone}\n" +
                "📍 *Delivery Address:*\n${order.deliveryAddress}\n" +
                "━━━━━━━━━━━━━━━━━━━━\n" +
                "📦 *Items:* ${order.itemsSummary}\n" +
                "💵 *Subtotal:* ₹${order.subtotal.toInt()}\n" +
                "🚚 *Delivery:* ₹${order.deliveryFee.toInt()}\n" +
                "💰 *Total Amount:* ₹${order.totalAmount.toInt()}\n" +
                "💳 *Payment Mode:* ${order.paymentMethod}\n" +
                "━━━━━━━━━━━━━━━━━━━━\n" +
                "🤖 *Sent automatically by WhatsApp Bot*"
    }
}
