package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.SweetItem
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBgLight
import com.example.ui.theme.RedBgSoft
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextMain

@Composable
fun OrderCard(
    order: OrderEntity,
    onAdvanceStatus: (OrderEntity) -> Unit,
    onUpdateStatus: (Long, OrderStatus) -> Unit,
    onDeleteOrder: (Long) -> Unit,
    onOpenInvoice: (OrderEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(true) }
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "expand")

    val items = remember(order.itemsJson) { order.getItems() }
    val isNewOrder = order.status == OrderStatus.NEW

    val formattedCreatedTime = remember(order.createdAt) {
        val diffMinutes = ((System.currentTimeMillis() - order.createdAt) / (1000 * 60)).coerceAtLeast(1)
        if (diffMinutes < 60) "منذ $diffMinutes دقيقة" else "منذ ${diffMinutes / 60} ساعة"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNewOrder) 4.dp else 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isNewOrder) 1.5.dp else 1.dp,
            color = if (isNewOrder) RedPrimary else RedBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Order Number, Relative Time & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isNewOrder) RedPrimary else RedBgLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = order.orderNumber,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isNewOrder) PureWhite else RedDark,
                                fontSize = 15.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedCreatedTime,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Status Badge
                StatusPill(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Delivery Schedule & Deadline Banner (مواعيد التسليم)
            DeliveryScheduleHighlight(
                deliveryTimeFormatted = order.deliveryTimeFormatted,
                isNewOrder = isNewOrder
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Customer Details Card (الاسم، العنوان، التواصل)
            CustomerInfoSection(
                order = order,
                onCall = { dialPhoneNumber(context, order.customerPhone) },
                onOpenMap = { openAddressInMap(context, order.deliveryAddress) },
                onShare = { shareOrderSummary(context, order, items) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Products Header with Expand Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalDining,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تفاصيل وصور الحلويات (${items.size} أصناف)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = RedDark
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "عرض التفاصيل",
                    modifier = Modifier.rotate(rotationState),
                    tint = RedPrimary
                )
            }

            // Products & Photos List
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { sweetItem ->
                        SweetItemRow(item = sweetItem)
                    }
                }
            }

            // Customer Special Notes
            if (order.customerNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                CustomerNotesBox(notes = order.customerNotes)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = RedBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // Payment and Total Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = if (order.isPaid) Color(0xFF2E7D32) else RedPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.paymentMethod,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (order.isPaid) Color(0xFF2E7D32) else RedPrimary,
                            fontSize = 12.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "الإجمالي: ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                    Text(
                        text = "${order.totalPrice.toInt()} ر.س",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = RedPrimary,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Actions Bar
            OrderActionButtons(
                order = order,
                onAdvanceStatus = { onAdvanceStatus(order) },
                onUpdateStatus = { newStatus -> onUpdateStatus(order.id, newStatus) },
                onOpenInvoice = { onOpenInvoice(order) },
                onDelete = { onDeleteOrder(order.id) }
            )
        }
    }
}

@Composable
private fun StatusPill(status: OrderStatus) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(status.containerColor)
            .border(1.dp, status.contentColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.titleAr,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = status.contentColor,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun DeliveryScheduleHighlight(
    deliveryTimeFormatted: String,
    isNewOrder: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isNewOrder) RedBgLight else RedBgSoft)
            .border(1.5.dp, if (isNewOrder) RedPrimary else RedBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(RedPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "موعد تسليم الطلب:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = RedDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = deliveryTimeFormatted,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = RedPrimary,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun CustomerInfoSection(
    order: OrderEntity,
    onCall: () -> Unit,
    onOpenMap: () -> Unit,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PureWhite)
            .border(1.dp, RedBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Customer Name & Quick Call / Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(RedBgLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = RedPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = order.customerName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextMain
                            )
                        )
                        Text(
                            text = order.customerPhone,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "اتصال",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(RedBgLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = RedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = RedBorder.copy(alpha = 0.5f))

            // Address & Maps Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RedPrimary,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = order.deliveryArea,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextMain,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = order.deliveryAddress,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                OutlinedButton(
                    onClick = onOpenMap,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = RedPrimary
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("الخريطة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SweetItemRow(item: SweetItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RedBgSoft)
            .border(1.dp, RedBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Photo Thumbnail
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, RedBorder, RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = item.getDrawableRes()),
                contentDescription = item.name,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Product Name & Note
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextMain
                )
            )

            if (item.notes.isNotBlank()) {
                Text(
                    text = "ملاحظة: ${item.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = RedDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Text(
                text = "${item.quantity} × ${item.unitPrice.toInt()} ر.س",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            )
        }

        Text(
            text = "${item.totalPrice.toInt()} ر.س",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = RedPrimary,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun CustomerNotesBox(notes: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(RedBgLight)
            .border(1.dp, RedBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = null,
                tint = RedPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ملاحظات وتوصيات خاصة: $notes",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = RedDark,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun OrderActionButtons(
    order: OrderEntity,
    onAdvanceStatus: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    onOpenInvoice: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (order.status) {
            OrderStatus.NEW -> {
                Button(
                    onClick = onAdvanceStatus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedPrimary,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SoupKitchen,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "قبول والبدء بالتجهيز 👨‍🍳",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            OrderStatus.PREPARING -> {
                Button(
                    onClick = onAdvanceStatus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تم التجهيز • جاهز للتسليم 🎁",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            OrderStatus.READY -> {
                Button(
                    onClick = onAdvanceStatus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0),
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBike,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تسليم للمندوب للتوصيل 🛵",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            OrderStatus.ON_DELIVERY -> {
                Button(
                    onClick = onAdvanceStatus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00695C),
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تأكيد التسليم للعميل ✅",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            OrderStatus.COMPLETED -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تم إكمال وتسليم هذا الطلب",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            OrderStatus.CANCELLED -> {
                Text(
                    text = "هذا الطلب ملغي",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Red),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        // Secondary Action: Invoice receipt & Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onOpenInvoice,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary),
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = RedPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "فاتورة الطلب",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = RedPrimary,
                        fontSize = 12.sp
                    )
                )
            }

            OutlinedButton(
                onClick = onDelete,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف",
                    tint = RedPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun dialPhoneNumber(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح لوحة الاتصال", Toast.LENGTH_SHORT).show()
    }
}

private fun openAddressInMap(context: Context, address: String) {
    try {
        val encoded = Uri.encode(address)
        val uri = Uri.parse("geo:0,0?q=$encoded")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح تطبيق الخرائط", Toast.LENGTH_SHORT).show()
    }
}

private fun shareOrderSummary(context: Context, order: OrderEntity, items: List<SweetItem>) {
    try {
        val summary = buildString {
            append("🧾 تفاصيل الطلب (${order.orderNumber})\n")
            append("العميل: ${order.customerName}\n")
            append("الهاتف: ${order.customerPhone}\n")
            append("العنوان: ${order.deliveryAddress}\n")
            append("موعد التسليم: ${order.deliveryTimeFormatted}\n\n")
            append("الأصناف:\n")
            items.forEach {
                append("- ${it.name} (${it.quantity}) = ${it.totalPrice.toInt()} ر.س\n")
            }
            append("\nالإجمالي: ${order.totalPrice.toInt()} ر.س\n")
            append("حالة الدفع: ${order.paymentMethod}\n")
            if (order.customerNotes.isNotBlank()) {
                append("ملاحظات: ${order.customerNotes}\n")
            }
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, summary)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "مشاركة تفاصيل الطلب"))
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر مشاركة الطلب", Toast.LENGTH_SHORT).show()
    }
}
