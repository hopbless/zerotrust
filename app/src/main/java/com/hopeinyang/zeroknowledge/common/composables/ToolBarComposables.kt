package com.hopeinyang.zeroknowledge.common.composables


import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hopeinyang.zeroknowledge.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroTrustCenterTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    @StringRes title: Int,
    @ColorRes titleColor: Color,
    @ColorRes containerColor: Color,
    @ColorRes scrolledContainerColor: Color,
    @ColorRes navIconColor: Color,
    @ColorRes titleContentColor: Color,
    @ColorRes actionIconColor: Color,
    isAdminAccount: Boolean,
    navigateToSettings: () -> Unit,
    navigateBack: () -> Unit
){
        CenterAlignedTopAppBar(
            modifier = Modifier,
            title = {
                Text(
                    text = stringResource(id = title),
                    modifier
                        .fillMaxWidth()
                        .heightIn()
                        .padding(start = 30.dp),
                    style = TextStyle(
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Normal,
                        color = titleColor,
                    ),
                    textAlign = TextAlign.Left

                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = scrolledContainerColor,
                navigationIconContentColor = navIconColor,
                titleContentColor = titleContentColor,
                actionIconContentColor = actionIconColor
            ),
            navigationIcon = {


                IconButton(onClick = {
                    navigateBack()
                }) {

                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null,
                        modifier = Modifier.size(30.dp)
                )

                }
            },
            actions = {

            },
            scrollBehavior = scrollBehavior


            )



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroTrustBasicToolBar(
    modifier: Modifier = Modifier,
    title: Int,
    navigateToSettings: () -> Unit,
    navigateBack: () -> Unit,
    endAction: () -> Unit
){

    TopAppBar(
        title = {
//            Text(stringResource(title))
//            Text(stringResource(title))
        },
        navigationIcon = {
            IconButton(onClick =  navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }


        },


        actions = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = modifier
            ) {
                IconButton(onClick = endAction) {
                    //Icon(imageVector = Icons.Default.Search, contentDescription = "Action")
                }
                IconButton(
                    onClick = { /*TODO*/ },

                    ) {
                    //Icon(painter = painterResource(id = endActionIcon), contentDescription = null)



                }
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicToolbar(@StringRes title: Int,
                 showArrowBack: Boolean = false,
                 navigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(title)) },

        navigationIcon = {
            if (showArrowBack){

                IconButton(onClick =  navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }


        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionToolbar(
    @StringRes title: Int,
    @DrawableRes endActionIcon: Int,
    modifier: Modifier,
    endAction: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(title)) },

        actions = {
            Box(modifier) {
                IconButton(onClick = endAction) {
                    Icon(painter = painterResource(endActionIcon), contentDescription = "Action")
                }
            }
        }
    )
}