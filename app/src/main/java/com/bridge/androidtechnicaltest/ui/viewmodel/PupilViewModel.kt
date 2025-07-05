package com.bridge.androidtechnicaltest.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.data.models.local.Pupil
import com.bridge.androidtechnicaltest.domain.PupilManagerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _pupilByIdState = MutableStateFlow<Pupil?>(null)
    val pupilByIdState: StateFlow<Pupil?> = _pupilByIdState

    private var operationJob: Job? = null

    fun createPupil(pupil: Pupil, localId: Int? = null) {
        operationJob?.cancel()
        operationJob = viewModelScope.launch {
            pupilManagerUsecase.createPupil(pupil, localId).collectLatest {
                _createState.value = it
            }
        }
    }

    fun updatePupil(pupil: Pupil) {
        operationJob?.cancel()
        operationJob = viewModelScope.launch {
            pupilManagerUsecase.updatePupil(pupil).collectLatest {
                _updateState.value = it
            }
        }
    }

    fun deletePupil(pupil: Pupil, localId: Int?) {
        operationJob?.cancel()

        operationJob = viewModelScope.launch {
            pupilManagerUsecase.deletePupil(pupil, localId).collectLatest {
                _deleteState.value = it
            }
        }
    }

    fun getPupilsLocal(pupilId: Int) {
        viewModelScope.launch {
            pupilManagerUsecase.getPupilByIdFromDB(pupilId).collectLatest {
                _pupilByIdState.value = it
            }
        }
    }

    fun resetAllStates() {
        _createState.value = BaseResponse.Empty
        _updateState.value = BaseResponse.Empty
        _deleteState.value = BaseResponse.Empty
    }

    fun cancelOngoingOperation() {
        operationJob?.cancel()
    }
}

