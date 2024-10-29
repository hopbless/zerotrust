package com.hopeinyang.zeroknowledge.ui.screens.ManageUsers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.DecimalNumberComponent
import com.hopeinyang.zeroknowledge.common.composables.DropdownSelector
import com.hopeinyang.zeroknowledge.common.composables.IntegerNumberComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUserScreen(
    viewModel: ManageUserViewModel = hiltViewModel(),
    clearAndNavigate: (String)->Unit
){

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Surface (
        color = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box (contentAlignment = Alignment.TopStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp, bottom = 8.dp)
                        ){
                            Text(
                                text = "Manage Users",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(end = 10.dp)
                                    .widthIn(min = 180.dp, max = 250.dp)

                            )

                        }



                    },
                    actions = {
                        /* IconButton(onClick = { *//* do something *//* }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }*/
                    },
                    windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal),
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onBackButtonClick(clearAndNavigate)}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
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

            },
        ) {contentPadding->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Upgrade User Privileges",
                    style = TextStyle(
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                )

                Text(
                    text = "This is sensitive operation, please be careful when specifying user privileges",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(start = 16.dp, top = 50.dp, end = 16.dp)
                )

                DropdownSelector(
                    label = R.string.select_user,
                    options = uiState.userList.map { it.email },
                    selection = uiState.selectedUserEmail,
                    isExpanded = uiState.isUserEmailExpanded,
                    onExpandedChanged = {viewModel.onUserEmailExpandedChanged(it)},
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 120.dp, end = 16.dp),
                    onNewValue = {viewModel.onUserEmailSelected(it)}
                ) {
                    viewModel.onUserEmailExpandedChanged(false)
                }

                Text(
                    text = "Selected User: ${
                        uiState.userList.firstOrNull { it.email == uiState.selectedUserEmail }?.firstName ?: ""} " +
                            (uiState.userList.firstOrNull { it.email == uiState.selectedUserEmail }?.lastName ?: ""),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 180.dp, start = 16.dp)
                )

                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 210.dp)
                ){
                    DropdownSelector(
                        label = R.string.department,
                        options = uiState.departmentList,
                        selection = uiState.department,
                        isExpanded = uiState.isDeptExpanded,
                        onExpandedChanged = { viewModel.onDeptExpandedChanged(it) },
                        onNewValue = { viewModel.onSelectedDept(it) },
                        modifier = Modifier
                            .weight(0.6f),
                        onDismiss = { viewModel.onDeptExpandedChanged(false) }
                    )

                    Spacer(modifier = Modifier.widthIn(8.dp))
                    DropdownSelector(
                        label = R.string.select_role,
                        options = if (uiState.isAdmin) listOf("Nurse", "Doctor", "MD", "Guest") else listOf("Nurse", "Doctor", "Guest"),
                        selection = uiState.role,
                        isExpanded = uiState.isRoleExpanded,
                        onExpandedChanged = { viewModel.onRoleExpandedChanged(it) },
                        onNewValue = { viewModel.onSelectedRole(it) },
                        modifier = Modifier
                            .weight(0.4f),
                        onDismiss = { viewModel.onRoleExpandedChanged(false) }
                    )
                }
                Text(
                    text = "Department: ${
                        uiState.userList.firstOrNull { it.email == uiState.selectedUserEmail }?.department ?: ""}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 0.dp, start = 16.dp)
                        .offset(y = (-110).dp)
                )


                Text(
                    text = "Specialty: ${
                        uiState.userList.firstOrNull { it.email == uiState.selectedUserEmail }?.specialty ?: ""}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 0.dp, start = 16.dp, end = 16.dp)
                        .offset(y = (-110).dp)
                )

                IntegerNumberComponent(
                    showLeadingIcon = false,
                    value = uiState.accessLevel.toString(),
                    painter = painterResource(id = R.drawable.call_asset),
                    onTextChanged = {viewModel.onAccessLevelChanged(it)},
                    placeholder = "0 to 10",
                    label = R.string.access_level,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(170.dp)
                        .padding(top = 0.dp, start = 16.dp)
                        .offset(y = (-50).dp)
                )

                Text(
                    text = "Current Access Level: ${
                        uiState.userList.firstOrNull { it.email == uiState.selectedUserEmail }?.accessLevel ?: ""}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 0.dp, start = 16.dp, end = 16.dp)

                )

               Button(
                   onClick = { viewModel.onUpgradeUserButtonClick(clearAndNavigate) },
                   modifier = Modifier
                       .align(Alignment.CenterEnd)
                       .padding(top = 0.dp, end = 16.dp, start = 160.dp)
                       .heightIn(50.dp)
                       .offset(y = (-50).dp)
               ) {
                   Text(text = "Upgrade User", fontSize = 20.sp, fontWeight = FontWeight.Normal)
               }


                if (uiState.isAdmin && uiState.selectedUserEmail.isNotEmpty()){

                    Box (
                        contentAlignment = Alignment.TopStart,
                        modifier = Modifier
                            .heightIn(min = 80.dp)
                            .align(Alignment.Center)
                            .padding(top = 100.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    ){
                        Text(
                            text = if (uiState.isAdminUser) "Admin Privileges Enabled" else "Admin Privileges Disabled",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        )

                        Text(
                            text = if (uiState.isAdminUser) "Click to Disable Admin Privileges" else "Click to Enable Admin Privileges",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(top = 40.dp)
                        )

                        Switch(
                            checked = uiState.isAdminUser,
                            onCheckedChange = {viewModel.onAdminSwitchClick(it)},
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                        )

                    }

                }

                if (uiState.isAdmin){
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 16.dp)
                            .offset(y = 100.dp)
                    )
                    Text(
                        text = "Update Hospice Geo Coordinates",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 0.dp)
                            .offset(y = 120.dp)
                    )

                    DecimalNumberComponent(
                        value = uiState.latitude,
                        painter = painterResource(id = R.drawable.add_location_icon),
                        onTextChanged = {viewModel.onLatitudeChanged(it)},
                        label = R.string.latitude,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                            .offset(y = 180.dp)
                            .width(170.dp)
                            .height(60.dp)
                    )

                    DecimalNumberComponent(
                        value = uiState.longitude,
                        painter = painterResource(id = R.drawable.add_location_icon),
                        onTextChanged = {viewModel.onLongitudeChanged(it)},
                        label = R.string.longitude,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .offset(y = 180.dp)
                            .width(170.dp)
                            .height(60.dp)
                    )

                    Button(
                        onClick = { viewModel.onHospiceLocationChange() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(top = 0.dp, end = 16.dp, start = 16.dp)
                            .heightIn(50.dp)
                            .offset(y = (-50).dp)
                    ) {
                        Text(text = "Update Location", fontSize = 20.sp, fontWeight = FontWeight.Normal)
                    }
                }


            }

        }
    }
}