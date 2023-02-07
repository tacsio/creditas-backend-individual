package challenge.service

import challenge.core.Customer
import challenge.core.Product

import java.math.BigDecimal

class ShipmentService {
    fun addShippingLabel(label: String, product: Product) {
        println("[ShipmentService] :: $label - ${product.name}")
    }
}

class EmailService {
    fun sendEmail(email: String, customer: Customer) {
        println("[EmailService] :: $email $customer")
    }
}

class MembershipService {
    fun activateMemberShip(customer: Customer, product: Product) {
        println("[MembershipService] :: Membership of ${product.name} activated to $customer")
    }
}

class VoucherService {
    fun grantDiscount(dollars: BigDecimal, customer: Customer) {
        println("[VoucherService] :: Discount of $$dollars granted to $customer")
    }
}