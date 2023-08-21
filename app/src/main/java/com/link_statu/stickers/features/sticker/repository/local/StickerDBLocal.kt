package com.link_statu.stickers.features.sticker.repository.local

import com.link_statu.stickers.features.sticker.models.StickerPackEntity

interface StickerDBLocal {
    fun getStickers(): List<StickerPackEntity>
    fun addStickers(stickerPackEntity: StickerPackEntity): Any
    fun deleteAllStickers(): Any
}