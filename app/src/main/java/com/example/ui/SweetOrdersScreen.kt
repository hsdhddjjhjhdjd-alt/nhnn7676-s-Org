package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.NewOrderDialog
import com.example.ui.components.OrderCard
import com.example.ui.components.OrderFilterBar
import com.example.ui.components.SweetShopHeader
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBgLight
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary

@Composable
fun SweetOrdersScreen(
    viewModel: SweetOrdersViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.filteredOrders.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val isAudioAlertEnabled by viewModel.isAudioAlertEnabled.collectAsStateWithLifecycle()
    val isAutoSimulationEnabled by viewModel.isAutoSimulationEnabled.collectAsStateWithLifecycle()
    val latestAlertOrder by viewModel.latestAlertOrder.collectAsStateWithLifecycle()
    val selectedOrderForInvoice by viewModel.selectedOrderForInvoice.collectAsStateWithLifecycle()
    val isNewOrderDialogOpen by viewModel.isNewOrderDialogOpen.collectAsStateWithLifecycle()
    val isFirebaseConnected by viewModel.isFirebaseConnected.collectAsStateWithLifecycle()
    val connectionMessage by viewModel.connectionMessage.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Real-time Top Notification Banner when a new order arrives
                AnimatedVisibility(
                    visible = latestAlertOrder != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    latestAlertOrder?.let { alertOrder ->
                        IncomingOrderBanner(
                            order = alertOrder,
                            onDismiss = { viewModel.dismissAlertBanner() }
                        )
                    }
                }

                // Single Unified Screen Layout
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Header: Counts & Actions (Red & White)
                    item(key = "header") {
                        SweetShopHeader(
                            stats = stats,
                            isAudioAlertEnabled = isAudioAlertEnabled,
                            isAutoSimulationEnabled = isAutoSimulationEnabled,
                            connectionMessage = connectionMessage,
                            isFirebaseConnected = isFirebaseConnected,
                            onToggleAudioAlert = { viewModel.toggleAudioAlert() },
                            onToggleAutoSimulation = { viewModel.toggleAutoSimulation() },
                            onSimulateNewOrder = { viewModel.simulateIncomingOrder() },
                            onOpenNewOrderDialog = { viewModel.openNewOrderDialog() },
                            onResetData = { viewModel.resetSampleData() }
                        )
                    }

                    // Search & Status Filters
                    item(key = "filter_bar") {
                        OrderFilterBar(
                            searchQuery = searchQuery,
                            selectedStatus = selectedStatus,
                            stats = stats,
                            onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                            onStatusSelect = { viewModel.onStatusFilterSelected(it) }
                        )
                    }

                    // Scheduled Delivery Times Quick Overview Widget
                    item(key = "delivery_schedules_timeline") {
                        DeliverySchedulesWidget(
                            orders = orders,
                            onOrderClick = { order ->
                                viewModel.onSearchQueryChanged(order.orderNumber)
                            }
                        )
                    }

                    // Section Title: عدد الطلبات ومواعيدها
                    item(key = "orders_section_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedStatus == null) "الطلبات والمواعيد (${orders.size})" else "${selectedStatus!!.titleAr} (${orders.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RedDark,
                                    fontSize = 15.sp
                                )
                            )

                            if (stats.newCount > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(RedPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${stats.newCount} طلبات جديدة",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = RedPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Orders List
                    if (orders.isEmpty()) {
                        item(key = "empty_state") {
                            EmptyOrdersState(
                                searchQuery = searchQuery,
                                onSimulate = { viewModel.simulateIncomingOrder() },
                                onClearFilter = {
                                    viewModel.onSearchQueryChanged("")
                                    viewModel.onStatusFilterSelected(null)
                                }
                            )
                        }
                    } else {
                        items(
                            items = orders,
                            key = { it.id }
                        ) { order ->
                            OrderCard(
                                order = order,
                                onAdvanceStatus = { viewModel.advanceOrderStatus(it) },
                                onUpdateStatus = { id, status -> viewModel.updateOrderStatus(id, status) },
                                onDeleteOrder = { viewModel.deleteOrder(it) },
                                onOpenInvoice = { viewModel.openInvoice(it) }
                            )
                        }
                    }
                }
            }

            // Printable Invoice Receipt Dialog
            selectedOrderForInvoice?.let { order ->
                InvoiceDialog(
                    order = order,
                    onDismiss = { viewModel.closeInvoice() }
                )
            }

            // Custom Order Dialog
            if (isNewOrderDialogOpen) {
                NewOrderDialog(
                    onDismiss = { viewModel.closeNewOrderDialog() },
                    onCreateOrder = { name, phone, address, area, deliveryTime, payment, notes, items ->
                        viewModel.createCustomOrder(
                            customerName = name,
                            customerPhone = phone,
                            address = address,
                            area = area,
                            deliveryTime = deliveryTime,
                            paymentMethod = payment,
                            notes = notes,
                            items = items
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun IncomingOrderBanner(
    order: OrderEntity,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, RedPrimary, RoundedCornerShape(12.dp)),
        color = RedPrimary,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PureWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "🔔 طلب جديد وصل الآن! (${order.orderNumber})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "العميل: ${order.customerName} • التسليم: ${order.deliveryTimeFormatted}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PureWhite.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق التنبيه",
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyOrdersState(
    searchQuery: String,
    onSimulate: () -> Unit,
    onClearFilter: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(RedBgLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (searchQuery.isNotBlank()) "لا توجد نتائج مطابقة لبحثك" else "لا توجد طلبات مسجلة حالياً",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RedDark
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (searchQuery.isNotBlank()) "جرب البحث بكلمات أخرى" else "اضغط أدناه لاستقبال ومحاكاة طلب جديد",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isNotBlank()) {
                Button(
                    onClick = onClearFilter,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary,
                        contentColor = PureWhite
                    )
                ) {
                    Text("مسح البحث وعرض الكل")
                }
            } else {
                Button(
                    onClick = onSimulate,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary,
                        contentColor = PureWhite
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("استقبال ومحاكاة طلب جديد")
                }
            }
        }
    }
}

@Composable
private fun DeliverySchedulesWidget(
    orders: List<OrderEntity>,
    onOrderClick: (OrderEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Only show for active orders (not completed/cancelled)
    val activeOrders = remember(orders) {
        orders.filter { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }
            .sortedBy { it.deliveryTimestamp }
    }
    
    if (activeOrders.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RedBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "مواعيد التسليم المحددة للطلبات النشطة (${activeOrders.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = RedDark
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Horizontal list of times
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
) {
                items(items = activeOrders) { order ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RedBgLight)
                            .border(1.dp, RedPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { onOrderClick(order) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(order.status.contentColor)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = order.orderNumber,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RedDark,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = order.deliveryTimeFormatted.replace("اليوم، ", ""),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RedPrimary,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = order.customerName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
