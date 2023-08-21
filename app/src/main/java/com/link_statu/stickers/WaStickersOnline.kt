package com.link_statu.stickers

import android.app.Application
import com.onesignal.OneSignal
import com.link_statu.stickers.core.extensions.koin.initKoin
import com.link_statu.stickers.core.extensions.sharedprefs.initSharedPrefs


class WaStickersOnline : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        initSharedPrefs()
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(resources.getString(R.string.ONESIGNAL_APP_ID))

    }
}