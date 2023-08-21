package com.link_statu.stickers.features.sticker.models

data class Sticker(
    val emojis: List<String>?,
    val imageFile: String?,
    val imageFileThum: String?
) {

    fun toStickerView(): StickerView =
        StickerView(
            emojis ?: emptyList(),
            imageFile ?: "",
            imageFileThum ?: ""
        )

}