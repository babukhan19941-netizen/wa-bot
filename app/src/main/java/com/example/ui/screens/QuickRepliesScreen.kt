package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.AutoReplyRule
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.ui.viewmodel.BotViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickRepliesScreen(
    viewModel: BotViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rules by viewModel.autoReplyRules.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var ruleToEdit by remember { mutableStateOf<AutoReplyRule?>(null) }
    var ruleToDelete by remember { mutableStateOf<AutoReplyRule?>(null) }

    val cleanPhone = settings.sellerPhone.replace("+", "").replace(" ", "").replace("-", "")
    val waLink = "https://wa.me/$cleanPhone?text=${URLEncoder.encode("Hi, I want to see your catalog and place an order", StandardCharsets.UTF_8.toString())}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Auto-Replies & Link Tools",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppGreenDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    ruleToEdit = null
                    showAddEditDialog = true
                },
                containerColor = WhatsAppGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_rule_fab")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Keyword Rule")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // WhatsApp Link Generator Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(WhatsAppGreenPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Filled.Link, contentDescription = null, tint = WhatsAppGreenPrimary)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "WhatsApp Direct Store Link",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = waLink,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("WhatsApp Link", waLink)
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showSnackBar("Link copied to clipboard! 📋")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Link")
                            }

                            OutlinedButton(
                                onClick = {
                                    val shareText = "नमस्ते! 🙏 हमारे स्टोर से सीधे सामान खरीदने या कैटलॉग देखने के लिए नीचे दिए गए WhatsApp लिंक पर क्लिक करें:\n$waLink"
                                    viewModel.shareOrderText(context, shareText)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Link")
                            }
                        }
                    }
                }
            }

            // Keyword Auto-Reply Rules Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = WhatsAppGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Auto-Reply Keyword Rules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${rules.size} Rules",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (rules.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚡", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Koi custom keyword rule nahi hai",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Jaise 'discount', 'timing', ya 'refund' par automatic jawab set karein.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(rules, key = { it.id }) { rule ->
                    AutoReplyRuleCard(
                        rule = rule,
                        onToggleEnabled = {
                            viewModel.updateRule(rule.copy(isEnabled = !rule.isEnabled))
                        },
                        onEdit = {
                            ruleToEdit = rule
                            showAddEditDialog = true
                        },
                        onDelete = {
                            ruleToDelete = rule
                        }
                    )
                }
            }
        }
    }

    // Add / Edit Rule Dialog
    if (showAddEditDialog) {
        AddEditRuleDialog(
            rule = ruleToEdit,
            onDismiss = {
                showAddEditDialog = false
                ruleToEdit = null
            },
            onSave = { savedRule ->
                if (ruleToEdit == null) {
                    viewModel.addRule(savedRule)
                } else {
                    viewModel.updateRule(savedRule)
                }
                showAddEditDialog = false
                ruleToEdit = null
            }
        )
    }

    if (ruleToDelete != null) {
        AlertDialog(
            onDismissRequest = { ruleToDelete = null },
            title = { Text("Delete Auto-Reply Rule?") },
            text = { Text("Kya aap is auto-reply rule ko hatana chahte hain?") },
            confirmButton = {
                Button(
                    onClick = {
                        ruleToDelete?.let { viewModel.deleteRule(it.id) }
                        ruleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { ruleToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AutoReplyRuleCard(
    rule: AutoReplyRule,
    onToggleEnabled: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = WhatsAppGreenPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Keywords: ${rule.keywords}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = { onToggleEnabled() },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = WhatsAppGreenPrimary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🤖 Auto Reply:\n${rule.response}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit", tint = WhatsAppGreenDark)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AddEditRuleDialog(
    rule: AutoReplyRule?,
    onDismiss: () -> Unit,
    onSave: (AutoReplyRule) -> Unit
) {
    var keywords by remember { mutableStateOf(rule?.keywords ?: "") }
    var response by remember { mutableStateOf(rule?.response ?: "") }
    var isEnabled by remember { mutableStateOf(rule?.isEnabled ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (rule == null) "New Auto-Reply Trigger" else "Edit Auto-Reply",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("Keywords (comma-separated)*") },
                    placeholder = { Text("offer, discount, sale, coupon") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = response,
                    onValueChange = { response = it },
                    label = { Text("Bot Auto Response Text*") },
                    placeholder = { Text("🎉 Special discount coupon: OFFER10") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (keywords.isNotBlank() && response.isNotBlank()) {
                        val newRule = AutoReplyRule(
                            id = rule?.id ?: 0,
                            keywords = keywords.trim(),
                            response = response.trim(),
                            isEnabled = isEnabled
                        )
                        onSave(newRule)
                    }
                },
                enabled = keywords.isNotBlank() && response.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary)
            ) {
                Text("Save Rule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
