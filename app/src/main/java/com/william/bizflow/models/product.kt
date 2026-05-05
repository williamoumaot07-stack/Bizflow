package com.william.bizflow.models

class Product {
    var name: String = ""
    var buyingPrice: String = ""
    var sellingPrice: String = ""
    var stockCount: String = ""
    var id: String = ""

    constructor(name: String, buyingPrice: String, sellingPrice: String, stockCount: String, id: String) {
        this.name = name
        this.buyingPrice = buyingPrice
        this.sellingPrice = sellingPrice
        this.stockCount = stockCount
        this.id = id
    }

    constructor()
}
