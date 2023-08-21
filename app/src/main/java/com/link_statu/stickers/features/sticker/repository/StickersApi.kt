package com.link_statu.stickers.features.sticker.repository

import com.link_statu.stickers.features.sticker.models.PacksEntity
import retrofit2.Call
import retrofit2.http.GET

interface StickersApi {

    @GET("contents.json")
    fun getStickers(): Call<PacksEntity>

}