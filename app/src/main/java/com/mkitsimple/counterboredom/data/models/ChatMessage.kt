package com.mkitsimple.counterboredom.data.models

class ChatMessage(
    override val id: String,
    val text: String,
    override val fromId: String,
    override val toId: String,
    override val timestamp: Long,
    val filename: String,
    override val type: String = MessageType.TEXT
) :  Message {
    constructor() : this("", "", "", "", -1, "", "" )
}