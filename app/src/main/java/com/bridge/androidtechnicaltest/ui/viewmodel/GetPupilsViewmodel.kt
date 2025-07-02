package com.bridge.androidtechnicaltest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.bridge.androidtechnicaltest.domain.GetPupilsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GetPupilsViewmodel @Inject constructor(
    private val getPupilsUseCase: GetPupilsUseCase
) : ViewModel() {

    private val _pupilsState = MutableStateFlow<BaseResponse<List<PupilEntity>>>(BaseResponse.Loading)
    val pupilsState: StateFlow<BaseResponse<List<PupilEntity>>> = _pupilsState

    private val _pupilByIdState = MutableStateFlow<BaseResponse<Pupil>>(BaseResponse.Loading)
    val pupilByIdState: StateFlow<BaseResponse<Pupil>> = _pupilByIdState

    fun loadPupils(page: Int = 4) {

        viewModelScope.launch {
            getPupilsUseCase.getPupils(page).collect { response ->
                _pupilsState.value = response
            }
        }
    }


    fun loadPupilById(pupilId: Int) {
        viewModelScope.launch {
            getPupilsUseCase.getPupilById(pupilId).collect {
                _pupilByIdState.value = it
            }
        }
    }
}
