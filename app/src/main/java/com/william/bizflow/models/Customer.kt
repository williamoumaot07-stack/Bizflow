package com.william.bizflow.models

class Customer {
    var name: String = ""
    var phone: String = ""
    var location: String = ""
    var id: String = ""

    constructor(name: String, phone: String, location: String, id: String) {
        this.name = name
        this.phone = phone
        this.location = location
        this.id = id
    }

    constructor()
}
