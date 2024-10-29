package com.hopeinyang.zeroknowledge.ui.screens.Settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.BasicButton
import com.hopeinyang.zeroknowledge.common.composables.HeadingTextComponent
import com.hopeinyang.zeroknowledge.common.composables.IntegerNumberComponent
import com.hopeinyang.zeroknowledge.common.composables.MyTextFieldComponent
import com.hopeinyang.zeroknowledge.common.composables.PhoneNumberComponent
import com.hopeinyang.zeroknowledge.common.composables.ZeroTrustCenterTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    openAndPop:(String, String) -> Unit
){
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = { ZeroTrustCenterTopBar(
            isAdminAccount = false,
            scrollBehavior = scrollBehavior,
            title = R.string.account,
            titleColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.surface,
            navIconColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primaryContainer,
            actionIconColor = MaterialTheme.colorScheme.onSurface,
            navigateToSettings = { /*TODO*/ }) {
            viewModel.navigateBack(openAndPop)
            }
        },


        ){innerPadding ->
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)


        ) {


            Icon(
                painter = painterResource(id = R.drawable.person_3_fill),
                modifier = Modifier
                    .clip(RectangleShape)
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    ,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
            HeadingTextComponent(
                value = "Edit Personal Data",
                textAlign = TextAlign.Left
            )
            
                MyTextFieldComponent(
                    value = uiState.firstName,
                    label = R.string.firstName,
                    painter = painterResource(id = R.drawable.person_3_fill),
                    onTextChanged = {viewModel.onFirstNameChange(it)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 8.dp)
                )

                MyTextFieldComponent(
                    value = uiState.lastName,
                    label = R.string.lastName,
                    painter = painterResource(id = R.drawable.person_3_fill),
                    onTextChanged = {viewModel.onLastNameChange(it)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 8.dp)
                )
            
            


                PhoneNumberComponent(
                    value = uiState.phoneNumber ,
                    label = R.string.phone_number,
                    painter = painterResource(id = R.drawable.call_asset),
                    onTextChanged = {viewModel.onPhoneNumberChange(it)},
                    modifier = Modifier
                        .padding(horizontal = 2.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            

        

           
            Spacer(modifier = Modifier.height(10.dp))

            BasicButton(
                text = R.string.update_account,
                enableButton = true,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .heightIn(15.dp)
                    .widthIn()
            ) {

                viewModel.onUpdateClick(openAndPop)
            }
           


        }

    }

}