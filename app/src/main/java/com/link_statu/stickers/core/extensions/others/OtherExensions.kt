package com.link_statu.stickers.core.extensions.others

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Browser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.jeluchu.jchucomponents.ktx.packageutils.buildIsLollipopAndUp
import com.jeluchu.jchucomponents.ktx.packageutils.buildIsMarshmallowAndUp
import com.link_statu.stickers.R
import java.io.IOException

fun Activity.statusBarColor() {
    setStatusBarColor(R.color.white)
    setSystemBarLight(this)
}

fun Activity.setStatusBarColor(@ColorRes color: Int) {
    if (buildIsLollipopAndUp) {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
}

fun setSystemBarLight(act: Activity) {
    if (buildIsMarshmallowAndUp) {
        val view = act.findViewById<View>(android.R.id.content)
        var flags = view.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        view.systemUiVisibility = flags
    }
}

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun Activity.openActivityRight() {
    overridePendingTransition(R.anim.enter_slide_right, R.anim.exit_slide_left)
}

fun Activity.exitActivityBottom() {
    finish()
    overridePendingTransition(R.anim.enter_slide_bottom, R.anim.exit_slide_bottom)
}

fun Activity.exitActivityLeft() {
    finish()
    overridePendingTransition(R.anim.enter_slide_left, R.anim.exit_slide_right)
}

fun Activity.openInCustomTab(string: String) = customTabsWeb(string)

private fun Context.customTabsWeb(string: String) {
    try {

        val builder = CustomTabsIntent.Builder().apply {
            setToolbarColor(
                Color.parseColor(
                    "#" + Integer.toHexString(
                        ContextCompat.getColor(
                            this@customTabsWeb,
                            R.color.redCardBackground
                        )
                    )
                )
            )
            setShowTitle(true)
            setExitAnimations(this@customTabsWeb, R.anim.enter_slide_left, R.anim.exit_slide_left)
            setStartAnimations(
                this@customTabsWeb,
                R.anim.enter_slide_right,
                R.anim.exit_slide_right
            )
        }

        val intent = builder.build()
        intent.launchUrl(this, Uri.parse(string))
    } catch (e: IOException) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(string))
        intent.putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, packageName)
        startActivity(intent)
    }
}


fun TextView.simpleText(value: String) {
    text = value
}

fun ViewGroup.inflate(layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)