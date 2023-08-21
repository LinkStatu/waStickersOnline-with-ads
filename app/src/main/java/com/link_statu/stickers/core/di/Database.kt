package com.link_statu.stickers.core.di

import com.link_statu.stickers.features.sticker.repository.local.StickerLocal
import org.koin.dsl.module

val databaseModule = module { factory { StickerLocal(get()) } }