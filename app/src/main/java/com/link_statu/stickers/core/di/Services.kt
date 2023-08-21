package com.link_statu.stickers.core.di

import com.link_statu.stickers.features.sticker.repository.StickersService
import org.koin.dsl.module

val dataSourceModule = module { factory { StickersService(get()) } }