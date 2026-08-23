package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BotDatabase
import com.example.data.local.entity.BotSettings
import com.example.data.local.entity.CustomerOrder
import com.example.data.local.entity.ProductItem
import com.example.data.repository.BotRepository
import com.example.ui.viewmodel.BotViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WA Bot", appName)
  }

  @Test
  fun `test database and repository seeding`() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val db = BotDatabase.getDatabase(app)
    val repo = BotRepository(db)
    repo.seedInitialDataIfNeeded()

    val settings = repo.getSettings()
    assertNotNull(settings)
    assertTrue(settings.sellerPhone.isNotEmpty())

    val products = repo.getAvailableProducts()
    assertTrue(products.isNotEmpty())
  }

  @Test
  fun `test viewModel chat simulation`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = BotViewModel(app)
    assertNotNull(viewModel.chatMessages.value)
    assertTrue(viewModel.chatMessages.value.isNotEmpty())

    viewModel.sendBuyerMessage("Hi")
    val hasMessages = viewModel.chatMessages.value.any { it.text.contains("Hi") }
    assertTrue(hasMessages)
  }

  @Test
  fun `test activity launches without crash`() {
    val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()
    assertNotNull(activity)
  }
}
