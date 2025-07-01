package com.bridge.androidtechnicaltest.ui.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil
import com.bridge.androidtechnicaltest.domain.PupilManagerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PupilViewModel @Inject constructor(
    private val pupilManagerUsecase: PupilManagerUsecase
) : ViewModel() {

    private val _createState = MutableStateFlow<BaseResponse<Pupil>>(BaseResponse.Empty)
    val createState: StateFlow<BaseResponse<Pupil>> = _createState.asStateFlow()

    private val _updateState = MutableStateFlow<BaseResponse<Pupil>>(BaseResponse.Empty)
    val updateState: StateFlow<BaseResponse<Pupil>> = _updateState.asStateFlow()

    private val _deleteState = MutableStateFlow<BaseResponse<Pupil>>(BaseResponse.Empty)
    val deleteState: StateFlow<BaseResponse<Pupil>> = _deleteState.asStateFlow()

    fun updatePupil(pupil: Pupil) {
        viewModelScope.launch {
            pupilManagerUsecase.updatePupil(pupil).collect {
                _updateState.value = it
            }
        }
    }


    fun createPupil(pupil: Pupil) {
        viewModelScope.launch {
            pupilManagerUsecase.createPupil(pupil).collectLatest {
                _createState.value = it
            }
        }
    }

    fun deletePupil(pupilId: Pupil) {
        viewModelScope.launch {
            pupilManagerUsecase.deletePupil(pupilId).collectLatest {
                _deleteState.value = it
            }
        }
    }


}
