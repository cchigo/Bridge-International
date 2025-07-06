package com.bridge.androidtechnicaltest.ui.pendingpupils


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.domain.GetPupilsUseCase
import com.bridge.androidtechnicaltest.domain.PendingStatus
import com.bridge.androidtechnicaltest.domain.PendingWorkerUseCase
import com.bridge.androidtechnicaltest.domain.PupilManagerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingViewModel @Inject constructor(
    private val getPupilsUseCase: GetPupilsUseCase,
    private val pupilManagerUsecase: PupilManagerUsecase,
    private val workerUseCase: PendingWorkerUseCase
) : ViewModel() {

    private val _workerStatus = MutableStateFlow<PendingStatus?>(null)
    val workerStatus: StateFlow<PendingStatus?> = _workerStatus

    fun startWorker() {
        viewModelScope.launch {
            workerUseCase.startPendingSyncWorker().collect { status ->
                _workerStatus.value = status
            }
        }
    }

    private var unsynchedPupils =
        getPupilsUseCase.getUnsyncedPupilsFromDB()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(PupilEventState())
    val pendingState =
        combine(_state, unsynchedPupils) { state,  notes ->
            state.copy(
                pupilsLocal = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PupilEventState())

    fun onEvent(event: PupilEvents) {

        when (event) {
            is PupilEvents.DeletePupils -> {
                viewModelScope.launch { pupilManagerUsecase.delete(event.localPupilId) }
            }

            PupilEvents.SyncPupils -> {
                viewModelScope.launch { startWorker() }
            }

        }
    }


}
