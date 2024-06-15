package com.xayah.feature.main.medium.backup.detail

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import com.xayah.core.data.repository.MediaRepository
import com.xayah.core.model.OpType
import com.xayah.core.model.database.MediaEntity
import com.xayah.core.rootservice.service.RemoteRootService
import com.xayah.core.ui.route.MainRoutes
import com.xayah.core.ui.viewmodel.BaseViewModel
import com.xayah.core.ui.viewmodel.IndexUiEffect
import com.xayah.core.ui.viewmodel.UiIntent
import com.xayah.core.ui.viewmodel.UiState
import com.xayah.feature.main.medium.R
import com.xayah.libpickyou.ui.PickYouLauncher
import com.xayah.libpickyou.ui.model.PermissionType
import com.xayah.libpickyou.ui.model.PickerType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class IndexUiState(
    val name: String,
    val isCalculating: Boolean,
) : UiState

sealed class IndexUiIntent : UiIntent {
    data object OnRefresh : IndexUiIntent()
    data class UpdateMedia(val mediaEntity: MediaEntity) : IndexUiIntent()
    data class SetPath(val context: Context, val mediaEntity: MediaEntity) : IndexUiIntent()
    data class Delete(val mediaEntity: MediaEntity) : IndexUiIntent()
}

@ExperimentalMaterial3Api
@HiltViewModel
class IndexViewModel @Inject constructor(
    args: SavedStateHandle,
    private val mediaRepo: MediaRepository,
    rootService: RemoteRootService,
) : BaseViewModel<IndexUiState, IndexUiIntent, IndexUiEffect>(
    IndexUiState(
        name = args.get<String>(MainRoutes.ARG_MEDIA_NAME) ?: "",
        isCalculating = false,
    )
) {
    init {
        rootService.onFailure = {
            val msg = it.message
            if (msg != null)
                emitEffectOnIO(IndexUiEffect.ShowSnackbar(message = msg))
        }
    }

    @DelicateCoroutinesApi
    override suspend fun onEvent(state: IndexUiState, intent: IndexUiIntent) {
        when (intent) {
            is IndexUiIntent.OnRefresh -> {
                emitState(state.copy(isCalculating = true))
                mediaRepo.updateLocalMediaSize(name = state.name, opType = OpType.BACKUP, preserveId = 0)
                emitState(state.copy(isCalculating = false))
            }

            is IndexUiIntent.UpdateMedia -> {
                mediaRepo.upsert(intent.mediaEntity)
            }

            is IndexUiIntent.SetPath -> {
                val entity = intent.mediaEntity
                withMainContext {
                    val context = intent.context
                    PickYouLauncher().apply {
                        setTitle(context.getString(R.string.select_target_directory))
                        setType(PickerType.DIRECTORY)
                        setLimitation(1)
                        setPermissionType(PermissionType.ROOT)
                        val pathList = awaitPickerOnce(context)
                        pathList.firstOrNull()?.also { pathString ->
                            entity.mediaInfo.path = pathString
                            entity.extraInfo.existed = true
                        }
                    }
                }
                mediaRepo.upsert(entity)
            }

            is IndexUiIntent.Delete -> {
                mediaRepo.deleteEntity(intent.mediaEntity)
            }
        }
    }

    private val _media: Flow<MediaEntity?> = mediaRepo.queryFlow(uiState.value.name, OpType.BACKUP, 0).flowOnIO()
    val mediaState: StateFlow<MediaEntity?> = _media.stateInScope(null)
}