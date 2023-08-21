package com.link_statu.stickers.features.sticker.repository.local

import com.jeluchu.jchucomponents.core.platform.ContextHandler
import com.link_statu.stickers.features.sticker.models.StickerPackEntity


class StickerLocal
    (contextHandler: ContextHandler) : StickerDBLocal {

    private val stickersApi by lazy {
        com.link_statu.stickers.core.database.AppDatabase.getAppDatabase(contextHandler.appContext).stickerEntityDao()
    }

    override fun getStickers(): List<StickerPackEntity> = stickersApi.getStickers()
    override fun addStickers(stickerPackEntity: StickerPackEntity) =
        stickersApi.insertStickers(stickerPackEntity)

    override fun deleteAllStickers() = stickersApi.deleteAll()

}