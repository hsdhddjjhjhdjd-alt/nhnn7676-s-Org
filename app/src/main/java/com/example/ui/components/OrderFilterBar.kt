package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.OrderStats
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBgLight
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFilterBar(
    searchQuery: String,
    selectedStatus: OrderStatus?,
    stats: OrderStats,
    onSearchQueryChange: (String) -> Unit,
    onStatusSelect: (OrderStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Red & White Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_order_input"),
            placeholder = {
                Text(
                    text = "ابحث باسم العميل، موعد التسليم، أو رقم الطلب...",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث",
                    tint = RedPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "مسح البحث",
                            tint = Color.Gray
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RedPrimary,
                unfocusedBorderColor = RedBorder,
                focusedContainerColor = PureWhite,
                unfocusedContainerColor = PureWhite
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal status & schedule filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // All Chip
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelect(null) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "جميع الطلبات",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(
                            containerColor = if (selectedStatus == null) RedPrimary else RedBgLight,
                            contentColor = if (selectedStatus == null) PureWhite else RedDark
                        ) {
                            Text(text = "${stats.totalCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RedPrimary,
                    selectedLabelColor = PureWhite,
                    containerColor = PureWhite,
                    labelColor = RedDark
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("filter_chip_all")
            )

            // NEW status
            FilterChip(
                selected = selectedStatus == OrderStatus.NEW,
                onClick = { onStatusSelect(OrderStatus.NEW) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("الطلبات الجديدة 🔴", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        if (stats.newCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = if (selectedStatus == OrderStatus.NEW) PureWhite else RedPrimary,
                                contentColor = if (selectedStatus == OrderStatus.NEW) RedPrimary else PureWhite
                            ) {
                                Text(text = "${stats.newCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = RedPrimary,
                    selectedLabelColor = PureWhite,
                    containerColor = RedBgLight,
                    labelColor = RedPrimary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("filter_chip_new")
            )

            // PREPARING / SCHEDULED
            FilterChip(
                selected = selectedStatus == OrderStatus.PREPARING,
                onClick = { onStatusSelect(OrderStatus.PREPARING) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("قيد التحضير ⏰", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        if (stats.preparingCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = if (selectedStatus == OrderStatus.PREPARING) PureWhite else Color(0xFFE65100),
                                contentColor = if (selectedStatus == OrderStatus.PREPARING) Color(0xFFE65100) else PureWhite
                            ) {
                                Text(text = "${stats.preparingCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFE65100),
                    selectedLabelColor = PureWhite,
                    containerColor = PureWhite,
                    labelColor = Color(0xFFE65100)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("filter_chip_preparing")
            )

            // READY
            FilterChip(
                selected = selectedStatus == OrderStatus.READY,
                onClick = { onStatusSelect(OrderStatus.READY) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("جاهز للتسليم ✅", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        if (stats.readyCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = if (selectedStatus == OrderStatus.READY) PureWhite else Color(0xFF2E7D32),
                                contentColor = if (selectedStatus == OrderStatus.READY) Color(0xFF2E7D32) else PureWhite
                            ) {
                                Text(text = "${stats.readyCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2E7D32),
                    selectedLabelColor = PureWhite,
                    containerColor = PureWhite,
                    labelColor = Color(0xFF2E7D32)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("filter_chip_ready")
            )

            // ON_DELIVERY
            FilterChip(
                selected = selectedStatus == OrderStatus.ON_DELIVERY,
                onClick = { onStatusSelect(OrderStatus.ON_DELIVERY) },
                label = {
                    Text("جاري التوصيل 🛵", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1565C0),
                    selectedLabelColor = PureWhite,
                    containerColor = PureWhite,
                    labelColor = Color(0xFF1565C0)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("filter_chip_delivery")
            )

            // COMPLETED
            FilterChip(
                selected = selectedStatus == OrderStatus.COMPLETED,
                onClick = { onStatusSelect(OrderStatus.COMPLETED) },
                label = {
                    Text("مكتمل", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF455A64),
                    selectedLabelColor = PureWhite,
                    containerColor = PureWhite,
                    labelColor = Color(0xFF455A64)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("filter_chip_completed")
            )
        }
    }
}
