package com.hopeinyang.zeroknowledge.ui.screens.PatientFormScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.PatientAddFormViewPager
import com.hopeinyang.zeroknowledge.data.dto.TempCardDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFormScreen(
    viewModel: PatientViewModel = hiltViewModel(),
    clearAndNavigate:(String)->Unit
){
    val uiState by viewModel.state

    Surface(
        color = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Patient Form",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                        )

                    },

                    actions = {},
                    navigationIcon = {
                         IconButton(onClick = { clearAndNavigate("$HOME_SCREEN/${uiState.userId}")}) {

                            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null,
                                modifier = Modifier)

                }
                    },
                )
            }
        ) {contentPadding->

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()



            ) {

                PatientAddFormViewPager(
                    listOf(1,2,3),
                    firstName = uiState.firstName,
                    lastName = uiState.lastName,
                    address = uiState.address,
                    phoneNumber = uiState.phoneNumber,
                    dateOfAdmission = uiState.dateOfAdmission,
                    age = uiState.age,
                    gender = uiState.gender,
                    height = uiState.height,
                    bloodType = uiState.bloodType,
                    weight = uiState.weight,
                    systolic = uiState.systolicBP,
                    diastolic = uiState.diastolicBP,
                    bloodSugar = uiState.bloodSugar,
                    bodyTemp = uiState.bodyTemp,
                    diagnosis = uiState.diagnosis,
                    options = uiState.departmentList,
                    department = uiState.department,
                    isExpanded = uiState.isExpanded,
                    onExpandedChanged = {viewModel.onDropDownExpandedChanged(it)},
                    onNewValue = {viewModel.onDepartmentChange(it)},
                    onDismissRequest = {viewModel.onDropDownExpandedChanged(false)},
                    onFirstNameChange = {viewModel.onFirstNameChange(it)},
                    onLastNameChange = {viewModel.onLastNameChange(it)},
                    onAddressChange = {viewModel.onAddressChange(it)},
                    onPhoneNumberChange = {viewModel.onPhoneNumberChange(it)},
                    onDateOfAdmissionChange = {viewModel.onDateOfAdmissionChange(it)},
                    onAgeChange = {viewModel.onAgeChange(it)},
                    onGenderChange = {viewModel.onGenderChange(it)},
                    onHeightChange = {viewModel.onHeightChange(it)},
                    onBloodTypeChange = {viewModel.onBloodTypeChange(it)},
                    onWeightChange = {viewModel.onWeightChange(it)},
                    onSystolicChange = {viewModel.onSystolicChange(it)},
                    onDiastolicChange = {viewModel.onDiastolicChange(it)},
                    onBloodSugarChange = {viewModel.onBloodSugarChange(it)},
                    onBodyTempChange = {viewModel.onBodyTempChange(it)},
                    onDiagnosisChange = {viewModel.onDiagnosisChange(it)},
                    onSubmitClick = {viewModel.onSubmitButtonClick(clearAndNavigate)}

                )
            }

        }

    }

}