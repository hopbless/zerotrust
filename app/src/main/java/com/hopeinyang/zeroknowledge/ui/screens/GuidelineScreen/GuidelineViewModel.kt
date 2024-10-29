package com.hopeinyang.zeroknowledge.ui.screens.GuidelineScreen


import androidx.lifecycle.SavedStateHandle
import com.hopeinyang.zeroknowledge.DETAIL_SCREEN
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.MainViewModel
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class GuidelineViewModel @Inject constructor(
    logService: LogService,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val dataStore: UserPreferenceService,
    savedStateHandle: SavedStateHandle

) :MainViewModel(logService, accountService, storageService , dataStore){




    private var _state = MutableStateFlow(GuidelineUIState())
    val state:StateFlow<GuidelineUIState>
        get() = _state

    val userId = checkNotNull(savedStateHandle.get<String>("userId")){
        throw IllegalArgumentException("userId is null")}

    init {


        val role = checkNotNull(savedStateHandle.get<String>("role"))

        launchCatching {

            val userRole = when (role) {
                "Nurse" -> {
                    "NursingGuides"
                }
                "Doctor" -> {
                    "DoctorGuides"
                }
                else -> {
                    ""
                }
            }
            Timber.e("userRole $userRole")
            storageService.getGuidelines(DOC_CATEGORY, userRole).collect{ doc->


                _state.update {state->
                    state.copy(
                        docList = doc.data ?: emptyList(),
                        userId = userId,
                        role = role

                    )
                }

            }

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


    companion object{
        const val DOC_CATEGORY = "guidelines"
    }
}


