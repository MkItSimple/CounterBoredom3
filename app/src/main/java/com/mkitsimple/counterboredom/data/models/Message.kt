package com.mkitsimple.counterboredom.data.models

object MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    val id: String
    val fromId: String
    val toId: String
    val timestamp: Long
    val type: String
}