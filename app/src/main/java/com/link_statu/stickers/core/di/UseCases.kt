package com.link_statu.stickers.core.di

import com.link_statu.stickers.features.sticker.usecase.GetStickers
import org.koin.dsl.module

val useCaseModule = module { factory { GetStickers(get()) } }