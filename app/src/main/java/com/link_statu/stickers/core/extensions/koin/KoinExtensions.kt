package com.link_statu.stickers.core.extensions.koin

import android.content.Context
import com.link_statu.stickers.core.di.applicationModule
import com.link_statu.stickers.core.di.dataSourceModule
import com.link_statu.stickers.core.di.databaseModule
import com.link_statu.stickers.core.di.networkModule
import com.link_statu.stickers.core.di.repositoryModule
import com.link_statu.stickers.core.di.useCaseModule
import com.link_statu.stickers.core.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun Context.initKoin() {
    startKoin {
        androidContext(this@initKoin)
        modules(
            networkModule,
            databaseModule,
            applicationModule,
            dataSourceModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }
}