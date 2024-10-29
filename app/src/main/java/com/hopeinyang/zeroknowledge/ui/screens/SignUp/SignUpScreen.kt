package com.hopeinyang.zeroknowledge.ui.screens.SignUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hopeinyang.zeroknowledge.LOGIN_SCREEN
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.BasicButton
import com.hopeinyang.zeroknowledge.common.composables.CheckboxComponent
import com.hopeinyang.zeroknowledge.common.composables.ClickableLoginTextComponent
import com.hopeinyang.zeroknowledge.common.composables.DividerTextComponent
import com.hopeinyang.zeroknowledge.common.composables.HeadingTextComponent
import com.hopeinyang.zeroknowledge.common.composables.IntegerNumberComponent
import com.hopeinyang.zeroknowledge.common.composables.MyTextFieldComponent
import com.hopeinyang.zeroknowledge.common.composables.PasswordTextFieldComponent
import com.hopeinyang.zeroknowledge.common.composables.PhoneNumberComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    clearAndNavigate:(String)->Unit,
    navigateTo:(String)->Unit
){
    val uiState by viewModel.uiState

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { CenterAlignedTopAppBar(
            modifier = Modifier,
            title = {
                Text(
                    text = "Create Account",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn()
                        .padding(start = 30.dp),
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = MaterialTheme.colorScheme.onPrimary,
                    ),
                    textAlign = TextAlign.Left

                )
                    },
            colors =TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {


                IconButton(onClick = { navigateTo(LOGIN_SCREEN)}) {

                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null,
                        modifier = Modifier.size(40.dp))

                             }
            },
            actions = {

            },


        )

        }

    ) {innerPadding ->

        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)


        ) {


           Image(
               painter = painterResource(id = R.drawable.zero_trust),
               modifier = Modifier
                   .clip(RectangleShape)
                   .size(100.dp)
                   .align(Alignment.CenterHorizontally),
               contentDescription = null)
            HeadingTextComponent(
                value = "Sign up",
                textAlign = TextAlign.Left
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ){
                MyTextFieldComponent(
                    value = uiState.firstName,
                    label = R.string.firstName,
                    painter = painterResource(id = R.drawable.person_3_fill),
                    onTextChanged = {viewModel.onFirstNameChange(it)},
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                )

                MyTextFieldComponent(
                    value = uiState.lastName,
                    label = R.string.lastName,
                    painter = painterResource(id = R.drawable.person_3_fill),
                    onTextChanged = {viewModel.onLastNameChange(it)},
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                )
            }


            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)

            ){
                OutlinedTextField(
                    modifier = Modifier
                        .weight(0.7f)
                        .clip(MaterialTheme.shapes.small),
                    placeholder = { Text("+234") },
                    label = { Text(text = "Code")},
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    maxLines = 1,
                    value = uiState.phoneCode,
                    onValueChange = {
                        viewModel.onPhoneCodeChange(it)
                    }
                )

                PhoneNumberComponent(
                    value = uiState.phoneNumber,
                    label =R.string.phone_number,
                    painter = painterResource(id = R.drawable.call_asset),
                    onTextChanged ={viewModel.onPhoneNumberChange(it)},
                    modifier = Modifier
                        .weight(1.7f)
                        .padding(horizontal = 2.dp)
                )



            }



            MyTextFieldComponent(
                value = uiState.email,
                label = R.string.email,
                painter = painterResource(id = R.drawable.mail_asset),
                onTextChanged = {viewModel.onEmailChange(it)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )

            PasswordTextFieldComponent(
                value = uiState.password,
                painter = painterResource(id = R.drawable.password_asset),
                text = R.string.password,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)

            ) {

                viewModel.onPasswordChange(it)
            }

            PasswordTextFieldComponent(
                value = uiState.repeatPass,
                painter = painterResource(id = R.drawable.password_asset),
                text = R.string.confrim_password,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {

                viewModel.onRepeatPasswordChange(it)
            }

            CheckboxComponent(value = stringResource(id = R.string.terms_and_conditions),
                onTextSelected = {
                    /* To Do*/
                },
                onCheckedChange = {
                    viewModel.onCheckBoxChange(it)

                }
            )
            if(uiState.isLoginInProgress) {
                Box (contentAlignment = Alignment.Center){
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 8.dp)
                            .align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }else
                Spacer(modifier = Modifier.height(10.dp))

            BasicButton(
                text = R.string.submit,
                enableButton = true,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .heightIn(15.dp)
                    .widthIn()
            ) {

                viewModel.onSignUpClick(clearAndNavigate)
            }
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = true) {
                navigateTo(LOGIN_SCREEN)
            }



        }

    }

}

