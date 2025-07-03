package com.bridge.androidtechnicaltest.ui.puplis


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.common.Utils.generateRandomImageUrl
import com.bridge.androidtechnicaltest.data.model.pupil.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTOMapper
import com.bridge.androidtechnicaltest.domain.GetPupilsUseCase
import com.bridge.androidtechnicaltest.domain.PupilManagerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingViewModel @Inject constructor(
    private val getPupilsUseCase: GetPupilsUseCase,
    private val pupilManagerUsecase: PupilManagerUsecase,
    private val dtoMapper: PupilDTOMapper,
    private val entityModelMapper: EntityModelMapper,
) : ViewModel() {


    fun insertMockPupils() {
        viewModelScope.launch {
            repeat(10) { index ->
                val mockPupil = Pupil(
                    name = "Pupil $index",
                    country = "Country $index",
                    latitude = 6.5 + index,
                    longitude = 3.4 + index,
                    image = generateRandomImageUrl("hii")
                )
                val ent = entityModelMapper.to(mockPupil)
                pupilManagerUsecase.mockInsert(ent)
            }
        }
    }



    private var unsynchedPupils =
        getPupilsUseCase.getUnsyncedPupilsFromDB()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    private val _state = MutableStateFlow(PupilEventState())
    val state =
        combine(_state, unsynchedPupils) { state,  notes ->
            state.copy(
                pupilsLocal = notes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PupilEventState())

    private val _syncState = MutableStateFlow<SyncResult?>(null)
    val syncState: StateFlow<SyncResult?> = _syncState



    fun onEvent(event: PupilEvents) {

        when (event) {
            is PupilEvents.DeletePupils -> {
                viewModelScope.launch {
                    pupilManagerUsecase.delete(event.localPupil)
                }
            }

            is PupilEvents.SavePupilEntity -> {


            }

            PupilEvents.SortPupils -> {

            }

            PupilEvents.SyncPupils -> {
                viewModelScope.launch {

                    pupilManagerUsecase.syncPupils()
                        .flowOn(Dispatchers.IO)
                        .collect { result ->
                            _syncState.value = result
                        }

                    // this  resets state after sync
                    delay(3000)
                    _syncState.value = null
                }
            }

        }
    }

    fun clearSyncState() {
        _syncState.value = null
    }


    sealed class SyncResult {
        data object Started : SyncResult()
        data class Progress(val current: Int, val total: Int) : SyncResult()
        data object Success : SyncResult()
        data class Failure(val failedCount: Int, val message: String ?= null) : SyncResult()


    }


}
