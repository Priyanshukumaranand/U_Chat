package com.example.uchat

class Message {
    var message: String?= null
    var senderIdL: String?=null

    constructor()
    {}

    constructor(message: String?,senderId:String?){
        this.message = message
        this.senderIdL = senderId
    }

}