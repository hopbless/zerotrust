package com.hopeinyang.zeroknowledge.common.composables

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.data.dto.Response
import com.hopeinyang.zeroknowledge.utils.convertMillisToDate
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.Date
import java.util.Locale


@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ), color = MaterialTheme.colorScheme.onSecondaryContainer,
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadingTextComponent(
    value: String,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
            color = MaterialTheme.colorScheme.tertiary,
        ),
        textAlign = textAlign


    )
}


@Composable
fun MyTextFieldComponent(
    showLeadingIcon:Boolean = true,
    value: String,
    @StringRes label: Int = 0,
    painter: Painter,
    onTextChanged: (String) -> Unit = {},
    modifier: Modifier

) {

//    val textValue by remember {
//        mutableStateOf("")
//    }
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .clip(MaterialTheme.shapes.small),
        label = {
            Text(
                text = stringResource(id = label),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
//        placeholder = { Text(stringResource(text)) },
//        colorss = TextFieldDefaults.outlinedTextFieldColors(
//            focusedBorderColor = MaterialTheme.colorScheme.primary,
//            focusedLabelColor = MaterialTheme.colorScheme.primary,
//            cursorColor = MaterialTheme.colorScheme.primary,
//            containerColor = MaterialTheme.colorScheme.onPrimary
//        ),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            cursorColor = MaterialTheme.colorScheme.inversePrimary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary

        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines = 1,
        value = value,
        onValueChange = {
            onTextChanged(it)
        },

        leadingIcon = {
            if (showLeadingIcon)
                Icon(painter = painter, contentDescription = "")

        },
        )
}



@Composable
fun PasswordTextFieldComponent(
    value: String,
    painter: Painter,
    @StringRes text: Int,
    modifier: Modifier,
    onTextSelected: (String) -> Unit,
) {

    val localFocusManager = LocalFocusManager.current
//    val password = remember {
//        mutableStateOf("")
//    }

    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        label = {
            Text(
                text = stringResource(id = text),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            cursorColor = MaterialTheme.colorScheme.inversePrimary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary

        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        maxLines = 1,
        value = value,
        onValueChange = {
            onTextSelected(it)
        },
        leadingIcon = {
            Icon(painter = painter, contentDescription = "")
        },
        trailingIcon = {

            val iconImage = if (passwordVisible.value) {
                painterResource(id = R.drawable.password_visibility)

            } else {
                painterResource(id = R.drawable.password_invisibility)

            }

            val description = if (passwordVisible.value) {
                stringResource(id = R.string.hide_password)
            } else {
                stringResource(id = R.string.show_password)
            }

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(painter = iconImage, contentDescription = description)
            }

        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),

        )
}

@Composable
fun CheckboxComponent(
    value: String,
    onTextSelected: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val checkedState = remember {
            mutableStateOf(false)
        }

        Checkbox(checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = !checkedState.value
                onCheckedChange.invoke(it)
            })

        ClickableTextComponent(value = value, onTextSelected)
    }
}

@Composable
fun ClickableTextComponent(value: String, onTextSelected: (String) -> Unit) {
    val initialText = "By continuing you accept our "
    val privacyPolicyText = "Privacy Policy"
    val andText = " and "
    val termsAndConditionsText = "Term of Use"


    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            pushStringAnnotation(tag = initialText, annotation = initialText)
            append(initialText)
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            pushStringAnnotation(tag = privacyPolicyText, annotation = privacyPolicyText)
            append(privacyPolicyText)
        }

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            pushStringAnnotation(tag = andText, annotation = andText)
            append(andText)
        }

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            pushStringAnnotation(tag = termsAndConditionsText, annotation = termsAndConditionsText)
            append(termsAndConditionsText)
        }
    }

    ClickableText(text = annotatedString, onClick = { offset ->

        annotatedString.getStringAnnotations(offset, offset)
            .firstOrNull()?.also { span ->
                Log.d("ClickableTextComponent", "{${span.item}}")

                if ((span.item == termsAndConditionsText) || (span.item == privacyPolicyText)) {
                    onTextSelected(span.item)
                }
            }

    })
}

@Composable
fun ButtonComponent(
    value: String,
    modifier: Modifier = Modifier,
    onButtonClicked: () -> Unit,

) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        onClick = {
            onButtonClicked.invoke()
        },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = RoundedCornerShape(50.dp),

        ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary
                        )
                    ),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

        }

    }
}

@Composable
fun DividerTextComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.primary,
            thickness = 1.2.dp
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(R.string.or),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = MaterialTheme.colorScheme.primary,
            thickness = 1.2.dp
        )


    }
}


@Composable
fun ClickableLoginTextComponent(tryingToLogin: Boolean = true, onTextSelected: (String) -> Unit) {
    val initialText =
        if (tryingToLogin) "Already have an account? " else "Don’t have an account yet? "
    val loginText = if (tryingToLogin) "Login" else "Register"

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            pushStringAnnotation(tag = initialText, annotation = initialText)
            append(initialText)
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            pushStringAnnotation(tag = loginText, annotation = loginText)
            append(loginText)
        }
    }

    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 21.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),
        text = annotatedString,
        onClick = { offset ->

            annotatedString.getStringAnnotations(offset, offset)
                .firstOrNull()?.also { span ->
                    Log.d("ClickableTextComponent", "{${span.item}}")

                    if (span.item == loginText) {
                        onTextSelected(span.item)
                    }
                }

        },
    )
}

@Composable
fun UnderLinedTextComponent(value: String, onClick: ()-> Unit) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .clickable { onClick.invoke() },

        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ), color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline
    )

}



@Composable
fun ComposableLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent:(LifecycleOwner, Lifecycle.Event)-> Unit
){
    DisposableEffect(key1 = lifecycleOwner){
        val observer = LifecycleEventObserver{source, event ->
            onEvent(source, event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

}



@Composable
fun PhoneNumberComponent(
    value: String,
    placeholder: String = "8123456789",
    @StringRes label: Int,
    painter: Painter,
    onTextChanged: (String) -> Unit,
    modifier: Modifier

) {

//    val textValue by remember {
//        mutableStateOf("")
//    }
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            .clip(MaterialTheme.shapes.small),
        label = { Text(text = stringResource(id = label),
            color = MaterialTheme.colorScheme.onSurface)},
        placeholder = { Text(text = placeholder)},
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            cursorColor = MaterialTheme.colorScheme.inversePrimary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary

        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Phone
        ),
        singleLine = true,
        maxLines = 1,
        value = value,
        onValueChange = {
            onTextChanged(it)
        },
        leadingIcon = {
            Icon(painter = painter, contentDescription = "")
        },

        )
}

@Composable
fun IntegerNumberComponent(
    showLeadingIcon:Boolean = true,
    value: String,
    painter: Painter,
    placeholder: String = "",
    onTextChanged: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier

) {

//    val textValue by remember {
//        mutableStateOf("")
//    }
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            //.fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        label = { Text(text = stringResource(id = label),
            color = MaterialTheme.colorScheme.onSurface)},
        placeholder = { Text(text = placeholder)},
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            cursorColor = MaterialTheme.colorScheme.inversePrimary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary

        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        maxLines = 1,
        value = value,
        onValueChange = {
            onTextChanged(it)
        },

        leadingIcon = {
            if (showLeadingIcon)
                Icon(painter = painter, contentDescription = "")
        } ,

        )
}

@Composable
fun DecimalNumberComponent(
    value: String,
    painter: Painter,
    onTextChanged: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier

) {

//    val textValue by remember {
//        mutableStateOf("")
//    }
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier
            //.fillMaxWidth()
            .clip(MaterialTheme.shapes.small),
        label = { Text(text = stringResource(id = label),
            color = MaterialTheme.colorScheme.onSurface)},
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.inversePrimary,
            cursorColor = MaterialTheme.colorScheme.inversePrimary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary

        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        maxLines = 1,
        value = value,
        onValueChange = {
            onTextChanged(it)
        },
        leadingIcon = {
            Icon(painter = painter, contentDescription = "")
        },

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerComponent(
    value: String,
    onTextChanged: (String) -> Unit,
    @StringRes label: Int,
    modifier: Modifier
) {

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {

        convertMillisToDate(it)
    } ?: ""

        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = label)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary
            ),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = {
                    onTextChanged(selectedDate)
                    showDatePicker = false
                                   },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .offset(y = 60.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }



}

@Composable
fun NameAndSelectorRow(
    modifier: Modifier = Modifier,
    label: String,
    items:List<String>,
    value: String,
    onSelected: (String) -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
    {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier.padding(end = 4.dp)

        )

        /*DropdownSelector(
            label = R.string.all_patients,
            options = items,
            selection = value,
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))


        ) {
            onSelected(it)
        }*/


    }
}




// Custom compose pin input

enum class ComposePinInputStyle {
    BOX,
    UNDERLINE
}


@Composable
fun ComposePinInput(
    value: String,
    onValueChange: (String) -> Unit,
    maxSize: Int = 4,
    mask: Char? = null,
    isError: Boolean = false,
    onPinEntered: ((String)-> Unit)? = null,
    cellShape: Shape = RoundedCornerShape(4.dp),
    fontColor: Color = Color.LightGray,
    cellBorderColor: Color = Color.Gray,
    rowPadding: Dp = 8.dp,
    cellPadding: Dp = 16.dp,
    cellSize: Dp = 50.dp,
    cellBorderWidth: Dp = 1.dp,
    textFontSize: TextUnit = 20.sp,
    focusedCellBorderColor: Color = Color.DarkGray,
    errorBorderColor: Color = Color.Red,
    cellBackgroundColor: Color = Color.Transparent,
    cellColorOnSelect: Color = Color.Transparent,
    borderThickness: Dp = 2.dp,
    style: ComposePinInputStyle = ComposePinInputStyle.BOX,
    modifier: Modifier,

) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusedState = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        BasicTextField(
            value = value,
            onValueChange = { text ->
                if (text.length <= maxSize) {
                    onValueChange(text)
                    if (text.length == maxSize) {

                        keyboardController?.hide()

                        onPinEntered?.invoke(text)
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            modifier = modifier
                .alpha(0.01f)
                .onFocusChanged {

                    focusedState.value = it.isFocused
                }
                .focusRequester(focusRequester),

            textStyle = TextStyle.Default.copy(color = Color.Transparent),

            )

        // UI for the Pin
        val boxWidth = cellSize
        Row(
            horizontalArrangement = Arrangement.spacedBy(cellPadding),
            modifier = modifier
                .padding(rowPadding)
        ) {
            repeat(maxSize) { index ->
                val isActiveBox = focusedState.value && index == value.length

                if (style == ComposePinInputStyle.BOX) {
                    // Box Style Pin field logic starts from here
                    Box(
                        modifier = modifier
                            .size(cellSize)
                            .background(
                                color = if (index < value.length) cellColorOnSelect else cellBackgroundColor,
                                shape = cellShape
                            )
                            .border(
                                width = cellBorderWidth,
                                color = when {
                                    isError -> errorBorderColor
                                    isActiveBox -> focusedCellBorderColor
                                    else -> cellBorderColor
                                },
                                shape = cellShape
                            )
                            .clickable(
                                indication = null, // Disable ripple effect
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                keyboardController?.show()
                                focusRequester.requestFocus()
                            }
                    ) {
                        if (index < value.length) {
                            val displayChar = mask ?: value[index]
                            Text(
                                text = displayChar.toString(),
                                modifier = modifier.align(Alignment.Center),
                                fontSize = textFontSize,
                                color = fontColor
                            )
                        }
                    }


                } else {
                    // Underline style logic here
                    Box(
                        modifier = modifier
                            .size(boxWidth, cellSize + borderThickness)
                            .background(color = if (index < value.length) cellColorOnSelect else cellBackgroundColor)
                            .clickable(
                                indication = null, // Disable ripple effect
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                keyboardController?.show()
                                focusRequester.requestFocus()
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                            drawLine(
                                color = when {
                                    isError -> errorBorderColor
                                    isActiveBox -> focusedCellBorderColor
                                    else -> cellBorderColor
                                },
                                start = Offset(x = 0f, y = size.height - borderThickness.toPx()),
                                end = Offset(
                                    x = size.width,
                                    y = size.height - borderThickness.toPx()
                                ),
                                strokeWidth = borderThickness.toPx()
                            )
                        })

                        if (index < value.length) {
                            val displayChar = mask ?: value[index]
                            val lineHeightDp: Dp = with(LocalDensity.current) {
                                textFontSize.toDp()
                            }
                            Text(
                                text = displayChar.toString(),
                                modifier = modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = (cellSize - lineHeightDp) / 2),
                                fontSize = textFontSize,
                                color = fontColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinInputModalBottomSheet(
    pin: String,
    sheetState: SheetState,
    onPinEntered: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onPinChange: (String) -> Unit,
    modifier: Modifier

){
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        dragHandle = {BottomSheetDefaults.DragHandle()},
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp)

    ) {

        Box (
            contentAlignment = Alignment.Center,
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(min = 200.dp)
        ){

            Text(
                text = "Enter your pin",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )

            HorizontalDivider(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
                    .align(Alignment.TopStart),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onTertiary
            )

            ComposePinInput(
                value = pin,
                onValueChange = onPinChange,
                onPinEntered = onPinEntered,
                fontColor = MaterialTheme.colorScheme.onSurface,
                cellBorderColor = MaterialTheme.colorScheme.onTertiary,
                focusedCellBorderColor = MaterialTheme.colorScheme.onTertiaryContainer,
                mask = '*',
                style = ComposePinInputStyle.BOX,
                modifier = modifier
                    .align(Alignment.Center)
            )

        }
    }
}
