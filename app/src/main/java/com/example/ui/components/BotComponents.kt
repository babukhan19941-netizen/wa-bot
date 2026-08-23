package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BotChatMessage
import com.example.data.local.entity.MessageSender
import com.example.ui.theme.OrderCancelledRed
import com.example.ui.theme.OrderConfirmedBlue
import com.example.ui.theme.OrderDeliveredGreen
import com.example.ui.theme.OrderPendingOrange
import com.example.ui.theme.WhatsAppBlueCheck
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.theme.WhatsAppGreenLight
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.ui.theme.WhatsAppIncomingBubbleDark
import com.example.ui.theme.WhatsAppIncomingBubbleLight
import com.example.ui.theme.WhatsAppOutgoingBubbleDark
import com.example.ui.theme.WhatsAppOutgoingBubbleLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status.lowercase()) {
        "confirmed" -> Triple(OrderConfirmedBlue.copy(alpha = 0.15f), OrderConfirmedBlue, Icons.Outlined.CheckCircle)
        "shipped" -> Triple(Color(0xFF9C27B0).copy(alpha = 0.15f), Color(0xFF9C27B0), Icons.Outlined.LocalShipping)
        "delivered" -> Triple(OrderDeliveredGreen.copy(alpha = 0.15f), OrderDeliveredGreen, Icons.Filled.Check)
        "cancelled" -> Triple(OrderCancelledRed.copy(alpha = 0.15f), OrderCancelledRed, Icons.Outlined.Pending)
        else -> Triple(OrderPendingOrange.copy(alpha = 0.15f), OrderPendingOrange, Icons.Outlined.Pending)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatBubbleItem(
    message: BotChatMessage,
    onOptionSelected: (String) -> Unit,
    onOpenWhatsApp: (() -> Unit)? = null,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeString = timeFormatter.format(Date(message.timestamp))

    if (message.sender == MessageSender.SYSTEM) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = if (isDarkTheme) Color(0xFF1E2A30) else Color(0xFFFFE082).copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color(0xFFE9EDEF) else Color(0xFF4A4A4A),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        return
    }

    val isBuyer = message.sender == MessageSender.BUYER

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = if (isBuyer) Alignment.End else Alignment.Start
    ) {
        // Bubble
        val bubbleColor = if (isBuyer) {
            if (isDarkTheme) WhatsAppOutgoingBubbleDark else WhatsAppOutgoingBubbleLight
        } else {
            if (isDarkTheme) WhatsAppIncomingBubbleDark else WhatsAppIncomingBubbleLight
        }

        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isBuyer) 14.dp else 2.dp,
                bottomEnd = if (isBuyer) 2.dp else 14.dp
            ),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                // Sender label for bot
                if (!isBuyer) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤖", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WhatsApp Bot",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhatsAppGreenPrimary
                        )
                    }
                }

                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (isDarkTheme) Color(0xFFE9EDEF) else Color(0xFF111B21)
                )

                // Action button if order summary
                if (message.isOrderSummary && onOpenWhatsApp != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onOpenWhatsApp,
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_wa_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📲 Send to Seller WhatsApp",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Time and read ticks
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeString,
                        fontSize = 10.sp,
                        color = if (isDarkTheme) Color(0xFF8696A0) else Color(0xFF667781)
                    )
                    if (isBuyer) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "Read",
                            tint = WhatsAppBlueCheck,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Quick action option pills
        if (!isBuyer && message.quickOptions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                message.quickOptions.forEach { opt ->
                    AssistChip(
                        onClick = { onOptionSelected(opt) },
                        label = {
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isDarkTheme) Color(0xFF1F2C34) else Color(0xFFE8F5E9),
                            labelColor = WhatsAppGreenDark
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("quick_reply_chip")
                    )
                }
            }
        }
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    subValue: String? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WhatsAppGreenPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subValue != null) {
                Text(
                    text = subValue,
                    style = MaterialTheme.typography.bodySmall,
                    color = WhatsAppGreenPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
