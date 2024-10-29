package com.hopeinyang.zeroknowledge.ui.screens.Login


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.BasicButton
import com.hopeinyang.zeroknowledge.common.composables.ClickableLoginTextComponent
import com.hopeinyang.zeroknowledge.common.composables.DividerTextComponent
import com.hopeinyang.zeroknowledge.common.composables.HeadingTextComponent
import com.hopeinyang.zeroknowledge.common.composables.MyTextFieldComponent
import com.hopeinyang.zeroknowledge.common.composables.PasswordTextFieldComponent
import com.hopeinyang.zeroknowledge.data.dto.OtpAuthResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateTo:(String, OtpAuthResult)->Unit,
    openAndPopUp:(String, String)-> Unit
){

    val uiState by viewModel.uiSate
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { CenterAlignedTopAppBar(
            modifier = Modifier,
            title = {
                Text(
                    text = "Zero Trust",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(),
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = MaterialTheme.colorScheme.onPrimary,
                    ),
                    textAlign = TextAlign.Center

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

//                IconButton(onClick = { /*TODO*/ }) {
//                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null,
//                        modifier = Modifier.size(40.dp))
//
//                }
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
                painter = painterResource(id = R.drawable.network_security),
                modifier = Modifier
                    .clip(RectangleShape)
                    //.size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentDescription = null)
            HeadingTextComponent(
                value = "Login",
                textAlign = TextAlign.Left
            )


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
                text = R.string.login,
                enableButton = true,

                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .heightIn(15.dp)
                    .widthIn()
            ) {
                viewModel.onLoginClick(context,navigateTo,openAndPopUp, )
            }
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                viewModel.onRegisterClick(it, openAndPopUp)
            })
        }



    }

}



