package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.BotSettings
import com.example.ui.theme.WhatsAppGreenDark
import com.example.ui.theme.WhatsAppGreenLight
import com.example.ui.theme.WhatsAppGreenPrimary
import com.example.ui.viewmodel.BotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotSetupScreen(
    viewModel: BotViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSimulator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by viewModel.settings.collectAsStateWithLifecycle()

    var sellerPhone by remember(currentSettings) { mutableStateOf(currentSettings.sellerPhone) }
    var sellerName by remember(currentSettings) { mutableStateOf(currentSettings.sellerName) }
    var welcomeMessage by remember(currentSettings) { mutableStateOf(currentSettings.welcomeMessage) }
    var catalogHeader by remember(currentSettings) { mutableStateOf(currentSettings.catalogHeader) }
    var deliveryChargesStr by remember(currentSettings) { mutableStateOf(currentSettings.deliveryCharges.toInt().toString()) }
    var freeDeliveryMinStr by remember(currentSettings) { mutableStateOf(currentSettings.freeDeliveryMinAmount.toInt().toString()) }
    var estimatedTime by remember(currentSettings) { mutableStateOf(currentSettings.estimatedDeliveryTime) }
    var codAvailable by remember(currentSettings) { mutableStateOf(currentSettings.codAvailable) }
    var upiAvailable by remember(currentSettings) { mutableStateOf(currentSettings.upiAvailable) }
    var upiId by remember(currentSettings) { mutableStateOf(currentSettings.upiId) }
    var botActive by remember(currentSettings) { mutableStateOf(currentSettings.botActive) }
    var autoAskAddress by remember(currentSettings) { mutableStateOf(currentSettings.autoAskAddress) }
    var autoAskPayment by remember(currentSettings) { mutableStateOf(currentSettings.autoAskPayment) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bot & WhatsApp Setup",
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
                actions = {
                    IconButton(
                        onClick = {
                            val newSett = currentSettings.copy(
                                sellerPhone = sellerPhone.trim(),
                                sellerName = sellerName.trim(),
                                welcomeMessage = welcomeMessage.trim(),
                                catalogHeader = catalogHeader.trim(),
                                deliveryCharges = deliveryChargesStr.toDoubleOrNull() ?: 40.0,
                                freeDeliveryMinAmount = freeDeliveryMinStr.toDoubleOrNull() ?: 499.0,
                                estimatedDeliveryTime = estimatedTime.trim(),
                                codAvailable = codAvailable,
                                upiAvailable = upiAvailable,
                                upiId = upiId.trim(),
                                botActive = botActive,
                                autoAskAddress = autoAskAddress,
                                autoAskPayment = autoAskPayment
                            )
                            viewModel.updateSettings(newSett)
                        },
                        modifier = Modifier.testTag("save_settings_top")
                    ) {
                        Icon(imageVector = Icons.Filled.Save, contentDescription = "Save", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WhatsAppGreenDark)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Helper Banner
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhatsAppGreenPrimary.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = WhatsAppGreenDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Apna WhatsApp number aur auto-reply details set karein. Koi bhi buyer message bhejte hi bot turant reply karega.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WhatsAppGreenDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Section 1: WhatsApp Seller Profile
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Phone, contentDescription = null, tint = WhatsAppGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. WhatsApp Profile Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = sellerPhone,
                            onValueChange = { sellerPhone = it },
                            label = { Text("Aapka WhatsApp Number (with Country Code)*") },
                            placeholder = { Text("+919876543210") },
                            leadingIcon = { Text("📞", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_phone_field")
                        )

                        OutlinedTextField(
                            value = sellerName,
                            onValueChange = { sellerName = it },
                            label = { Text("Dukan / Business ka Naam (Store Name)*") },
                            placeholder = { Text("Babu Store") },
                            leadingIcon = { Text("🏪", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bot Auto-Reply Active", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = botActive,
                                onCheckedChange = { botActive = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = WhatsAppGreenPrimary)
                            )
                        }
                    }
                }
            }

            // Section 2: Welcome Greeting & Catalog Header
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.WavingHand, contentDescription = null, tint = WhatsAppGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. Welcome Message & Greeting",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = welcomeMessage,
                            onValueChange = { welcomeMessage = it },
                            label = { Text("Welcome Message (Jaise hi koi 'Hi' bheje)*") },
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("welcome_message_field")
                        )

                        OutlinedTextField(
                            value = catalogHeader,
                            onValueChange = { catalogHeader = it },
                            label = { Text("Product Menu Header") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Section 3: Delivery Details & Fees
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.LocalShipping, contentDescription = null, tint = WhatsAppGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "3. Delivery Options & Charges",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = deliveryChargesStr,
                                onValueChange = { deliveryChargesStr = it },
                                label = { Text("Delivery Fee (₹)*") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            OutlinedTextField(
                                value = freeDeliveryMinStr,
                                onValueChange = { freeDeliveryMinStr = it },
                                label = { Text("Free Delivery above (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = estimatedTime,
                            onValueChange = { estimatedTime = it },
                            label = { Text("Estimated Delivery Time") },
                            placeholder = { Text("24-48 Hours / 1-2 Din") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Section 4: Payment Options
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Payments, contentDescription = null, tint = WhatsAppGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4. Payment Options (भुगतान)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = codAvailable,
                                onCheckedChange = { codAvailable = it },
                                colors = CheckboxDefaults.colors(checkedColor = WhatsAppGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cash on Delivery (COD) Available", fontWeight = FontWeight.Medium)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = upiAvailable,
                                onCheckedChange = { upiAvailable = it },
                                colors = CheckboxDefaults.colors(checkedColor = WhatsAppGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("UPI / Online Payment Available", fontWeight = FontWeight.Medium)
                        }

                        if (upiAvailable) {
                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it },
                                label = { Text("Aapka UPI ID (e.g. mobile@upi)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Save and Test Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val newSett = currentSettings.copy(
                                sellerPhone = sellerPhone.trim(),
                                sellerName = sellerName.trim(),
                                welcomeMessage = welcomeMessage.trim(),
                                catalogHeader = catalogHeader.trim(),
                                deliveryCharges = deliveryChargesStr.toDoubleOrNull() ?: 40.0,
                                freeDeliveryMinAmount = freeDeliveryMinStr.toDoubleOrNull() ?: 499.0,
                                estimatedDeliveryTime = estimatedTime.trim(),
                                codAvailable = codAvailable,
                                upiAvailable = upiAvailable,
                                upiId = upiId.trim(),
                                botActive = botActive,
                                autoAskAddress = autoAskAddress,
                                autoAskPayment = autoAskPayment
                            )
                            viewModel.updateSettings(newSett)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_settings_button")
                    ) {
                        Icon(imageVector = Icons.Filled.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Apply Settings", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onNavigateToSimulator,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = WhatsAppGreenDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test Bot in Live Simulator 💬", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WhatsAppGreenDark)
                    }
                }
            }
        }
    }
}
