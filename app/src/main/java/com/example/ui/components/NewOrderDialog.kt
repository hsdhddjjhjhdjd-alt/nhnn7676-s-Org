package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SweetItem
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RedBgLight
import com.example.ui.theme.RedBgSoft
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedDark
import com.example.ui.theme.RedPrimary

@Composable
fun NewOrderDialog(
    onDismiss: () -> Unit,
    onCreateOrder: (
        customerName: String,
        customerPhone: String,
        address: String,
        area: String,
        deliveryTime: String,
        paymentMethod: String,
        notes: String,
        items: List<SweetItem>
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("سلطان ناصر القحطاني") }
    var customerPhone by remember { mutableStateOf("0558765432") }
    var deliveryAddress by remember { mutableStateOf("حي الروضة، شارع خالد بن الوليد، عمارة 7، شقة 12") }
    var deliveryArea by remember { mutableStateOf("حي الروضة - الرياض") }
    var deliveryTime by remember { mutableStateOf("اليوم، 06:00 م (خلال 30 دقيقة)") }
    var paymentMethod by remember { mutableStateOf("بطاقة مدى / أبل باي (تم الدفع)") }
    var notes by remember { mutableStateOf("يرجى كتابة تهنئة وتغليف هدايا فاخر") }

    val availableSweets = remember {
        listOf(
            SweetItem("كنافة نابلسية خشنة بالمكسرات والقشطة", 1, 55.0, "img_kunafa", "شيرة دافئة"),
            SweetItem("تورتة الشوكولاتة والتوت البلجيكي الفاخرة", 1, 160.0, "img_cake", "مع شموع"),
            SweetItem("صينية بقلاوة تركية بالفستق الحلبي والعسل", 1, 110.0, "img_baklava", "تغليف هدايا ملكي"),
            SweetItem("كنافة بين نارين بالجبن الساخن", 1, 65.0, "img_kunafa", "زيادة فستق")
        )
    }

    val itemQuantities = remember {
        mutableStateMapOf<Int, Int>().apply {
            put(0, 1)
            put(1, 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, RedBorder),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إضافة وتأكيد طلب جديد",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RedDark
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = RedPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("بيانات العميل وموعد التسليم:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RedDark)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("اسم العميل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = RedBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("رقم جوال العميل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = RedBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("عنوان التوصيل بالتفصيل") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = RedBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deliveryTime,
                    onValueChange = { deliveryTime = it },
                    label = { Text("موعد التسليم المطلوب") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = RedBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات العميل والتوصيل") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RedPrimary,
                        unfocusedBorderColor = RedBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Select Sweets Items
                Text("اختيار الحلويات والكميات:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RedDark)
                Spacer(modifier = Modifier.height(8.dp))

                availableSweets.forEachIndexed { index, sweet ->
                    val qty = itemQuantities[index] ?: 0
                    val isSelected = qty > 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) RedBgLight else RedBgSoft)
                            .border(
                                1.dp,
                                if (isSelected) RedPrimary else RedBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = sweet.getDrawableRes()),
                            contentDescription = sweet.name,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(sweet.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${sweet.unitPrice.toInt()} ر.س", color = RedPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Quantity selector
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (qty > 0) {
                                IconButton(
                                    onClick = {
                                        if (qty == 1) itemQuantities.remove(index) else itemQuantities[index] = qty - 1
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "إنقاص", tint = RedPrimary, modifier = Modifier.size(16.dp))
                                }

                                Text("$qty", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                            }

                            IconButton(
                                onClick = {
                                    itemQuantities[index] = (itemQuantities[index] ?: 0) + 1
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(RedPrimary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "زيادة", tint = PureWhite, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val selectedItemsList = availableSweets.mapIndexedNotNull { index, item ->
                                val count = itemQuantities[index] ?: 0
                                if (count > 0) item.copy(quantity = count) else null
                            }
                            val finalItems = if (selectedItemsList.isEmpty()) {
                                listOf(availableSweets.first().copy(quantity = 1))
                            } else selectedItemsList

                            onCreateOrder(
                                customerName,
                                customerPhone,
                                deliveryAddress,
                                deliveryArea,
                                deliveryTime,
                                paymentMethod,
                                notes,
                                finalItems
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedPrimary,
                            contentColor = PureWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_create_order_button")
                    ) {
                        Text("تأكيد واستقبال الطلب", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RedPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إلغاء")
                    }
                }
            }
        }
    }
}
