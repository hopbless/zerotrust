package com.hopeinyang.zeroknowledge.ui.screens.PatientLogs


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold

import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopeinyang.zeroknowledge.HOME_SCREEN
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.ButtonComponent
import com.hopeinyang.zeroknowledge.common.composables.DropdownSelector
import com.hopeinyang.zeroknowledge.common.composables.MyDatePickerComponent
import com.hopeinyang.zeroknowledge.common.composables.MyTextFieldComponent
import com.hopeinyang.zeroknowledge.common.composables.NameAndSelectorRow
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarManager
import com.hopeinyang.zeroknowledge.common.snackbar.SnackBarMessage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientsLogScreen(
    viewModel: PatientsLogViewModel = hiltViewModel(),
    clearAndNavigate: (String)->Unit
){
    val uiState by viewModel.state.collectAsStateWithLifecycle()


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Surface(
        color = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Patient Treatment",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                        )

                    },

                    actions = {},
                    navigationIcon = {
                        IconButton(onClick = { clearAndNavigate("$HOME_SCREEN/${uiState.userId}")}) {

                            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null,
                                modifier = Modifier
                            )

                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.tertiary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer

                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        ){contentPadding->

            if (uiState.newTreatmentClick){

                if (uiState.selectedPatient.patientId.isNotEmpty()){
                    NewTreatmentScreen(
                        paddingValues = contentPadding,
                        uiState.treatment,
                        labTests = uiState.labTest,
                        prescription = uiState.prescription,
                        doctorComments = uiState.doctorComments,
                        onNewTreatmentChange = {viewModel.onNewTreatmentChange(it)},
                        onLabTestsChange = {viewModel.onLabTestChange(it)},
                        onPrescriptionChange = {viewModel.onPrescriptionChange(it)},
                        onDoctorCommentsChange = {viewModel.onDoctorCommentsChange(it)},
                        onNextAppointmentChange = {viewModel.onNextAppointmentChange(it)},
                        onCancelButtonClick = {viewModel.onCancelTreatmentButtonClick()},
                        onSaveButtonClick = {viewModel.onSaveTreatmentButtonClick()}
                    )
                }else{
                    SnackBarManager.showMessage(SnackBarMessage.StringSnackbar("You have to select a patient"))
                    viewModel.onCancelTreatmentButtonClick()
                }



            }else{
                Column (
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()


                ){
                    /*SearchBar(
                        query = uiState.query,
                        onQueryChange = {viewModel.onQueryChange(it)},
                        onSearch = {
                            viewModel.onSearchClick(it)
                        },
                        active = uiState.isSearchActive,
                        onActiveChange = {
                            viewModel.onActiveChange(it)
                        },
                        trailingIcon = {
                            if (uiState.isSearchActive)
                                IconButton(onClick = {
                                    viewModel.onClearIconClick("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear, contentDescription = null)
                                }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null )
                        },
                        placeholder = { Text(text = "Search") },

                        colors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            dividerColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        modifier = Modifier





                    ) {



                        uiState.patientList.forEach {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ){
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                Text(
                                    text = "${it.firstName} ${it.lastName}",
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .clickable { viewModel.onPatientSelected(it) })

                            }
                        }

                    }*/

                    DropdownSelector(
                        label = R.string.all_patients,
                        options = uiState.patientList.map { it.firstName + " " + it.lastName },
                        selection = uiState.selectedPatient.firstName + " " + uiState.selectedPatient.lastName,
                        isExpanded = uiState.isExpanded,
                        onExpandedChanged = {viewModel.onExpandedChange(it)},
                        modifier = Modifier
                            .padding(16.dp),
                        onNewValue = {viewModel.onSelectedPatient(it)}
                    ) {
                        viewModel.onExpandedChange(false)
                    }

                    Column (modifier = Modifier.verticalScroll(rememberScrollState())) {


                        HorizontalDivider(
                            Modifier.padding(8.dp),
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        )
                        Box (
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ){
                            Text(
                                text = "Treatments:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .width(250.dp)
                            )

                            DropdownSelector(
                                label = R.string.select_treatments,
                                options = uiState.treatmentList.map { it.treatmentDate },
                                selection = uiState.selectedTreatmentDate,
                                isExpanded = uiState.treatmentIsExpanded,
                                onExpandedChanged = {viewModel.onTreatmentExpanded(it)},
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .width(200.dp),
                                onNewValue = {viewModel.onTreatmentDateSelected(it)}
                            ) {
                                viewModel.onTreatmentExpanded(false)
                            }
                        }
                        HorizontalDivider(
                            Modifier.padding(8.dp),
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        )

                        Text(
                            text = "BioData",
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                letterSpacing = 0.2.em
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Box(
                            contentAlignment = Alignment.TopStart,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .heightIn(min = 150.dp)
                        ) {

                            Text(
                                buildAnnotatedString {
                                    withStyle(style =
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 20.sp
                                    )
                                    ){
                                        append("- Full Name: ")
                                    }
                                    append("${uiState.selectedPatient.firstName} ${uiState.selectedPatient.lastName}")
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(style =
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 20.sp
                                    )
                                    ){
                                        append("- Date of Admission: ")
                                    }
                                    append(uiState.selectedPatient.dateOfAdmission)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(top = 30.dp)
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(style =
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 20.sp
                                    )
                                    ){
                                        append("- Gender: ")
                                    }
                                    append(uiState.selectedPatient.gender)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(style =
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 20.sp
                                    )
                                    ){
                                        append("- Age: ")
                                    }
                                    append(uiState.selectedPatient.age)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(top = 60.dp)
                            )

                            Text(
                                buildAnnotatedString {
                                    withStyle(style =
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 20.sp
                                    )
                                    ){
                                        append("- Blood type: ")
                                    }
                                    append(uiState.selectedPatient.bloodType)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)

                            )

                        }

                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Details of Last Treatment",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }

                        Box(

                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .heightIn(min = 150.dp)
                        ) {
                            MyTextFieldComponent(
                                showLeadingIcon = false,
                                value = uiState.lastTreatment.labTests,
                                label = R.string.lab_test,
                                painter = painterResource(id = R.drawable.password_invisibility),
                                onTextChanged = {},
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxWidth()
                                    .heightIn(min = 120.dp)
                            )


                        }

                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                        Box(

                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .heightIn(min = 150.dp)
                        ) {
                            MyTextFieldComponent(
                                showLeadingIcon = false,
                                value = uiState.lastTreatment.prescription,
                                label = R.string.prescription,
                                painter = painterResource(id = R.drawable.password_invisibility),
                                onTextChanged = {},
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxWidth()
                                    .heightIn(min = 120.dp)
                            )


                        }

                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                        Text(
                            text = "Treated by: ${uiState.lastTreatment.treatedBy}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

                        Box(

                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .heightIn(min = 150.dp)
                        ) {
                            MyTextFieldComponent(
                                showLeadingIcon = false,
                                value = uiState.lastTreatment.doctorComments,
                                label = R.string.doctor_comments,
                                painter = painterResource(id = R.drawable.password_invisibility),
                                onTextChanged = {},
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxWidth()
                                    .heightIn(min = 120.dp)
                            )

                        }

                        ButtonComponent(
                            value = "New Treatment"
                        ) {

                           viewModel.onNewTreatmentButtonClick()
                        }

                        Spacer(modifier = Modifier.height(30.dp))


                    }

                }
            }










        }
    }

}

@Composable
private fun NewTreatmentScreen(
    paddingValues: PaddingValues,
    newTreatment:String,
    labTests:String,
    prescription:String,
    doctorComments:String,
    onNewTreatmentChange:(String)->Unit,
    onLabTestsChange:(String)->Unit,
    onPrescriptionChange:(String)->Unit,
    onDoctorCommentsChange:(String)->Unit,
    onNextAppointmentChange:(String)->Unit,
    onCancelButtonClick:()->Unit,
    onSaveButtonClick:()->Unit
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,

        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "State clearly  below what you are treating this patient for:",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(16.dp)
        )

       MyTextFieldComponent(
           showLeadingIcon = false,
           value = newTreatment,
           label = R.string.reason,
           painter = painterResource(id = R.drawable.password_invisibility),
           modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp)
               .heightIn(100.dp),
           onTextChanged = onNewTreatmentChange
       )

        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        MyTextFieldComponent(
            showLeadingIcon = false,
            value = labTests,
            label = R.string.lab_test,
            painter = painterResource(id = R.drawable.password_invisibility),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(100.dp),
            onTextChanged = onLabTestsChange
        )

        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

        MyTextFieldComponent(
            showLeadingIcon = false,
            value = prescription,
            label = R.string.prescription,
            painter = painterResource(id = R.drawable.password_invisibility),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(100.dp),
            onTextChanged = onPrescriptionChange
        )

        HorizontalDivider(Modifier.padding(horizontal = 16.dp))

        MyTextFieldComponent(
            showLeadingIcon = false,
            value = doctorComments,
            label = R.string.doctor_comments,
            painter = painterResource(id = R.drawable.password_invisibility),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(100.dp),
            onTextChanged = onDoctorCommentsChange
        )


        MyDatePickerComponent(
            value = "",
            onTextChanged = onNextAppointmentChange,
            label = R.string.next_appointment,
            modifier =Modifier.padding(16.dp)
        )

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ){
            Button(onClick = onCancelButtonClick) {
                Text(text = "Cancel")
            }

            Button(onClick = onSaveButtonClick) {
                Text(text = "Save")
            }
        }
    }
}