package challenge.core

import challenge.service.EmailService
import challenge.service.MembershipService
import challenge.service.ShipmentService
import challenge.service.VoucherService
import java.math.BigDecimal

class PaymentProcessor {
    private val processingActions = HashMap<ProductType, ProcessAction>()

    init {
        processingActions[ProductType.PHYSICAL] = PhysicalItemProcessor()
        processingActions[ProductType.MEMBERSHIP] = SubscriptionProcessor()
        processingActions[ProductType.BOOK] = BookProcessor()
        processingActions[ProductType.DIGITAL] = DigitalMediaProcessor()
    }

    fun handlePayment(payment: Payment) {
        if (payment == null) throw Exception("The order has not been paid!")

        payment.order.orderItems().forEach {
            if (processingActions[it.product.type] == null) {
                println("[Error] :: ${it.product.name} could not be processed")
            }

            processingActions[it.product.type]?.process(payment, it)
        }
    }
}

interface ProcessAction {
    fun process(payment: Payment, item: OrderItem)
}

class PhysicalItemProcessor : ProcessAction {
    private val shipmentService = ShipmentService()

    override fun process(payment: Payment, item: OrderItem) {
        shipmentService.addShippingLabel("Add to send box.", item.product)
    }
}

class SubscriptionProcessor : ProcessAction {
    private val membershipService = MembershipService()
    private val emailService = EmailService()

    override fun process(payment: Payment, item: OrderItem) {
        val customer = payment.order.customer

        membershipService.activateMemberShip(customer, item.product)
        emailService.sendEmail("Membership email", customer)
    }
}

class BookProcessor : ProcessAction {
    private val shipmentService = ShipmentService()

    override fun process(payment: Payment, item: OrderItem) {
        shipmentService.addShippingLabel(
            "Item isento de impostos conforme disposto na Constituição Art. 150, VI, d.",
            item.product
        )
    }
}

class DigitalMediaProcessor : ProcessAction {
    private val emailService = EmailService()
    private val voucherService = VoucherService()

    override fun process(payment: Payment, item: OrderItem) {
        val customer = payment.order.customer

        emailService.sendEmail(
            """
                Uma compra de ${item.product.name} foi realizada.
                Quantidade: ${item.quantity}.
                Total: ${item.total}".
            """.trimIndent(), customer
        )

        voucherService.grantDiscount(BigDecimal.valueOf(10), customer)
    }
}