package com.link_statu.stickers.features.sticker.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.jeluchu.jchucomponents.ktx.coroutines.noCrash
import com.jeluchu.jchucomponents.ktx.strings.getLastBitFromUrl
import com.jeluchu.jchucomponents.ktx.strings.saveImage
import com.link_statu.stickers.BuildConfig
import com.link_statu.stickers.R
import com.link_statu.stickers.core.extensions.others.exitActivityLeft
import com.link_statu.stickers.core.extensions.others.openInCustomTab
import com.link_statu.stickers.core.extensions.others.simpleText
import com.link_statu.stickers.core.extensions.others.statusBarColor
import com.link_statu.stickers.core.extensions.serializable
import com.link_statu.stickers.core.extensions.viewbinding.viewBinding
import com.link_statu.stickers.core.utils.ConstantsMeth.Companion.getApiEndpointStickers
import com.link_statu.stickers.databinding.ActivityStickerDetailsBinding
import com.link_statu.stickers.features.sticker.models.StickerPackView
import com.link_statu.stickers.features.sticker.view.MainActivity.Companion.EXTRA_STICKER_PACK_AUTHORITY
import com.link_statu.stickers.features.sticker.view.MainActivity.Companion.EXTRA_STICKER_PACK_ID
import com.link_statu.stickers.features.sticker.view.MainActivity.Companion.EXTRA_STICKER_PACK_NAME
import com.link_statu.stickers.features.sticker.view.adapter.StickersDetailsAdapter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream

private var rewardedAd: RewardedAd? = null
private var TAG = "Stickers"
private var lastAdTime: Long = 0
private var interstitialAd: InterstitialAd? = null


class StickerDetailsActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityStickerDetailsBinding::inflate)
    private val adapterStickers: StickersDetailsAdapter by inject()
    private var stickerPackView: StickerPackView? = null
    private lateinit var mprogressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        statusBarColor()
        initUI()
        initListeners()
        getStickerPack()




        MobileAds.initialize(this)
        InterstitialAd.load(
            this, getString(R.string.interstitial_ad_unit_id),
            AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "onAdFailedToLoad: ${error.message}")
                }
            })
    }
    private fun tele(){
        openInCustomTab(stickerPackView!!.publisherWebsite)


    }
    private fun whats(){
        val intent = Intent().apply {
            action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
            putExtra(EXTRA_STICKER_PACK_ID, stickerPackView!!.identifier.toString())
            putExtra(EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY)
            putExtra(EXTRA_STICKER_PACK_NAME, stickerPackView!!.name)
        }
        try {
            startActivityForResult(intent, 200)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this@StickerDetailsActivity,
                getString(R.string.message_error_whatsapp),
                Toast.LENGTH_LONG
            ).show()
        }
    }


    ////////// Reward AdMob Ad ///////////////

    private fun loadRewardedAd(afterClose: () -> Unit) {
        mprogressBar.visibility = View.VISIBLE
        Toast.makeText(this@StickerDetailsActivity, "Loading the Ad, please wait", Toast.LENGTH_SHORT).show()
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, getString(R.string.reward_unit_id), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d(TAG, it) }
                rewardedAd = null
                mprogressBar.visibility=View.GONE
                Toast.makeText(this@StickerDetailsActivity, "Failed to load the Ad.", Toast.LENGTH_SHORT).show()

            }
            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd = ad
                showRewardedAd { afterClose() }


            }
        })

    }
    private fun showRewardedAd(afterClose: () -> Unit) {
        var rewardSuccess = false
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    mprogressBar.visibility=View.GONE
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    mprogressBar.visibility=View.GONE
                    Toast.makeText(this@StickerDetailsActivity, "Failed to load the Ad.", Toast.LENGTH_SHORT).show()
                }

                override fun onAdDismissedFullScreenContent() {
                    if(rewardSuccess){
                        mprogressBar.visibility=View.GONE
                        afterClose()
                    } else {
                        mprogressBar.visibility=View.GONE
                        Toast.makeText(this@StickerDetailsActivity, "The reward was not complete", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            rewardedAd?.show(this) {
                rewardSuccess = true
            }
        } else {
            mprogressBar.visibility=View.GONE
        }
    }
////////////////////////////////////


    private fun initListeners() = with(binding) {
        mprogressBar = findViewById(R.id.load_progressBar)
        mprogressBar.visibility = View.GONE
        ivBack.setOnClickListener { exitActivityLeft() }
        mcvAddToWhatsApp.setOnClickListener {
            val builder = AlertDialog.Builder(this@StickerDetailsActivity)
            builder.setMessage(getString(R.string.message_video_ad))
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    // Show the ad
                    loadRewardedAd(::whats)

                }
                .setNegativeButton("No") { dialog, id ->
                    // Don't show the ad
                }
            val alert = builder.create()
            alert.show()
        }
        mcvAddToTelegram.setOnClickListener {
            val builder = AlertDialog.Builder(this@StickerDetailsActivity)
            builder.setMessage(getString(R.string.message_video_ad))
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    // Show the ad
                    loadRewardedAd(::tele)



                }
                .setNegativeButton("No") { dialog, id ->
                    // Don't show the ad
                }
            val alert = builder.create()
            alert.show()
        }
        ////// Interstitial AdMob Ad on back-pressed //////
        onBackPressedDispatcher.addCallback(
            this@StickerDetailsActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (System.currentTimeMillis() - lastAdTime > getString(R.string.time_loop_for_interstitial_to_show_in_sec).toInt() * 1000L) {
                        if (interstitialAd != null) {
                            interstitialAd!!.show(this@StickerDetailsActivity)
                            interstitialAd = null
                            lastAdTime = System.currentTimeMillis()
                            InterstitialAd.load(
                                this@StickerDetailsActivity,
                                getString(R.string.interstitial_ad_unit_id),
                                AdRequest.Builder().build(),
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdLoaded(ad: InterstitialAd) {
                                        interstitialAd = ad
                                    }

                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        Log.e(TAG, "onAdFailedToLoad: ${error.message}")
                                    }
                                })
                            exitActivityLeft()
                        } else {
                            exitActivityLeft()
                        }
                    } else {
                        exitActivityLeft()
                    }
                }
            }
        )
    }
    ////////////////

    private fun initUI() = with(binding) {
        if (intent.extras != null) {
            stickerPackView = intent.serializable("stickerpack") as StickerPackView?
        }

        ivTrayImage.load(stickerPackView!!.trayImageFile)
        tvPackName.simpleText(stickerPackView!!.name)
        tvAuthor.simpleText(stickerPackView!!.publisher)

        adapterStickers.supportFragmentManager = supportFragmentManager
        adapterStickers.submitList(stickerPackView?.stickers.orEmpty())
        rvStickers.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(30)
            adapter = adapterStickers
            scheduleLayoutAnimation()
        }

    }



    private fun getStickerPack() {
        noCrash {

            val trayImageFile = stickerPackView!!.trayImageFile.getLastBitFromUrl()

            val req = ImageRequest.Builder(this@StickerDetailsActivity)
                .data(getApiEndpointStickers() + stickerPackView!!.identifier + "/" + trayImageFile)
                .target {
                    val myDir =
                        File("${MainActivity.path}/${stickerPackView!!.identifier}/try")
                    myDir.mkdirs()
                    val imageName = trayImageFile.replace(".png", "").replace(" ", "_") + ".png"
                    val file = File(myDir, imageName)
                    if (file.exists()) file.delete()
                    try {
                        val out = FileOutputStream(file)
                        it.toBitmap().compress(Bitmap.CompressFormat.PNG, 40, out)
                        out.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.build()

            lifecycleScope.launch { ImageLoader(this@StickerDetailsActivity).execute(req) }

            for (s in stickerPackView!!.stickers) {
                val imageFile = s.imageFile.getLastBitFromUrl()

                val myDir = File("${MainActivity.path}/${stickerPackView!!.identifier}")
                myDir.mkdirs()
                val file = File(myDir, imageFile)
                if (file.exists()) file.delete()

                (getApiEndpointStickers() + stickerPackView!!.identifier + "/" + imageFile).saveImage(
                    File("${MainActivity.path}/${stickerPackView!!.identifier}", imageFile)
                )
            }
        }
    }
}