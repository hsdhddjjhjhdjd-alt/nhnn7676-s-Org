package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class OrderStatus(
    val titleAr: String,
    val descriptionAr: String,
    val containerColor: Color,
    val contentColor: Color
) {
    NEW(
        titleAr = "طلب جديد",
        descriptionAr = "تم تأكيد الطلب من العميل وبانتظار الموافقة",
        containerColor = Color(0xFFFFEBEE),
        contentColor = Color(0xFFC62828)
    ),
    PREPARING(
        titleAr = "قيد التحضير والفرن",
        descriptionAr = "جاري إعداد الحلويات والخبز في المعمل",
        containerColor = Color(0xFFFFF3E0),
        contentColor = Color(0xFFE65100)
    ),
    READY(
        titleAr = "جاهز للتسليم",
        descriptionAr = "الطلب معبأ وجاهز في قسم الاستلام",
        containerColor = Color(0xFFE8F5E9),
        contentColor = Color(0xFF2E7D32)
    ),
    ON_DELIVERY(
        titleAr = "خرج للتوصيل",
        descriptionAr = "مع مندوب التوصيل في الطريق للعميل",
        containerColor = Color(0xFFE3F2FD),
        contentColor = Color(0xFF1565C0)
    ),
    COMPLETED(
        titleAr = "تم التسليم بنجاح",
        descriptionAr = "تم استلام الطلب من قبل العميل",
        containerColor = Color(0xFFECEFF1),
        contentColor = Color(0xFF455A64)
    ),
    CANCELLED(
        titleAr = "ملغي",
        descriptionAr = "تم إلغاء هذا الطلب",
        containerColor = Color(0xFFFFEBEE),
        contentColor = Color(0xFF880E4F)
    )
}
