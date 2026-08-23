package com.example.data.repository

import com.example.data.local.BotDatabase
import com.example.data.local.entity.AutoReplyRule
import com.example.data.local.entity.BotSettings
import com.example.data.local.entity.CustomerOrder
import com.example.data.local.entity.ProductItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BotRepository(private val database: BotDatabase) {

    private val settingsDao = database.settingsDao()
    private val productDao = database.productDao()
    private val ruleDao = database.ruleDao()
    private val orderDao = database.orderDao()

    val settingsFlow: Flow<BotSettings?> = settingsDao.getSettingsFlow()
    val productsFlow: Flow<List<ProductItem>> = productDao.getAllProductsFlow()
    val availableProductsFlow: Flow<List<ProductItem>> = productDao.getAvailableProductsFlow()
    val rulesFlow: Flow<List<AutoReplyRule>> = ruleDao.getAllRulesFlow()
    val ordersFlow: Flow<List<CustomerOrder>> = orderDao.getAllOrdersFlow()

    suspend fun getSettings(): BotSettings = withContext(Dispatchers.IO) {
        val existing = settingsDao.getSettingsDirect()
        if (existing != null) {
            existing
        } else {
            val defaultSettings = BotSettings()
            settingsDao.insertOrUpdateSettings(defaultSettings)
            defaultSettings
        }
    }

    suspend fun saveSettings(settings: BotSettings) = withContext(Dispatchers.IO) {
        settingsDao.insertOrUpdateSettings(settings)
    }

    suspend fun addProduct(product: ProductItem): Long = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductItem) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(id: Long) = withContext(Dispatchers.IO) {
        productDao.deleteProductById(id)
    }

    suspend fun getAvailableProducts(): List<ProductItem> = withContext(Dispatchers.IO) {
        productDao.getAvailableProductsDirect()
    }

    suspend fun addRule(rule: AutoReplyRule): Long = withContext(Dispatchers.IO) {
        ruleDao.insertRule(rule)
    }

    suspend fun updateRule(rule: AutoReplyRule) = withContext(Dispatchers.IO) {
        ruleDao.updateRule(rule)
    }

    suspend fun deleteRule(id: Long) = withContext(Dispatchers.IO) {
        ruleDao.deleteRuleById(id)
    }

    suspend fun getActiveRules(): List<AutoReplyRule> = withContext(Dispatchers.IO) {
        ruleDao.getActiveRulesDirect()
    }

    suspend fun saveOrder(order: CustomerOrder): Long = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun deleteOrder(id: Long) = withContext(Dispatchers.IO) {
        orderDao.deleteOrderById(id)
    }

    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        try {
            val count = productDao.getProductCount()
            val settings = settingsDao.getSettingsDirect()
            if (count == 0 || settings == null) {
                BotDatabase.populateInitialData(database)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
