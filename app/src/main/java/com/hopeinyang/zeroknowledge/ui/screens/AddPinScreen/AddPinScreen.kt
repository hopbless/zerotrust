package com.hopeinyang.zeroknowledge.ui.screens.AddPinScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.SETTINGS_SCREEN
import com.hopeinyang.zeroknowledge.common.composables.BasicButton
import com.hopeinyang.zeroknowledge.common.composables.ComposePinInput
import com.hopeinyang.zeroknowledge.common.composables.ComposePinInputStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPinScreen(
    viewModel: AddPinViewModel = hiltViewModel(),
    clearAndNavigate: (String) -> Unit,

    ){

    val uiState = viewModel.state
    val context = LocalContext.current


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                    },
                navigationIcon = {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clickable { clearAndNavigate(SETTINGS_SCREEN) }) {


                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Back",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.surface
                ),
                title = { Text(
                    text = "Create Pin",
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.Bold
                ) },
                windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
        },
        bottomBar = {}
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)

                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 0.dp),
                        text = "Enter four digit number to create your pin",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                        //.padding(16.dp)
                    ) {
                        Text(
                            text = "Enter Pin"
                        )

                        ComposePinInput(
                            value = uiState.value.pinText,
                            onValueChange = { viewModel.onPinChange(it) },
                            onPinEntered = {},
                            fontColor = MaterialTheme.colorScheme.onSurface,
                            cellBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                            focusedCellBorderColor = MaterialTheme.colorScheme.primary,
                            mask = '*',
                            style = ComposePinInputStyle.UNDERLINE,
                            modifier = Modifier,

                            )

                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                        //.padding(16.dp)
                    ) {
                        Text(
                            text = "Confirm",
                            textAlign = TextAlign.Center
                        )

                        ComposePinInput(
                            value = uiState.value.confirmPinText,
                            onValueChange = { viewModel.onConfirmPinChange(it) },
                            onPinEntered = { viewModel.onPinComplete(it) },
                            fontColor = MaterialTheme.colorScheme.onSurface,
                            cellBorderColor = MaterialTheme.colorScheme.tertiaryContainer,
                            focusedCellBorderColor = MaterialTheme.colorScheme.primary,
                            style = ComposePinInputStyle.UNDERLINE,
                            mask = '*',
                            modifier = Modifier,


                            )

                    }

                    Spacer(modifier = Modifier.height(20.dp))


                }
                BasicButton(
                    text = R.string.continue_text,
                    enableButton = uiState.value.enableButton,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-20).dp)
                ) {
                    viewModel.onContinueButtonClick(clearAndNavigate)
                }


            }
        }
    }

}