package com.william.bizflow.models

class Sale {
    var id: String = ""
    var productId: String = ""
    var productName: String = ""
    var quantity: String = ""
    var buyingPrice: String = ""
    var sellingPrice: String = ""
    var totalAmount: String = ""
    var profit: String = ""
    var customerId: String = ""
    var customerName: String = ""
    var timestamp: Long = 0L

    constructor(
        id: String,
        productId: String,
        productName: String,
        quantity: String,
        buyingPrice: String,
        sellingPrice: String,
        totalAmount: String,
        profit: String,
        customerId: String,
        customerName: String,
        timestamp: Long
    ) {
        this.id = id
        this.productId = productId
        this.productName = productName
        this.quantity = quantity
        this.buyingPrice = buyingPrice
        this.sellingPrice = sellingPrice
        this.totalAmount = totalAmount
        this.profit = profit
        this.customerId = customerId
        this.customerName = customerName
        this.timestamp = timestamp
    }

    constructor()
}
