package com.link_statu.stickers.core.di

import com.link_statu.stickers.features.sticker.viewmodel.StickersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module { viewModel { StickersViewModel(get()) } }
