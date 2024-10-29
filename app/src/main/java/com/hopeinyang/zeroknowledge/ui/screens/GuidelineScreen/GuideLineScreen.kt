package com.hopeinyang.zeroknowledge.ui.screens.GuidelineScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.composables.DropdownSelector



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidelineScreen(
    viewModel: GuidelineViewModel = hiltViewModel(),
    navigateBack:(String, String)->Unit

){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.state.collectAsStateWithLifecycle()
 

    Surface(
        color = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Box (contentAlignment = Alignment.TopStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp, bottom = 8.dp)
                        ){
                            Text(
                                text = uiState.selectedDocTitle.ifEmpty { "Select article below" },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding( end = 10.dp)
                                    .widthIn(min = 180.dp, max = 250.dp)

                            )

                            Text(
                                text = uiState.selectedDoc.writtenBy.ifEmpty { "" },
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 1.2.em,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .widthIn(max = 200.dp)
                                    .padding(top = 40.dp)

                            )


                            Image(
                                painter =
                                rememberAsyncImagePainter(
                                    model = uiState.selectedDoc.docSubIcon,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.place_holder)
                                    
                                ), contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .requiredSize(60.dp)
                                    .clip(MaterialTheme.shapes.small)
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
                        IconButton(onClick = { viewModel.onBackButtonClick(navigateBack)}) {
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

            modifier = Modifier
        ) { innerPadding->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())

            ) {

                DropdownSelector(
                    label = R.string.all_articles,
                    options = uiState.docList.map { it.docTitle },
                    selection = uiState.selectedDocTitle,
                    isExpanded = uiState.isExpanded,
                    onExpandedChanged = {viewModel.onExpandedChanged(it)},
                    onNewValue = {viewModel.onDocSelected(it)},
                    modifier = Modifier.fillMaxWidth()
                ) {

                    viewModel.onExpandedChanged(false)

                }
                
                
                
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ){
                    Image(
                        painter =
                        rememberAsyncImagePainter(
                            model = uiState.selectedDoc.docSubIcon,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,

                            ), contentDescription = null,
                        modifier = Modifier
                            .requiredSize(40.dp)
                            .clip(MaterialTheme.shapes.small)
                        
                    )
                    Text(
                        text = uiState.selectedDoc.docSubTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 1.2.em,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier

                    )
                }
                HorizontalDivider()

                Box (contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(16.dp)
                ) {
                    Image(
                        painter =
                        rememberAsyncImagePainter(
                            model = uiState.selectedDoc.docCoverImage,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,

                            ), contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                            
                    )
                }
                HorizontalDivider()

                Text(
                    text = if (uiState.selectedDoc.contentBody.isNotEmpty())"Content:" else "",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 1.2.em,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 16.dp)
                )
                
                HorizontalDivider()
                
                Box (
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                    ){
                    Text(
                        text = uiState.selectedDoc.contentBody,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 1.5.em,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Justify
                        )
                    )
                }
                

            }

        }
    }

}