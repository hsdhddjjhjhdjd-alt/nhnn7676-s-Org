package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OrderEntity
import com.example.data.model.SweetItem
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBgLight
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceDialog(
    order: OrderEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val items = remember(order.itemsJson) { order.getItems() }
    val dateStr = remember(order.createdAt) {
        SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar")).format(Date(order.createdAt))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, RedBorder),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "فاتورة استلام وتجهيز الطلب",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RedDark
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = RedPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Receipt Paper Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, RedBorder, RoundedCornerShape(8.dp)),
                    color = Color(0xFFFAFAFA)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "فاتورة تفاصيل الطلب",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = RedPrimary,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "قسم التجهيز والاستقبال",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Key-values
                        InvoiceRow("رقم الطلب:", order.orderNumber, isBold = true)
                        InvoiceRow("التاريخ والوقت:", dateStr)
                        InvoiceRow("اسم العميل:", order.customerName, isBold = true)
                        InvoiceRow("رقم الجوال:", order.customerPhone)
                        InvoiceRow("العنوان:", order.deliveryAddress)
                        InvoiceRow("موعد التسليم:", order.deliveryTimeFormatted, isHighlight = true)
                        InvoiceRow("طريقة الدفع:", order.paymentMethod)

                        if (order.customerNotes.isNotBlank()) {
                            InvoiceRow("ملاحظات:", order.customerNotes)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Table Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الصنف", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedDark, modifier = Modifier.weight(2f))
                            Text("العدد", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedDark, modifier = Modifier.weight(0.7f))
                            Text("السعر", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedDark, modifier = Modifier.weight(1f))
                            Text("الإجمالي", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedDark, modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = RedBorder)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Item rows
                        items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.name, fontSize = 11.sp, modifier = Modifier.weight(2f))
                                Text("${item.quantity}", fontSize = 11.sp, modifier = Modifier.weight(0.7f))
                                Text("${item.unitPrice.toInt()} ر.س", fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Text("${item.totalPrice.toInt()} ر.س", fontWeight = FontWeight.Bold, color = RedPrimary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        val subtotal = order.totalPrice / 1.15
                        val vat = order.totalPrice - subtotal

                        InvoiceRow("المبلغ بدون الضريبة:", "${String.format(Locale.US, "%.2f", subtotal)} ر.س")
                        InvoiceRow("ضريبة القيمة المضافة (15%):", "${String.format(Locale.US, "%.2f", vat)} ر.س")

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(RedBgLight)
                                .border(1.dp, RedPrimary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المبلغ الإجمالي المستحق:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RedDark
                                )
                            )
                            Text(
                                text = "${order.totalPrice.toInt()} ر.س",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RedPrimary,
                                    fontSize = 16.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SimulatedBarcode(order.orderNumber)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { shareReceipt(context, order, items) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedPrimary,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مشاركة الفاتورة", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إغلاق")
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF616161),
                fontSize = 12.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBold || isHighlight) FontWeight.Bold else FontWeight.Normal,
                color = if (isHighlight) RedPrimary else Color(0xFF212121),
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawLine(
            color = Color(0xFFEF9A9A),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = 1.5f
        )
    }
}

@Composable
private fun SimulatedBarcode(code: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .width(180.dp)
                .height(36.dp)
        ) {
            val barWidth = 3f
            var currentX = 0f
            val random = java.util.Random(code.hashCode().toLong())
            while (currentX < size.width) {
                val isBar = random.nextBoolean()
                val width = if (random.nextBoolean()) barWidth * 2 else barWidth
                if (isBar) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(currentX, 0f),
                        end = Offset(currentX, size.height),
                        strokeWidth = width
                    )
                }
                currentX += width + 2f
            }
        }
        Text(
            text = "* $code *",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = Color.Gray,
                fontSize = 11.sp
            )
        )
    }
}

private fun shareReceipt(context: Context, order: OrderEntity, items: List<SweetItem>) {
    val receiptText = buildString {
        append("🧾 تفاصيل الفاتورة\n")
        append("============================\n")
        append("رقم الطلب: ${order.orderNumber}\n")
        append("العميل: ${order.customerName}\n")
        append("الهاتف: ${order.customerPhone}\n")
        append("العنوان: ${order.deliveryAddress}\n")
        append("موعد التسليم: ${order.deliveryTimeFormatted}\n")
        append("طريقة الدفع: ${order.paymentMethod}\n")
        append("============================\n")
        append("الأصناف:\n")
        items.forEach {
            append("- ${it.name} | كمية: ${it.quantity} | السعر: ${it.totalPrice.toInt()} ر.س\n")
        }
        append("============================\n")
        append("المجموع النهائي: ${order.totalPrice.toInt()} ر.س\n")
        if (order.customerNotes.isNotBlank()) {
            append("ملاحظات: ${order.customerNotes}\n")
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, receiptText)
    }
    context.startActivity(Intent.createChooser(intent, "إرسال الفاتورة"))
}
