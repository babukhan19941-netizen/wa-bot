package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AutoReplyRule
import com.example.data.local.entity.BotSettings
import com.example.data.local.entity.CustomerOrder
import com.example.data.local.entity.ProductItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM bot_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<BotSettings?>

    @Query("SELECT * FROM bot_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): BotSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: BotSettings)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProductsFlow(): Flow<List<ProductItem>>

    @Query("SELECT * FROM products WHERE inStock = 1 ORDER BY id DESC")
    fun getAvailableProductsFlow(): Flow<List<ProductItem>>

    @Query("SELECT * FROM products WHERE inStock = 1 ORDER BY id DESC")
    suspend fun getAvailableProductsDirect(): List<ProductItem>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductItem>)

    @Update
    suspend fun updateProduct(product: ProductItem)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}

@Dao
interface RuleDao {
    @Query("SELECT * FROM auto_reply_rules ORDER BY id DESC")
    fun getAllRulesFlow(): Flow<List<AutoReplyRule>>

    @Query("SELECT * FROM auto_reply_rules WHERE isEnabled = 1")
    suspend fun getActiveRulesDirect(): List<AutoReplyRule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AutoReplyRule): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRules(rules: List<AutoReplyRule>)

    @Update
    suspend fun updateRule(rule: AutoReplyRule)

    @Query("DELETE FROM auto_reply_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Long)

    @Query("SELECT COUNT(*) FROM auto_reply_rules")
    suspend fun getRuleCount(): Int
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM customer_orders ORDER BY createdAt DESC")
    fun getAllOrdersFlow(): Flow<List<CustomerOrder>>

    @Query("SELECT * FROM customer_orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): CustomerOrder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: CustomerOrder): Long

    @Update
    suspend fun updateOrder(order: CustomerOrder)

    @Query("UPDATE customer_orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("DELETE FROM customer_orders WHERE id = :id")
    suspend fun deleteOrderById(id: Long)

    @Query("SELECT COUNT(*) FROM customer_orders")
    fun getOrderCountFlow(): Flow<Int>
}
