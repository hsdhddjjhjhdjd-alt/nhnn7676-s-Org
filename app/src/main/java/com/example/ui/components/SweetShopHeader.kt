package com.example.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OrderStats
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBgLight
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary

@Composable
fun SweetShopHeader(
    stats: OrderStats,
    isAudioAlertEnabled: Boolean,
    isAutoSimulationEnabled: Boolean,
    connectionMessage: String,
    isFirebaseConnected: Boolean,
    onToggleAudioAlert: () -> Unit,
    onToggleAutoSimulation: () -> Unit,
    onSimulateNewOrder: () -> Unit,
    onOpenNewOrderDialog: () -> Unit,
    onResetData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseColor by infiniteTransition.animateColor(
        initialValue = RedPrimary,
        targetValue = Color(0xFFFF5252),
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseColor"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PureWhite,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Bar without store name, strictly functional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "استقبال وإدارة الطلبات",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RedDark,
                                fontSize = 17.sp
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(pulseColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مباشر • استقبال الطلبات نشط",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = RedPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Action icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleAudioAlert,
                        modifier = Modifier.testTag("audio_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isAudioAlertEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "التنبيهات الصوتية",
                            tint = if (isAudioAlertEnabled) RedPrimary else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = onResetData,
                        modifier = Modifier.testTag("reset_data_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة تعيين",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Firebase Cloud Sync Connection Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isFirebaseConnected) Color(0xFFE8F5E9) else RedBgLight)
                    .border(1.dp, if (isFirebaseConnected) Color(0xFF2E7D32).copy(alpha = 0.2f) else RedPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isFirebaseConnected) Color(0xFF2E7D32) else RedPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = connectionMessage,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isFirebaseConnected) Color(0xFF2E7D32) else RedDark,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Red & White Stat Summary Cards: (عدد الطلبات، الطلبات الجديدة، المواعيد)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total orders
                StatCard(
                    title = "عدد الطلبات",
                    value = "${stats.totalCount}",
                    color = RedDark,
                    bgColor = PureWhite,
                    borderColor = RedPrimary.copy(alpha = 0.3f),
                    modifier = Modifier.weight(1f)
                )

                // New Orders (الطلبات الجديدة)
                StatCard(
                    title = "طلبات جديدة",
                    value = "${stats.newCount}",
                    color = PureWhite,
                    bgColor = RedPrimary,
                    borderColor = RedDark,
                    isFeatured = true,
                    modifier = Modifier.weight(1.1f)
                )

                // Upcoming schedules count
                StatCard(
                    title = "مواعيد قادمة",
                    value = "${stats.preparingCount + stats.readyCount}",
                    color = RedPrimary,
                    bgColor = RedBgLight,
                    borderColor = RedPrimary.copy(alpha = 0.4f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Manual Custom Order Button (For telephone or walk-in orders)
            Button(
                onClick = onOpenNewOrderDialog,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedPrimary,
                    contentColor = PureWhite
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("add_custom_order_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "تسجيل طلب يدوي جديد (هاتف / استلام)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    bgColor: Color,
    borderColor: Color,
    isFeatured: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isFeatured) PureWhite.copy(alpha = 0.9f) else color.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = color,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            )
        }
    }
}
