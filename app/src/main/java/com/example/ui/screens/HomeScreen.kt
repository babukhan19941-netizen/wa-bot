package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.CustomerOrder
import com.example.ui.components.MetricStatCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.theme.WhatsAppGreenLight
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.ui.theme.WhatsAppTeal
import com.example.ui.viewmodel.BotViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BotViewModel,
    onNavigateToSimulator: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToQuickReplies: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    val totalRevenue = orders.sumOf { it.totalAmount }
    val pendingOrdersCount = orders.count { it.orderStatus.equals("Pending", ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. WhatsApp Bot Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = WhatsAppGreenDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(WhatsAppGreenDark, WhatsAppTeal, WhatsAppGreenPrimary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🤖", fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = settings.sellerName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "WhatsApp: ${settings.sellerPhone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            // Bot Active Switch
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (settings.botActive) "ON" else "OFF",
                                    color = if (settings.botActive) WhatsAppGreenLight else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = settings.botActive,
                                    onCheckedChange = { viewModel.toggleBotActive(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = WhatsAppGreenLight,
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier.testTag("bot_toggle_switch")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Test Bot Live Simulator Button
                        Button(
                            onClick = onNavigateToSimulator,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WhatsAppGreenLight,
                                contentColor = Color(0xFF00382B)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_bot_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "💬 Test Bot Live Sandbox (चैट टेस्ट करें)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "Total Orders",
                    value = "${orders.size}",
                    subValue = "$pendingOrdersCount Pending",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = WhatsAppGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = onNavigateToOrders,
                    modifier = Modifier.weight(1f)
                )

                MetricStatCard(
                    title = "Products",
                    value = "${products.size}",
                    subValue = "${products.count { it.inStock }} In Stock",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Inventory2,
                            contentDescription = null,
                            tint = WhatsAppGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = onNavigateToCatalog,
                    modifier = Modifier.weight(1f)
                )

                MetricStatCard(
                    title = "Total Sales",
                    value = "₹${totalRevenue.toInt()}",
                    subValue = "All time",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Store,
                            contentDescription = null,
                            tint = WhatsAppGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.weight(1.1f)
                )
            }
        }

        // 3. Main Action Modules
        item {
            Text(
                text = "⚡ Bot Management & Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeFeatureTile(
                    title = "1. Product Catalog & Pricing",
                    subtitle = "Manage products, prices, images, stock & units",
                    icon = Icons.Filled.Inventory2,
                    badge = "${products.size} Items",
                    onClick = onNavigateToCatalog,
                    modifier = Modifier.testTag("nav_catalog")
                )

                HomeFeatureTile(
                    title = "2. Bot Settings & Delivery",
                    subtitle = "WhatsApp number, Welcome message, COD & Delivery fees",
                    icon = Icons.Filled.Settings,
                    badge = "Configure",
                    onClick = onNavigateToSettings,
                    modifier = Modifier.testTag("nav_settings")
                )

                HomeFeatureTile(
                    title = "3. Customer Orders & Leads",
                    subtitle = "Collected addresses, quantities, total & WhatsApp dispatch",
                    icon = Icons.Filled.ReceiptLong,
                    badge = if (pendingOrdersCount > 0) "$pendingOrdersCount New" else "${orders.size} Total",
                    badgeColor = if (pendingOrdersCount > 0) Color(0xFFFF9800) else WhatsAppGreenPrimary,
                    onClick = onNavigateToOrders,
                    modifier = Modifier.testTag("nav_orders")
                )

                HomeFeatureTile(
                    title = "4. Auto-Replies & Share Links",
                    subtitle = "Keywords triggers (Discounts, Timings) & wa.me catalog links",
                    icon = Icons.Filled.AutoAwesome,
                    badge = "Tools",
                    onClick = onNavigateToQuickReplies,
                    modifier = Modifier.testTag("nav_replies")
                )
            }
        }

        // 4. Quick Share WhatsApp Catalog Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📢 Share Store on WhatsApp",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Direct WhatsApp link bhejein taaki buyer direct bot se baat kar sake.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val cleanNumber = settings.sellerPhone.replace("+", "").replace(" ", "")
                            val shareText = "नमस्ते! 🙏 हमारे स्टोर से सीधे ऑर्डर करने के लिए WhatsApp पर 'Hi' भेजें: https://wa.me/$cleanNumber?text=Hi"
                            viewModel.shareOrderText(context, shareText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Link")
                    }
                }
            }
        }

        // 5. Recent Orders Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📦 Recent Received Orders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (orders.isNotEmpty()) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium,
                        color = WhatsAppGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToOrders() }
                    )
                }
            }
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛒", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Abhi koi naya order nahi aaya hai",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Live Sandbox me jakar test order create karein!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onNavigateToSimulator,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Test Chat Bot")
                        }
                    }
                }
            }
        } else {
            items(orders.take(3)) { order ->
                RecentOrderCard(
                    order = order,
                    onOpenWhatsApp = {
                        val message = viewModel.formatOrderForWhatsApp(order, settings)
                        viewModel.openWhatsAppChat(context, order.buyerPhone.ifBlank { settings.sellerPhone }, message)
                    }
                )
            }
        }
    }
}

@Composable
fun HomeFeatureTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    badgeColor: Color = WhatsAppGreenPrimary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WhatsAppGreenPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = WhatsAppGreenPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badge,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RecentOrderCard(
    order: CustomerOrder,
    onOpenWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${order.orderNumber} • ${order.buyerName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📦 ${order.itemsSummary}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ₹${order.totalAmount.toInt()} (${order.paymentMethod})",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = WhatsAppGreenPrimary
                )

                FilledTonalButton(
                    onClick = onOpenWhatsApp,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("📲 WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
