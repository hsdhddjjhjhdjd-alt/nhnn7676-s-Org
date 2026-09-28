package com.example.data.repository

import com.example.data.db.OrderDao
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.SweetItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class OrderRepository(val orderDao: OrderDao) {

    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun insertOrder(order: OrderEntity): Long {
        return orderDao.insertOrder(order)
    }

    suspend fun updateStatus(orderId: Long, newStatus: OrderStatus) {
        orderDao.updateStatus(orderId, newStatus)
    }

    suspend fun deleteOrder(orderId: Long) {
        orderDao.deleteOrderById(orderId)
    }

    suspend fun clearAll() {
        orderDao.clearAll()
    }

    suspend fun initializeSampleDataIfEmpty() {
        // Create initial realistic orders
        val sampleOrders = listOf(
            OrderEntity(
                orderNumber = "#HL-901",
                customerName = "سارة عبد العزيز الشمري",
                customerPhone = "0551234567",
                deliveryAddress = "حي النرجس، شارع الأمير فيصل بن بندر، عمارة 12، الدور الثاني، شقة 4",
                deliveryArea = "حي النرجس - الرياض",
                deliveryTimeFormatted = "اليوم، 05:30 م (خلال 30 دقيقة)",
                deliveryTimestamp = System.currentTimeMillis() + (30 * 60 * 1000),
                status = OrderStatus.NEW,
                paymentMethod = "مدى / بطاقة ائتمانية (تم الدفع مسبقاً)",
                isPaid = true,
                customerNotes = "يرجى كتابة: (ألف مبروك التخرج يا سارة) على التورتة ووضع 6 شوك صغيرة",
                itemsJson = OrderEntity.itemsToJson(
                    listOf(
                        SweetItem(
                            name = "تورتة الشوكولاتة والتوت البلجيكي الفاخرة (وسط)",
                            quantity = 1,
                            unitPrice = 160.0,
                            imageDrawableName = "img_cake",
                            notes = "شوكولاتة داكنة مع توت طازج"
                        ),
                        SweetItem(
                            name = "كنافة نابلسية خشنة بالمكسرات والقشطة (نصف كيلو)",
                            quantity = 1,
                            unitPrice = 45.0,
                            imageDrawableName = "img_kunafa",
                            notes = "شيرة خفيفة وساخنة"
                        )
                    )
                ),
                totalPrice = 205.0,
                createdAt = System.currentTimeMillis() - (5 * 60 * 1000)
            ),
            OrderEntity(
                orderNumber = "#HL-902",
                customerName = "م. خالد بن منصور العتيبي",
                customerPhone = "0509876543",
                deliveryAddress = "حي الملقا، طريق أنس بن مالك، فيلا 28",
                deliveryArea = "حي الملقا - الرياض",
                deliveryTimeFormatted = "اليوم، 06:15 م (خلال 45 دقيقة)",
                deliveryTimestamp = System.currentTimeMillis() + (45 * 60 * 1000),
                status = OrderStatus.PREPARING,
                paymentMethod = "الدفع عند الاستلام (كاش أو شبكة)",
                isPaid = false,
                customerNotes = "الرجاء عدم التأخير ضيوف قادمون للمنزل، وتغليف البقلاوة في بوكس هدايا ملكي",
                itemsJson = OrderEntity.itemsToJson(
                    listOf(
                        SweetItem(
                            name = "صينية بقلاوة تركية مشكلة بالفستق الحلبي والعسل الملكي (1 كجم)",
                            quantity = 2,
                            unitPrice = 110.0,
                            imageDrawableName = "img_baklava",
                            notes = "تغليف ذهبي فاخر"
                        ),
                        SweetItem(
                            name = "كنافة بين نارين بالجبنة النابلسية الساخنة (1 كجم)",
                            quantity = 1,
                            unitPrice = 75.0,
                            imageDrawableName = "img_kunafa",
                            notes = "زيادة فستق حلبي محمص"
                        )
                    )
                ),
                totalPrice = 295.0,
                createdAt = System.currentTimeMillis() - (20 * 60 * 1000)
            ),
            OrderEntity(
                orderNumber = "#HL-903",
                customerName = "أم فهد القحطاني",
                customerPhone = "0543322114",
                deliveryAddress = "حي الصحافة، شارع العليا العام، مجمع الروضة، بوابة 2",
                deliveryArea = "حي الصحافة - الرياض",
                deliveryTimeFormatted = "اليوم، 06:45 م (خلال ساعة)",
                deliveryTimestamp = System.currentTimeMillis() + (60 * 60 * 1000),
                status = OrderStatus.READY,
                paymentMethod = "أبل باي Apple Pay (مدفوع)",
                isPaid = true,
                customerNotes = "التسليم لمندوب التوصيل المرفق رقمه أو الحارس عند البوابة",
                itemsJson = OrderEntity.itemsToJson(
                    listOf(
                        SweetItem(
                            name = "كنافة نابلسية ناعمة بالجبن العكاوي والمكسرات (1 كجم)",
                            quantity = 1,
                            unitPrice = 70.0,
                            imageDrawableName = "img_kunafa",
                            notes = "شيرة خارجية منفصلة"
                        ),
                        SweetItem(
                            name = "بوكس بقلاوة أصابع بالفستق والكاجو (نصف كيلو)",
                            quantity = 1,
                            unitPrice = 55.0,
                            imageDrawableName = "img_baklava",
                            notes = "طازجة ومقرمشة"
                        )
                    )
                ),
                totalPrice = 125.0,
                createdAt = System.currentTimeMillis() - (40 * 60 * 1000)
            ),
            OrderEntity(
                orderNumber = "#HL-904",
                customerName = "د. طارق الحازمي",
                customerPhone = "0567788990",
                deliveryAddress = "حي الياسمين، تقاطع طريق الملك عبد العزيز مع التخصصي",
                deliveryArea = "حي الياسمين - الرياض",
                deliveryTimeFormatted = "اليوم، 07:30 م",
                deliveryTimestamp = System.currentTimeMillis() + (120 * 60 * 1000),
                status = OrderStatus.ON_DELIVERY,
                paymentMethod = "بطاقة فيزا (مدفوع)",
                isPaid = true,
                customerNotes = "المندوب استلم الطلب وهو في الطريق الآن",
                itemsJson = OrderEntity.itemsToJson(
                    listOf(
                        SweetItem(
                            name = "كيكة رد فلفت وكريمة الجبن اللذيذة",
                            quantity = 1,
                            unitPrice = 140.0,
                            imageDrawableName = "img_cake",
                            notes = "مع شمعة ذهبية واحدة"
                        )
                    )
                ),
                totalPrice = 140.0,
                createdAt = System.currentTimeMillis() - (75 * 60 * 1000)
            )
        )
        orderDao.insertOrders(sampleOrders)
    }

    suspend fun generateRandomIncomingOrder(): OrderEntity {
        val randomNames = listOf(
            "نورة محمد الدوسري",
            "عبدالله إبراهيم السعد",
            "ريم فهد الشريف",
            "فيصل بن سلطان الماجد",
            "منى سليمان الرشيد",
            "عمر عبد الرحمن الحربي",
            "دلال أحمد التميمي",
            "يوسف علي الغامدي",
            "هند ناصر الخالدي",
            "بدر صالح الزهراني"
        )

        val randomAddresses = listOf(
            Pair("حي حطين، شارع الأمير تركي الأول، فيلا 15", "حي حطين - الرياض"),
            Pair("حي النخيل الغربي، شارع سالم بن معقل، عمارة 8، شقة 3", "حي النخيل - الرياض"),
            Pair("حي العقيق، شارع التحلية، برج الأندلس، الدور الرابع", "حي العقيق - الرياض"),
            Pair("حي الغدير، بالقرب من محطة المترو، مجمع ديار 2", "حي الغدير - الرياض"),
            Pair("حي اليرموك، طريق الدمام الفرعي، فيلا 42", "حي اليرموك - الرياض"),
            Pair("حي الملقا، شارع وادي حنيفة، شقة 10", "حي الملقا - الرياض")
        )

        val sweetPool = listOf(
            SweetItem(
                name = "كنافة نابلسية ملكية بالجبنة السائحة والمكسرات",
                quantity = 1,
                unitPrice = 65.0,
                imageDrawableName = "img_kunafa",
                notes = "مع شيرة دافئة إضافية"
            ),
            SweetItem(
                name = "تورتة شوكولاتة وتوت أحمر فاخرة لمناسبة سعيدة",
                quantity = 1,
                unitPrice = 150.0,
                imageDrawableName = "img_cake",
                notes = "كتابة تهنئة وشموع مجانية"
            ),
            SweetItem(
                name = "صينية مشكلة بقلاوة تركية بالفستق والعسل الطبيعي",
                quantity = 1,
                unitPrice = 95.0,
                imageDrawableName = "img_baklava",
                notes = "تغليف هدايا أنيق"
            ),
            SweetItem(
                name = "كنافة بالقشطة البلدية والورد الجوري والمكسرات",
                quantity = 2,
                unitPrice = 50.0,
                imageDrawableName = "img_kunafa",
                notes = "مقرمشة وساخنة"
            ),
            SweetItem(
                name = "كيك لوتس كرانشي مع صوص كراميل دافئ",
                quantity = 1,
                unitPrice = 135.0,
                imageDrawableName = "img_cake",
                notes = "بدون مكسرات"
            ),
            SweetItem(
                name = "أصابع بقلاوة بالكاجو والفستق الحلبي الممتاز",
                quantity = 1,
                unitPrice = 70.0,
                imageDrawableName = "img_baklava",
                notes = "سمن بلدي أصلي"
            )
        )

        val customer = randomNames.random()
        val addressPair = randomAddresses.random()
        val phoneNum = "05" + Random.nextInt(10000000, 99999999).toString()
        val orderNum = "#HL-" + Random.nextInt(905, 999).toString()

        // Pick 1 to 3 items
        val itemCount = Random.nextInt(1, 3)
        val selectedItems = sweetPool.shuffled().take(itemCount).map {
            it.copy(quantity = Random.nextInt(1, 3))
        }

        val totalPrice = selectedItems.sumOf { it.quantity * it.unitPrice }

        val minutesAhead = listOf(25, 35, 45, 60, 90).random()
        val deliveryTime = SimpleDateFormat("hh:mm a", Locale("ar")).format(
            Date(System.currentTimeMillis() + minutesAhead * 60 * 1000)
        )

        val paymentMethod = listOf(
            "أبل باي Apple Pay (تم الدفع)",
            "بطاقة مدى / ائتمان (تم الدفع)",
            "الدفع نقداً عند الاستلام",
            "شبكة (مدى) عند الاستلام"
        ).random()

        val isPaid = paymentMethod.contains("تم الدفع")

        val randomNotes = listOf(
            "يرجى الحرص على عدم اهتزاز الكيك أثناء التوصيل",
            "الرجاء الاتصال قبل الوصول بـ 10 دقائق",
            "تسليم الطلب للصالون مباشرة",
            "وضع شيرة خفيفة بدون إكثار",
            "طلب مستعجل لمناسبة عائلية، شكراً لتعاونكم",
            ""
        ).random()

        val newOrder = OrderEntity(
            orderNumber = orderNum,
            customerName = customer,
            customerPhone = phoneNum,
            deliveryAddress = addressPair.first,
            deliveryArea = addressPair.second,
            deliveryTimeFormatted = "اليوم، $deliveryTime (خلال $minutesAhead دقيقة)",
            deliveryTimestamp = System.currentTimeMillis() + (minutesAhead * 60 * 1000),
            status = OrderStatus.NEW,
            paymentMethod = paymentMethod,
            isPaid = isPaid,
            customerNotes = randomNotes,
            itemsJson = OrderEntity.itemsToJson(selectedItems),
            totalPrice = totalPrice,
            createdAt = System.currentTimeMillis()
        )

        val insertedId = orderDao.insertOrder(newOrder)
        return newOrder.copy(id = insertedId)
    }
}
