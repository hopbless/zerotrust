package com.hopeinyang.zeroknowledge.common.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.ext.spacer
import com.hopeinyang.zeroknowledge.data.dto.ViewPagerContents

import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenViewPager(

    homeScreenContent: List<ViewPagerContents>?,

    onPageCardClick:(String,)->Unit

){
    /*val tabData = requireNotNull(homeScreenContent?.map { it.pageName }){
        throw  IllegalArgumentException ("Empty pager")
        //onShowHomePager(false)
    }*/

    val tabData = homeScreenContent?.map { it.pageName } ?: emptyList()


    val pagerState = rememberPagerState(
        pageCount = { tabData.size },
        initialPage = 0,
        initialPageOffsetFraction = -0.1f
    )

    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(3)
    )
    val tabIndex = pagerState.currentPage
    val scope = rememberCoroutineScope()


    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            //Log.d("Page change", "Page changed to $page")
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(fraction = 1.0f)

    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier
                .fillMaxWidth(),

            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = {},
            divider = {},

            ) {
            tabData.forEachIndexed { index, text ->
                Tab(
                    selected = tabIndex == index, onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {

                        Text(
                            text = text,
                            fontSize = if (tabIndex == index) 18.sp else 14.sp,
                            fontWeight = if (tabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.background(
                                color = if (tabIndex == index) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceBright
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }


        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 3,
            flingBehavior = fling
        ) { page ->

            Card(
                shape = CardDefaults.elevatedShape,

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onTertiary,
                    contentColor = MaterialTheme.colorScheme.tertiary,

                    ),
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                ).absoluteValue
                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .clip(RoundedCornerShape(20.dp))


            ) {

                when (val tabIndexName = tabData[tabIndex]) {
                    "Home" -> {

                        HomePager(
                            pagerContent = homeScreenContent?.first { it.pageName == tabIndexName }!!,
                            onPageCardClick,


                            )
                    }

                    "Manage" -> {
                        HomePager(
                            pagerContent = homeScreenContent?.first { it.pageName == tabIndexName }!!,
                            onPageCardClick
                        )

                    }
                }

                BottomPageIndicator(pagerState = pagerState)


            }


        }
    }




}

@Composable
private fun HomePager(
    pagerContent: ViewPagerContents,
    onCardClick:(String,)->Unit
){

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(start = 4.dp, end = 4.dp, top = 20.dp, bottom = 20.dp)
            .heightIn(min = 200.dp, max = 500.dp)


    ) {
        Box (
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 8.dp)

            ){
                Text(
                    text = pagerContent.title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = 1.4.em,
                        lineHeightStyle = LineHeightStyle.Default,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)

                )

                Text(
                    text = pagerContent.subTitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default,
                        lineHeight = 1.3.em,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        textAlign = TextAlign.Justify

                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,


                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .width(200.dp)
                        .padding(top = 4.dp)

                )


                Image(
                    painter = rememberAsyncImagePainter(
                        model = pagerContent.pageIconUrl ,
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .requiredSize(100.dp)
                )

            }





        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth(),
            userScrollEnabled = true,
            state = rememberLazyListState()
        ) {
            items(pagerContent.cardDetail.entries.size){cardIndex ->

                HomeScreenCard(
                    imageUrl = pagerContent.cardIcons.entries.elementAt(cardIndex).value,
                    cardTitle = pagerContent.cardDetail.entries.elementAt(cardIndex).key,
                    cardSubTitle = pagerContent.cardDetail.entries.elementAt(cardIndex).value,
                    cardClick = onCardClick,

                )

            }

        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPageIndicator(
    pagerState: PagerState
){

    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration){
                MaterialTheme.colorScheme.onPrimaryContainer

            } else {MaterialTheme.colorScheme.primaryContainer}
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(16.dp)
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PatientAddFormViewPager(
    items: List<Int>,
    firstName:String,
    lastName:String,
    address:String,
    age:String,
    phoneNumber:String,
    dateOfAdmission:String,
    gender:String,
    weight:String,
    height:String,
    bloodType:String,
    systolic: String,
    diastolic: String,
    bloodSugar:String,
    bodyTemp:String,
    diagnosis:String,
    options: List<String>,
    department: String,
    isExpanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    onNewValue: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onDateOfAdmissionChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onBloodTypeChange: (String) -> Unit,
    onSystolicChange:(String)->Unit,
    onDiastolicChange:(String)->Unit,
    onBloodSugarChange: (String) -> Unit,
    onBodyTempChange: (String) -> Unit,
    onDiagnosisChange: (String) -> Unit,
    onSubmitClick: () -> Unit


){

    val pagerState = rememberPagerState(
        pageCount = { items.size},
        initialPage = 0,
        initialPageOffsetFraction = 0.0f
    )

    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(3)
    )

    val scope = rememberCoroutineScope()


    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            //Log.d("Page change", "Page changed to $page")
        }
    }

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        flingBehavior = fling,


    ) {page->

        when(page){
            0->{
                AddPatientFormPageOne(
                    firstName = firstName,
                    lastName = lastName,
                    address = address,
                    age = age,
                    phoneNumber = phoneNumber,
                    onFirstNameChange = onFirstNameChange,
                    onLastNameChange = onLastNameChange,
                    onAddressChange = onAddressChange,
                    onAgeChange = onAgeChange,
                    onPhoneNumberChange = onPhoneNumberChange,
                ){
                    scope.launch {
                       pagerState.animateScrollToPage(1)
                    }
                }
            }
            1->{
                AddPatientFormPageTwo(
                    dateOfAdmission = dateOfAdmission,
                    gender = gender,
                    weight = weight,
                    height = height,
                    bloodType = bloodType,
                    onDateOfAdmissionChange = onDateOfAdmissionChange,
                    onGenderChange = onGenderChange,
                    onWeightChange = onWeightChange,
                    onHeightChange = onHeightChange,
                    onBloodTypeChange = onBloodTypeChange,
                    onNextClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    onPreviousClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                )
            }
            2->{
                AddPatientFormPageThree(
                    systolic = systolic,
                    diastolic = diastolic,
                    bloodSugar = bloodSugar,
                    bodyTemp = bodyTemp,
                    diagnosis = diagnosis,
                    options = options,
                    department = department,
                    isExpanded =  isExpanded,
                    onExpandedChanged = onExpandedChanged,
                    onNewValue = onNewValue,
                    onDismissRequest = onDismissRequest,
                    onSystolicChange = onSystolicChange,
                    onDiastolicChange = onDiastolicChange,
                    onBloodSugarChange = onBloodSugarChange,
                    onBodyTempChange = onBodyTempChange,
                    onDiagnosisChange = onDiagnosisChange,

                    onPreviousClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },

                    onSubmitClick = onSubmitClick
                )
            }

        }



    }

}

@Composable
private fun AddPatientFormPageOne(
    firstName: String,
    lastName: String,
    address: String,
    age: String,
    phoneNumber: String,
    onFirstNameChange:(String)->Unit,
    onLastNameChange:(String)->Unit,
    onAddressChange:(String)->Unit,
    onAgeChange:(String)->Unit,
    onPhoneNumberChange:(String)->Unit,
    onNextClick:()->Unit
){
    Box (
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxSize())
    {
        Text(
            text = "Patient Personal Data",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .align(Alignment.TopStart)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            MyTextFieldComponent(
                value = firstName,
                label = R.string.firstName ,
                painter = painterResource(id = R.drawable.person_3_fill) ,
                onTextChanged = {onFirstNameChange(it)} ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.spacer())

            MyTextFieldComponent(
                value = lastName,
                label = R.string.lastName ,
                painter = painterResource(id = R.drawable.person_3_fill) ,
                onTextChanged = {onLastNameChange(it)} ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.spacer())

            MyTextFieldComponent(
                value = address,
                label = R.string.address ,
                painter = painterResource(id = R.drawable.add_location_icon) ,
                onTextChanged = {onAddressChange(it)} ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.spacer())



            IntegerNumberComponent(
                value = age,
                label = R.string.age ,
                painter = painterResource(id = R.drawable.person_3_fill) ,
                onTextChanged = {onAgeChange(it)} ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.spacer())

            PhoneNumberComponent(
                value = phoneNumber,
                placeholder = "08012345678",
                label = R.string.phone_number,
                painter = painterResource(id = R.drawable.call_asset),
                onTextChanged = {onPhoneNumberChange(it)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

        }

               Button(
                   onClick = onNextClick,
                   shape = RoundedCornerShape(
                       topStart = 40.dp,
                       bottomStart = 5.dp,
                       bottomEnd = 5.dp,
                       topEnd = 5.dp

                   ),
                   modifier = Modifier
                       .align(Alignment.BottomEnd)

               ) {

                   Text(
                       text = "Next",
                       fontSize = 20.sp,
                       fontWeight = FontWeight.SemiBold
                   )
               }


    }


}


@Composable
private fun AddPatientFormPageTwo(
    dateOfAdmission: String,
    gender: String,
    weight: String,
    height: String,
    bloodType: String,
    onDateOfAdmissionChange:(String)->Unit,
    onGenderChange:(String)->Unit,
    onWeightChange:(String)->Unit,
    onHeightChange:(String)->Unit,
    onBloodTypeChange:(String)->Unit,
    onNextClick:()->Unit,
    onPreviousClick:()->Unit
){
    Box (
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxSize())
    {
        Text(
            text = "Patient Bio Data",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .align(Alignment.TopStart)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            MyDatePickerComponent(
                value = dateOfAdmission,
                label = R.string.date_of_admission ,
                onTextChanged = onDateOfAdmissionChange ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.spacer())


            Row (
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ){
                Text(text = "Gender:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Female", fontSize = 16.sp)
                RadioButton(
                    selected = gender=="Female", onClick = { onGenderChange("Female") },
                )
                Text(text = "Male", fontSize = 16.sp)
                RadioButton(
                    selected = gender =="Male", onClick = { onGenderChange("Male")}
                )

            }
            Spacer(modifier = Modifier.spacer())

            DecimalNumberComponent(
                value = weight,
                painter = painterResource(id = R.drawable.fitness_center_fill1_wght400_grad0_opsz24) ,
                onTextChanged = {onWeightChange(it)} ,
                label = R.string.weight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.spacer())

            DecimalNumberComponent(
                value = height,
                painter = painterResource(id = R.drawable.fitness_center_fill1_wght400_grad0_opsz24) ,
                onTextChanged = {onHeightChange(it)} ,
                label = R.string.height,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.spacer())

            MyTextFieldComponent(
                value = bloodType,
                label = R.string.blood_type ,
                painter = painterResource(id = R.drawable.person_3_fill) ,
                onTextChanged = {onBloodTypeChange(it)} ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )


        }

        Button(
            onClick = onNextClick,
            shape = RoundedCornerShape(
                topStart = 40.dp,
                bottomStart = 5.dp,
                bottomEnd = 5.dp,
                topEnd = 5.dp

            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)

        ) {

            Text(
                text = "Next",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onPreviousClick,
            shape = RoundedCornerShape(
                topStart = 5.dp,
                bottomStart = 5.dp,
                bottomEnd = 5.dp,
                topEnd = 40.dp

            ),
            modifier = Modifier
                .align(Alignment.BottomStart)


        ) {

            Text(
                text = "Previous",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }


    }


}

@Composable
private fun AddPatientFormPageThree(
    systolic: String,
    diastolic:String,
    bloodSugar: String,
    bodyTemp: String,
    diagnosis: String,
    options:List<String>,
    department:String,
    isExpanded:Boolean,
    onExpandedChanged:(Boolean)->Unit,
    onNewValue:(String)->Unit,
    onDismissRequest:()->Unit,
    onSystolicChange:(String)->Unit,
    onDiastolicChange:(String)-> Unit,
    onBloodSugarChange:(String)->Unit,
    onBodyTempChange:(String)->Unit,
    onDiagnosisChange:(String)->Unit,
    onSubmitClick:()->Unit,
    onPreviousClick:()->Unit
){
    Box (
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxSize())
    {
        Text(
            text = "Patient Vitals Info",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = MaterialTheme.typography.labelLarge.fontWeight,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .align(Alignment.TopStart)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {

            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ){

                IntegerNumberComponent(
                    value = systolic,
                    painter = painterResource(id = R.drawable.lock_fill0_wght400_grad0_opsz24),
                    onTextChanged = onSystolicChange,
                    label = R.string.systolic_bp ,
                    modifier = Modifier.weight(0.5f)
                )


                Spacer(modifier = Modifier.width(5.dp))

                IntegerNumberComponent(
                    value = diastolic,
                    painter = painterResource(id = R.drawable.lock_fill0_wght400_grad0_opsz24),
                    onTextChanged = onDiastolicChange,
                    label = R.string.diastolic_bp ,
                    modifier = Modifier.weight(0.5f)
                )

            }

            Spacer(modifier = Modifier.spacer())

            DecimalNumberComponent(
                value = bloodSugar,
                painter = painterResource(id = R.drawable.fitness_center_fill1_wght400_grad0_opsz24) ,
                onTextChanged = {onBloodSugarChange(it)} ,
                label = R.string.blood_sugar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.spacer())

            DecimalNumberComponent(
                value = bodyTemp,
                painter = painterResource(id = R.drawable.fitness_center_fill1_wght400_grad0_opsz24) ,
                onTextChanged = {onBodyTempChange(it)} ,
                label = R.string.body_temp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.spacer())

            MyTextFieldComponent(
                value = diagnosis,
                label = R.string.diagnosis ,
                painter = painterResource(id = R.drawable.person_3_fill) ,
                onTextChanged = {onDiagnosisChange(it)} ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.spacer())

            DropdownSelector(
                label = R.string.department,
                options = options,
                selection = department,
                isExpanded = isExpanded,
                onExpandedChanged = onExpandedChanged,
                onNewValue = onNewValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onDismiss = onDismissRequest
            )


        }

        Button(
            onClick = onSubmitClick,
            shape = RoundedCornerShape(
                topStart = 40.dp,
                bottomStart = 5.dp,
                bottomEnd = 5.dp,
                topEnd = 5.dp

            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)

        ) {

            Text(
                text = "Submit",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onPreviousClick,
            shape = RoundedCornerShape(
                topStart = 5.dp,
                bottomStart = 5.dp,
                bottomEnd = 5.dp,
                topEnd = 40.dp

            ),
            modifier = Modifier
                .align(Alignment.BottomStart)


        ) {

            Text(
                text = "Previous",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }


    }


}