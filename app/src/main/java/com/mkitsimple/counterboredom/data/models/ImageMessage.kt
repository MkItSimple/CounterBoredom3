package com.mkitsimple.counterboredom.data.models

class ImageMessage(
    override val id: String,
    val imagePath: String,
    override val fromId: String,
    override val toId: String,
    override val timestamp: Long,
    val filename: String,
    override val type: String = MessageType.IMAGE

) : Message {
    constructor() : this("", "", "", "", -1,"", "")
}