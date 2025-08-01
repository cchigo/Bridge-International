package com.bridge.androidtechnicaltest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.domain.GetPupilsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GetPupilsViewmodel @Inject constructor(
    private val getPupilsUseCase: GetPupilsUseCase
) : ViewModel() {

//    val pagedPupils: Flow<PagingData<PupilEntity>> =
//        pagingRepository.getPagedPupils().cachedIn(viewModelScope)

    private val _pupilByIdState = MutableStateFlow<BaseResponse<PupilEntity>?>(null)
    val pupilByIdState: StateFlow<BaseResponse<PupilEntity>?> = _pupilByIdState


    fun loadPupilById(pupilId: Int) {
        viewModelScope.launch {
            getPupilsUseCase.getPupilById(pupilId).collectLatest {
                _pupilByIdState.value = it
            }
        }
    }

    fun clearPupilById() {
        _pupilByIdState.value = null
    }


    // Manual loading

//    private val _pupilsState = MutableStateFlow<BaseResponse<List<PupilEntity>>>(BaseResponse.Loading)
//    val pupilsState: StateFlow<BaseResponse<List<PupilEntity>>> = _pupilsState
//
//    fun loadPupils(page: Int = 1) {
//        viewModelScope.launch {
//            getPupilsUseCase.getPupils(page).collect { response ->
//                _pupilsState.value = response
//            }
//        }
//    }

}

