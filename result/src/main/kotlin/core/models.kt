package challenge.core

import java.util.*

interface PaymentMethod

class Address

class Customer

class Order(val customer: Customer, val address: Address) {
    private val paymentProcessor = PaymentProcessor()

    private val items = mutableListOf<OrderItem>()
    var closedAt: Date? = null
        private set
    var payment: Payment? = null
        private set
    val totalAmount
        get() = items.sumOf { it.total }

    fun addProduct(product: Product, quantity: Int) {
        var productAlreadyAdded = items.any { it.product == product }
        if (productAlreadyAdded)
            throw Exception("The product have already been added. Change the amount if you want more.")

        items.add(OrderItem(product, quantity))
    }

    fun pay(method: PaymentMethod) {
        if (payment != null)
            throw Exception("The order has already been paid!")

        if (items.isEmpty())
            throw Exception("Empty order can not be paid!")

        payment = Payment(this, method)

        paymentProcessor.handlePayment(payment!!)

        close()
    }

    private fun close() {
        closedAt = Date()
    }

    fun orderItems(): List<OrderItem> {
        return items.toList()
    }

}

enum class ProductType {
    PHYSICAL, BOOK, DIGITAL, MEMBERSHIP
}

data class OrderItem(val product: Product, val quantity: Int) {
    val total get() = product.price * quantity
}

data class Payment(val order: Order, val paymentMethod: PaymentMethod) {
    val paidAt = Date()
    val authorizationNumber = paidAt.time
    val amount = order.totalAmount
    val invoice = Invoice(order)
}

data class CreditCard(val number: String) : PaymentMethod

data class Invoice(val order: Order) {
    val billingAddress: Address = order.address
    val shippingAddress: Address = order.address
}

data class Product(val name: String, val type: ProductType, val price: Double)

