package com.hopeinyang.zeroknowledge.ui.screens.DetailScreen


import androidx.lifecycle.SavedStateHandle
import com.hopeinyang.zeroknowledge.DETAIL_SCREEN
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dto.ArticleContent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class DetailScreenViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val dataStore: UserPreferenceService,
    savedStateHandle: SavedStateHandle

) :MainViewModel(logService, accountService, storageService , dataStore){




    private var _state = MutableStateFlow(DetailScreenUIState())
    val state:StateFlow<DetailScreenUIState>
        get() = _state


    val userId = checkNotNull(savedStateHandle.get<String>("userId")){throw IllegalArgumentException("userId is null")}
    init {

        launchCatching {

            val opDocsFlow = storageService.getOperationalDocs()
            val baseDocsFlow = storageService.getBaseDocs()

            combine(opDocsFlow, baseDocsFlow){
                                             opDocs, baseDocs->
                DetailScreenUIState(
                    userId = userId,
                    docList = baseDocs.data?.plus(opDocs.data ?: emptyList()) ?: emptyList(),
                )

            }.collect{_state.value = it}





        }

    }

    fun onDocSelected(selectedDocTitle: String) {

        _state.update {

            state->
            val selected = state.docList.first{ it.docTitle==selectedDocTitle }
            state.copy(
                selectedDocTitle = selectedDocTitle,
                isExpanded = false,
                selectedDoc = selected
            )
        }


    }

    fun onExpandedChanged(isExpanded: Boolean) {

        _state.update {state->
            state.copy(
                isExpanded = isExpanded
            )
        }

    }

    fun onBackButtonClick(navigateBack: (String, String) -> Unit) {
        navigateBack("$HOME_SCREEN/$userId", DETAIL_SCREEN)
    }



}


