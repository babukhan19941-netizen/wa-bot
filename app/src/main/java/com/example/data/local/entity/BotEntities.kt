package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bot_settings")
data class BotSettings(
    @PrimaryKey val id: Int = 1,
    val sellerPhone: String = "+919876543210",
    val sellerName: String = "My Store",
    val welcomeMessage: String = "नमस्ते! 🙏 हमारे स्टोर में आपका स्वागत है। आप क्या ऑर्डर करना चाहते हैं? नीचे दिए गए कैटलॉग में से चुनें।",
    val catalogHeader: String = "🛍️ *हमारा प्रोडक्ट कैटलॉग (Product Catalog):*",
    val deliveryCharges: Double = 40.0,
    val freeDeliveryMinAmount: Double = 499.0,
    val estimatedDeliveryTime: String = "24-48 Hours (1-2 दिन)",
    val codAvailable: Boolean = true,
    val upiAvailable: Boolean = true,
    val upiId: String = "store@upi",
    val botActive: Boolean = true,
    val autoAskAddress: Boolean = true,
    val autoAskPayment: Boolean = true,
    val notifySellerOnComplete: Boolean = true
)

@Entity(tableName = "products")
data class ProductItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "General",
    val price: Double,
    val originalPrice: Double = 0.0,
    val unit: String = "Piece",
    val description: String = "",
    val inStock: Boolean = true,
    val iconKey: String = "🛍️",
    val deliveryNote: String = "Fast Dispatch"
)

@Entity(tableName = "auto_reply_rules")
data class AutoReplyRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keywords: String, // comma separated keywords like "offer, discount, sale"
    val response: String,
    val isEnabled: Boolean = true
)

@Entity(tableName = "customer_orders")
data class CustomerOrder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val buyerName: String,
    val buyerPhone: String,
    val deliveryAddress: String,
    val pincode: String = "",
    val itemsSummary: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val orderStatus: String = "Pending", // Pending, Confirmed, Shipped, Delivered, Cancelled
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class MessageSender {
    BUYER,
    BOT,
    SYSTEM
}

data class BotChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val quickOptions: List<String> = emptyList(),
    val isOrderSummary: Boolean = false,
    val orderPayload: CustomerOrder? = null
)
