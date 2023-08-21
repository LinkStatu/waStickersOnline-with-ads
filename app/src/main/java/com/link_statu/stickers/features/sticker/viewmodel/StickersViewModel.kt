package com.link_statu.stickers.features.sticker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jeluchu.jchucomponents.core.functional.map
import com.jeluchu.jchucomponents.ktx.job.cancelIfActive
import com.link_statu.stickers.core.interactor.UseCase
import com.link_statu.stickers.core.platform.BaseViewModel
import com.link_statu.stickers.features.sticker.models.StickerPackView
import com.link_statu.stickers.features.sticker.usecase.GetStickers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class StickersViewModel(private val getStickers: GetStickers) : BaseViewModel() {

    var sticker = MutableLiveData<List<StickerPackView>>()

    var getStickersJob: Job? = null

    init {
        getStickers()
    }

    private fun getStickers() {
        getStickersJob.cancelIfActive()
        getStickersJob = viewModelScope.launch {
            getStickers(UseCase.None())
                .onStart { handleShowSpinner(true) }
                .onEach { handleShowSpinner(false) }
                .map { either ->
                    either.map { list ->
                        list.map { article ->
                            article.toStickersPackView()
                        }
                    }
                }.collect { it.fold(::handleFailure, ::handleStickersList) }
        }
    }

    private fun handleStickersList(stickers: List<StickerPackView>) {
        this.sticker.value = stickers
    }

}