package com.link_statu.stickers.features.sticker.models

data class StickerPack(
    val androidPlayStoreLink: String?,
    val iosAppStoreLink: String?,
    val publisherEmail: String?,
    val privacyPolicyWebsite: String?,
    val licenseAgreementWebsite: String?,
    val telegram_url: String?,
    val identifier: Int?,
    val name: String?,
    val publisher: String?,
    val publisherWebsite: String?,
    val animatedStickerPack: Boolean?,
    val stickers: List<Sticker>?,
    val trayImageFile: String?
) {

    fun toStickersPackView(): StickerPackView =
        StickerPackView(
            androidPlayStoreLink ?: "",
            iosAppStoreLink ?: "",
            publisherEmail ?: "",
            privacyPolicyWebsite ?: "",
            licenseAgreementWebsite ?: "",
            telegram_url ?: "",
            identifier ?: 0,
            name ?: "",
            publisher ?: "",
            publisherWebsite ?: "",
            animatedStickerPack ?: false,
            stickers?.map { it.toStickerView() } ?: emptyList(),
            trayImageFile ?: ""
        )

}