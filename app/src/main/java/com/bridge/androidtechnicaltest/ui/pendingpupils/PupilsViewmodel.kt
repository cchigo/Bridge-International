package com.bridge.androidtechnicaltest.ui.pendingpupils


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingData
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.domain.GetPupilsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PupilsViewmodel @Inject constructor(
    pager: Pager<Int, PupilEntity>,
    private val getPupilsUseCase: GetPupilsUseCase,
    private val networkChecker: NetworkChecker
): ViewModel() {

    val pupilsPagingFlow = pager
        .flow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), PagingData.empty())

    private val _pupilByIdState = MutableStateFlow<BaseResponse<PupilEntity>?>(null)
    val pupilByIdState: StateFlow<BaseResponse<PupilEntity>?> = _pupilByIdState

    private val _isConnected = MutableStateFlow(networkChecker.isConnected())
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _shouldRetryLoad = MutableStateFlow(false)
    val shouldRetryLoad: StateFlow<Boolean> = _shouldRetryLoad



    fun loadPupilById(pupilId: Int) {
        viewModelScope.launch {
            getPupilsUseCase.getPupilById(pupilId).collectLatest {
                _pupilByIdState.value = it
            }
        }
    }


    fun checkNetwork() {
        _isConnected.value = networkChecker.isConnected()
    }

    fun clearPupilById() {
        _pupilByIdState.value = null
    }

}