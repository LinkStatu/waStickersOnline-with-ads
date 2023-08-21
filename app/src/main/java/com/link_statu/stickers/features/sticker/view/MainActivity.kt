package com.link_statu.stickers.features.sticker.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.jeluchu.jchucomponents.core.exception.Failure
import com.jeluchu.jchucomponents.ktx.lifecycle.failure
import com.jeluchu.jchucomponents.ktx.lifecycle.observe
import com.link_statu.stickers.R
import com.link_statu.stickers.core.extensions.others.exitActivityBottom
import com.link_statu.stickers.core.extensions.others.openActivity
import com.link_statu.stickers.core.extensions.others.openActivityRight
import com.link_statu.stickers.core.extensions.others.statusBarColor
import com.link_statu.stickers.core.extensions.sharedprefs.SharedPrefsHelpers
import com.link_statu.stickers.core.extensions.viewbinding.viewBinding
import com.link_statu.stickers.databinding.ActivityMainBinding
import com.link_statu.stickers.features.sticker.models.StickerPackView
import com.link_statu.stickers.features.sticker.view.adapter.StickersAdapter
import com.link_statu.stickers.features.sticker.viewmodel.StickersViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val getStickersView: StickersViewModel by inject()
    private val adapterStickers: StickersAdapter by inject()
    private lateinit var adView: AdView


    private val preferences by lazy { SharedPrefsHelpers() }
    private lateinit var errorMessageTextView: TextView  // Declare the errorMessageTextView variable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        MobileAds.initialize(this) {}
        // Load the AdView element from the XML layout
        adView = findViewById(R.id.adView)

        errorMessageTextView = findViewById(R.id.errorMessageTextView)
        val manufacturer = Build.MANUFACTURER // Manufacturer of the device
        val model = Build.MODEL // Model of the device


        if ((manufacturer.equals("Huawei", ignoreCase = true) && model.equals("FLA-LX2", ignoreCase = true))) {
            hidePacks()
            return

        }



        // Create an AdRequest to load the banner ad
        val adRequest = AdRequest.Builder().build()
        val adView = findViewById<AdView>(R.id.adView)
        admobBanner()


        // Load the banner ad
        adView.loadAd(adRequest)

        with(getStickersView) {
            observe(sticker, ::renderStickersList)
            failure(failure, ::handleFailure)
        }

        initRequiresConfig()
        statusBarColor()
        initListeners()
    }


    private fun hidePacks (){
        binding.rvStickersList.visibility = View.GONE
        adView.visibility = View.GONE

        // Show the error message
        errorMessageTextView.visibility = View.VISIBLE
        errorMessageTextView.text = "Sorry, sticker packs are not yet available"
    }


    private fun admobBanner(){
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Ad has finished loading, update the UI and display the ad
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Ad request failed, handle the error
            }

            override fun onAdClicked() {
                // User clicked on the ad, track the click event
            }

            override fun onAdClosed() {
                // User closed the ad, handle the close event
            }

            override fun onAdImpression() {
                // Ad impression recorded, track the impression event
            }

            override fun onAdOpened() {
                // Ad opened an overlay that covers the screen, handle the open event
            }
        }

    }
    override fun onDestroy() {
        // Destroy the banner ad when the activity is destroyed to prevent memory leaks
        adView.destroy()
        super.onDestroy()
    }
    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }


    private fun initRequiresConfig() {
        path = "$filesDir/stickers_asset"

    }

    private fun initListeners() {
        adapterStickers.clickListener = {
            openActivity(StickerDetailsActivity::class.java) {
                putParcelable(EXTRA_STICKERPACK, it)
            }
            openActivityRight()
        }
        onBackPressedDispatcher.addCallback(
            this@MainActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = exitActivityBottom()
            }
        )
    }

    private fun renderStickersList(stickersView: List<StickerPackView>?) {
        preferences.saveObjectsList("sticker_packs", stickersView)
        adapterStickers.submitList(stickersView.orEmpty())
        binding.rvStickersList.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = adapterStickers
            scheduleLayoutAnimation()
        }
    }

    private fun handleFailure(failure: Failure?) = failure.toString()

    companion object {
        const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
        const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
        const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"
        const val EXTRA_STICKERPACK = "stickerpack"

        @JvmField
        var path: String? = null

    }
}