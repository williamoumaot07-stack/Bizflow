package com.william.bizflow.models

class User {
    var name: String = ""
    var email: String = ""
    var uid: String = ""
    var profileImageUrl: String = ""

    constructor(name: String, email: String, uid: String, profileImageUrl: String = "") {
        this.name = name
        this.email = email
        this.uid = uid
        this.profileImageUrl = profileImageUrl
    }

    constructor()
}
