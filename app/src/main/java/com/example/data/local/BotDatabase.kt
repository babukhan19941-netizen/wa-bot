package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.RuleDao
import com.example.data.local.dao.SettingsDao
import com.example.data.local.entity.AutoReplyRule
import com.example.data.local.entity.BotSettings
import com.example.data.local.entity.CustomerOrder
import com.example.data.local.entity.ProductItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BotSettings::class,
        ProductItem::class,
        AutoReplyRule::class,
        CustomerOrder::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BotDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao
    abstract fun productDao(): ProductDao
    abstract fun ruleDao(): RuleDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: BotDatabase? = null

        fun getDatabase(context: Context): BotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BotDatabase::class.java,
                    "wa_business_bot_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            populateInitialData(database)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        suspend fun populateInitialData(database: BotDatabase) {
            val settingsDao = database.settingsDao()
            val productDao = database.productDao()
            val ruleDao = database.ruleDao()
            val orderDao = database.orderDao()

            // 1. Initial Settings
            settingsDao.insertOrUpdateSettings(
                BotSettings(
                    id = 1,
                    sellerPhone = "+919876543210",
                    sellerName = "Babu Store & Enterprises",
                    welcomeMessage = "नमस्ते! 🙏 हमारे स्टोर में आपका स्वागत है। आप क्या ऑर्डर करना चाहते हैं? नीचे दिए गए कैटलॉग में से चुनें।",
                    catalogHeader = "🛍️ *हमारा प्रोडक्ट कैटलॉग (Products):*",
                    deliveryCharges = 40.0,
                    freeDeliveryMinAmount = 499.0,
                    estimatedDeliveryTime = "24-48 Hours (1-2 दिन)",
                    codAvailable = true,
                    upiAvailable = true,
                    upiId = "seller@upi",
                    botActive = true,
                    autoAskAddress = true,
                    autoAskPayment = true,
                    notifySellerOnComplete = true
                )
            )

            // 2. Starter Products
            val starterProducts = listOf(
                ProductItem(
                    name = "Cotton Casual T-Shirt",
                    category = "Clothing",
                    price = 399.0,
                    originalPrice = 699.0,
                    unit = "Piece",
                    description = "100% Pure Cotton, Regular Fit, Premium Comfort (M, L, XL)",
                    inStock = true,
                    iconKey = "👕",
                    deliveryNote = "1-2 Days Dispatch"
                ),
                ProductItem(
                    name = "Wireless Bluetooth Earbuds",
                    category = "Electronics",
                    price = 899.0,
                    originalPrice = 1499.0,
                    unit = "Piece",
                    description = "40H Playtime, Deep Bass, Noise Cancellation, Type-C Fast Charge",
                    inStock = true,
                    iconKey = "🎧",
                    deliveryNote = "Free Delivery"
                ),
                ProductItem(
                    name = "Running Sports Shoes",
                    category = "Footwear",
                    price = 999.0,
                    originalPrice = 1999.0,
                    unit = "Pair",
                    description = "Lightweight, Breathable Mesh, Durable Cushion Sole (Size 7-10)",
                    inStock = true,
                    iconKey = "👟",
                    deliveryNote = "Fast Delivery"
                ),
                ProductItem(
                    name = "Smart Fitness Watch",
                    category = "Electronics",
                    price = 1299.0,
                    originalPrice = 2499.0,
                    unit = "Piece",
                    description = "HD Touch Display, Heart Rate & SpO2 Monitor, 100+ Sports Modes",
                    inStock = true,
                    iconKey = "⌚",
                    deliveryNote = "Free Delivery"
                ),
                ProductItem(
                    name = "Organic Green Tea (250g)",
                    category = "Grocery",
                    price = 249.0,
                    originalPrice = 350.0,
                    unit = "Pack",
                    description = "Natural Antioxidant Rich Whole Leaf Tea, Fresh & Pure Aroma",
                    inStock = true,
                    iconKey = "🍵",
                    deliveryNote = "Express Dispatch"
                )
            )
            productDao.insertAll(starterProducts)

            // 3. Initial Auto Reply Rules
            val starterRules = listOf(
                AutoReplyRule(
                    keywords = "discount, offer, code, sale, coupon",
                    response = "🎉 *Special Offer!* ₹999 से ऊपर के ऑर्डर्स पर 10% की अतिरिक्त छूट (Extra Discount) पाएं! Code: *OFFER10*",
                    isEnabled = true
                ),
                AutoReplyRule(
                    keywords = "return, refund, exchange, replacement",
                    response = "🔄 *Return Policy:* 7 Days Easy Return & Exchange available for all damaged or incorrect items.",
                    isEnabled = true
                ),
                AutoReplyRule(
                    keywords = "timing, open, contact, address, dukan",
                    response = "⏰ *Store Timings:* Monday to Saturday: 9:00 AM - 9:00 PM. Sunday: 10:00 AM - 6:00 PM. Call / Support: +91 9876543210",
                    isEnabled = true
                )
            )
            ruleDao.insertAllRules(starterRules)

            // 4. Sample initial order
            val sampleOrder = CustomerOrder(
                orderNumber = "WA-101",
                buyerName = "Rahul Sharma",
                buyerPhone = "+919811223344",
                deliveryAddress = "Flat 402, Green Valley Apts, Sector 14, Gurugram, Haryana",
                pincode = "122001",
                itemsSummary = "Cotton Casual T-Shirt (x1) - ₹399",
                subtotal = 399.0,
                deliveryFee = 40.0,
                totalAmount = 439.0,
                paymentMethod = "Cash on Delivery (COD)",
                orderStatus = "Pending",
                createdAt = System.currentTimeMillis() - 3600000L,
                notes = "Auto-collected via WhatsApp Bot"
            )
            orderDao.insertOrder(sampleOrder)
        }
    }
}
