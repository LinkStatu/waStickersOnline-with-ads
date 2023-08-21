package com.link_statu.stickers.core.di

import com.link_statu.stickers.features.sticker.repository.StickersRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<StickersRepository> { StickersRepository.Network(get(), get(), get()) }
}