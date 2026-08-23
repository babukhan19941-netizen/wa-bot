package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.BotSetupScreen
import com.example.ui.screens.BotSimulatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProductCatalogScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.QuickRepliesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.theme.WhatsAppGreenLight
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.ui.viewmodel.BotViewModel

enum class AppDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    CATALOG("Products", Icons.Filled.Inventory2, Icons.Outlined.Inventory2, "tab_catalog"),
    SIMULATOR("Live Bot", Icons.Filled.Chat, Icons.Outlined.Chat, "tab_simulator"),
    ORDERS("Orders", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "tab_orders"),
    SETTINGS("Setup", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
}

enum class SubScreen {
    NONE,
    QUICK_REPLIES
}

class MainActivity : ComponentActivity() {

    private val viewModel: BotViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val snackBarMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()
                val settings by viewModel.settings.collectAsStateWithLifecycle()
                val orders by viewModel.orders.collectAsStateWithLifecycle()

                var currentDestination by rememberSaveable { mutableStateOf(AppDestination.HOME) }
                var currentSubScreen by rememberSaveable { mutableStateOf(SubScreen.NONE) }

                val pendingOrdersCount = orders.count { it.orderStatus.equals("Pending", ignoreCase = true) }

                LaunchedEffect(snackBarMessage) {
                    snackBarMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearSnackBar()
                    }
                }

                // Handle system back navigation
                BackHandler(enabled = currentSubScreen != SubScreen.NONE || currentDestination != AppDestination.HOME) {
                    if (currentSubScreen != SubScreen.NONE) {
                        currentSubScreen = SubScreen.NONE
                    } else if (currentDestination != AppDestination.HOME) {
                        currentDestination = AppDestination.HOME
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        if (currentSubScreen == SubScreen.NONE && currentDestination == AppDestination.HOME) {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(WhatsAppGreenLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🤖", fontSize = 18.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "WA Bot & Order Manager",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (settings.botActive) "🟢 Bot Active • ${settings.sellerPhone}" else "⏸️ Bot Paused",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { currentDestination = AppDestination.SIMULATOR },
                                        modifier = Modifier.testTag("top_test_bot_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = "Test Bot",
                                            tint = WhatsAppGreenLight
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppGreenDark)
                            )
                        }
                    },
                    bottomBar = {
                        if (currentSubScreen == SubScreen.NONE && currentDestination != AppDestination.SIMULATOR) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 3.dp
                            ) {
                                AppDestination.entries.forEach { destination ->
                                    val isSelected = currentDestination == destination
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentDestination = destination },
                                        icon = {
                                            if (destination == AppDestination.ORDERS && pendingOrdersCount > 0) {
                                                BadgedBox(
                                                    badge = {
                                                        Badge(
                                                            containerColor = Color(0xFFFF9800),
                                                            contentColor = Color.White
                                                        ) {
                                                            Text("$pendingOrdersCount")
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                                        contentDescription = destination.title
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                                    contentDescription = destination.title
                                                )
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = destination.title,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = WhatsAppGreenDark,
                                            selectedTextColor = WhatsAppGreenDark,
                                            indicatorColor = WhatsAppGreenPrimary.copy(alpha = 0.2f),
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag(destination.testTag)
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = Pair(currentDestination, currentSubScreen),
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "ScreenTransition"
                        ) { (dest, sub) ->
                            when {
                                sub == SubScreen.QUICK_REPLIES -> {
                                    QuickRepliesScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { currentSubScreen = SubScreen.NONE }
                                    )
                                }
                                dest == AppDestination.HOME -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToSimulator = { currentDestination = AppDestination.SIMULATOR },
                                        onNavigateToCatalog = { currentDestination = AppDestination.CATALOG },
                                        onNavigateToSettings = { currentDestination = AppDestination.SETTINGS },
                                        onNavigateToOrders = { currentDestination = AppDestination.ORDERS },
                                        onNavigateToQuickReplies = { currentSubScreen = SubScreen.QUICK_REPLIES }
                                    )
                                }
                                dest == AppDestination.CATALOG -> {
                                    ProductCatalogScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { currentDestination = AppDestination.HOME }
                                    )
                                }
                                dest == AppDestination.SIMULATOR -> {
                                    BotSimulatorScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { currentDestination = AppDestination.HOME }
                                    )
                                }
                                dest == AppDestination.ORDERS -> {
                                    OrdersScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { currentDestination = AppDestination.HOME },
                                        onNavigateToSimulator = { currentDestination = AppDestination.SIMULATOR }
                                    )
                                }
                                dest == AppDestination.SETTINGS -> {
                                    BotSetupScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { currentDestination = AppDestination.HOME },
                                        onNavigateToSimulator = { currentDestination = AppDestination.SIMULATOR }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
