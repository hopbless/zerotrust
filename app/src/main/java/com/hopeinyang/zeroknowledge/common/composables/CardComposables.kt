package com.hopeinyang.zeroknowledge.common.composables


import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hopeinyang.zeroknowledge.R
import com.hopeinyang.zeroknowledge.common.ext.card



@Composable
fun HomeScreenCard(
    imageUrl: String,
    cardTitle: String,
    cardSubTitle: String,
    cardClick: (String,) -> Unit
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp,
            focusedElevation = 4.dp,
            hoveredElevation = 2.dp
        ),
        onClick = {cardClick(cardTitle)},

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 100.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp)

    ) {
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp,)
                .fillMaxHeight()
        ){

           Image(
               painter = rememberAsyncImagePainter(
                   model = imageUrl,
                   contentScale = ContentScale.Crop
               ),
               modifier = Modifier.requiredSize(80.dp)
               , contentDescription =null
           )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .width(200.dp)

            ) {

                Text(
                   cardTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier

                )

                Text(
                    cardSubTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    minLines = 1,
                    style = TextStyle(
                        color =  MaterialTheme.colorScheme.onSurface,
                    ),

                    modifier = Modifier


                )

            }

            Image(
                painter = painterResource(
                id =R.drawable.outline_chevron_right_24),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { cardClick.invoke(cardTitle) }
            )


        }



    }




    }




@Composable
fun CircleShapeBgr(){
    Box (modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .background(color = Color.Cyan)
    )
}


@Composable
fun DangerousCardEditor(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    content: String,
    modifier: Modifier,
    onEditClick: () -> Unit
) {
    CardEditor(title, icon, content, onEditClick, MaterialTheme.colorScheme.primary, modifier)
}

@Composable
fun RegularCardEditor(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    content: String,
    modifier: Modifier,
    onEditClick: () -> Unit
) {
    CardEditor(title, icon, content, onEditClick, MaterialTheme.colorScheme.primary, modifier)
}



@Composable
private fun CardEditor(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    content: String,
    onEditClick: () -> Unit,
    highlightColor: Color,
    modifier: Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.onPrimary,
        )
        ,
        modifier = modifier,
        onClick = onEditClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) { Text(stringResource(title), color = highlightColor) }

            if (content.isNotBlank()) {
                Text(text = content, modifier = Modifier.padding(16.dp, 0.dp))
            }

            Icon(painter = painterResource(icon), contentDescription = "Icon", tint = highlightColor)
        }
    }
}

@Composable
fun CardSelector(
    @StringRes label: Int,
    options: List<String>,
    selection: String,
    modifier: Modifier,
    onNewValue: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    ) {
        //DropdownSelector(label, options, selection, Modifier.dropdownSelector(), onNewValue)
    }
}





@Composable
fun SignOutCard(signOut: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    RegularCardEditor(R.string.sign_out, R.drawable.lock_fill0_wght400_grad0_opsz24, "Sign Out", Modifier.card()) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.sign_out_title)) },
            text = { Text(stringResource(R.string.sign_out_description)) },
            dismissButton = { DialogCancelButton(R.string.cancel) { showWarningDialog = false } },
            confirmButton = {
                DialogConfirmButton(R.string.sign_out) {
                    signOut()
                    showWarningDialog = false
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}

@Composable
fun MFACard(
    @StringRes label: Int,
    painter: Painter,
    isMFASwitchEnabled:Boolean = false,
    phoneNumber:String,
    highlightColor: Color,
    switchCheck:Boolean,
    onSwitchChange:(Boolean)->Unit,
    modifier: Modifier,
    onTextChange:(String)->Unit,
    enableMFA:(String)->Unit
)
{
    var showWarningDialog by remember { mutableStateOf(switchCheck) }

    Card(
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = modifier,
        onClick = {
            showWarningDialog = true
            onSwitchChange(true)
        }
    ){
        Box (
           contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .heightIn(min = 50.dp)
        ){
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
            )

            Text( "Enable MFA",
                fontSize = 20.sp,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                    color = highlightColor,

                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 50.dp)
                
            )

            Text(
                text = "Add Second Authentication",
                fontSize = 16.sp,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Left

                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 50.dp)
                )

            Switch(
                modifier = Modifier.align(Alignment.CenterEnd),
                checked = if (isMFASwitchEnabled) true else switchCheck,
                onCheckedChange = {
                    showWarningDialog = it
                    if (isMFASwitchEnabled){
                        onSwitchChange(true)
                    }
                    onSwitchChange(it)
                }
            )

        }
    }

    if (showWarningDialog && !isMFASwitchEnabled){
        AlertDialog(
            title = { Text(stringResource(R.string.enableMFA)) },
            text = {
                OutlinedTextField(
                    modifier = modifier
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
                    /*keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Phone
                    )*/
                    readOnly = true,
                    singleLine = true,
                    maxLines = 1,
                    value =phoneNumber ,
                    onValueChange = {onTextChange(it)},
                    leadingIcon = {
                        Icon(painter = painter, contentDescription = "")
                    },
                )
            },
            //text = { Text(stringResource(R.string.sign_out_description)) },
            dismissButton = { DialogCancelButton(R.string.cancel) {
                showWarningDialog = false
                onSwitchChange(false)
            }
                            },
            confirmButton = {
                DialogConfirmButton(R.string.ok) {
                    enableMFA(phoneNumber)
                    showWarningDialog = false
                }
            },
            onDismissRequest = {
                showWarningDialog = false
               onSwitchChange(false)
            }
        )
    }




}


@Composable
fun DeleteMyAccountCard(deleteMyAccount: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(false) }

    DangerousCardEditor(
        R.string.delete_my_account,
        R.drawable.lock_fill0_wght400_grad0_opsz24,
        "Delete Account",
        Modifier.card()
    ) {
        showWarningDialog = true
    }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(R.string.delete_account_title)) },
            text = { Text(stringResource(R.string.delete_account_description)) },
            dismissButton = { DialogCancelButton(R.string.cancel) { showWarningDialog = false } },
            confirmButton = {
                DialogConfirmButton(R.string.delete_my_account) {
                    deleteMyAccount()
                    showWarningDialog = false
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}


@Composable
fun SettingsItemCard(
    @StringRes title: Int,
    imageVector: ImageVector,
    @StringRes subTitle:Int,
    onEditClick: () -> Unit,
    highlightColor: Color,
    modifier: Modifier,
    addSwitch:Boolean = false,
    switchCheck: Boolean = false,
    onSwitchChange:(Boolean)->Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.onPrimary,
        )
        ,
        modifier = modifier,
        onClick = onEditClick
    ) {
        Box(
           contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .heightIn(min = 50.dp)
        ) {


            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
            )

                Text( stringResource(id = title),
                    fontSize = 20.sp,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left,
                        color = highlightColor
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 50.dp)
                )

                Text(
                    text = stringResource(id = subTitle),
                    fontSize = 16.sp,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Left

                    ),

                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 50.dp, top = 30.dp)


                )

            if (addSwitch){
                Switch(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = switchCheck,
                    onCheckedChange = onSwitchChange
                )
            }



        }
    }
}

@Composable
fun ZeroTrustUserHeaderCard(
    userImageUrl:String,
    userFullName:String,
    trustScore:String,
    role:String,
    isGPSEnable:Boolean = false,
    onLocationIconClick:()->Unit ={}
){

   ElevatedCard (
       colors = CardDefaults.cardColors(
           containerColor = MaterialTheme.colorScheme.tertiary,
           contentColor = MaterialTheme.colorScheme.onTertiary,

       ),
       shape = CardDefaults.outlinedShape,
       elevation = CardDefaults.elevatedCardElevation(
           defaultElevation = 8.dp,
           pressedElevation = 8.dp,
           focusedElevation = 4.dp,
           hoveredElevation = 8.dp
       ),
       modifier = Modifier
           .fillMaxWidth()
           .heightIn(min = 100.dp)
           .padding(start = 16.dp, top = 5.dp, end = 16.dp)
   ) {

       Box (
           contentAlignment = Alignment.TopStart,
           modifier = Modifier
               .fillMaxWidth()
               .heightIn(min = 100.dp)
               .padding(start = 8.dp, end = 8.dp)

       ){
           if (userImageUrl.isNotEmpty()) {
               Image(
                   painter = rememberAsyncImagePainter(
                       model = userImageUrl,
                       placeholder = painterResource(id = R.drawable.place_holder)
                   ),
                   contentDescription = null,
                   modifier = Modifier
                       .clip(CircleShape)
                       .align(Alignment.CenterStart)
                       .requiredSize(70.dp)

               )
           }else{
               Image(
                   painter =  painterResource(id = R.drawable.place_holder),
                   contentDescription = null,
                   modifier = Modifier
                       .clip(CircleShape)
                       .align(Alignment.CenterStart)
                       .requiredSize(70.dp)

               )
           }

           Text(
               text = "Name: $userFullName",

               style = TextStyle(
                   fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                   fontSize = MaterialTheme.typography.headlineMedium.fontSize,


               ),
               maxLines = 1,
               overflow = TextOverflow.Ellipsis,

               modifier = Modifier
                   .align(Alignment.TopStart)
                   .padding(start = 80.dp, top = 2.dp)
           )

           Text(
               text = "Specialty: $role",
               style = TextStyle(
                   fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                   fontSize = MaterialTheme.typography.headlineSmall.fontSize,
               ),
               modifier = Modifier
                   .align(Alignment.TopStart)
                   .padding(start = 80.dp, top = 32.dp)
           )

           Text(
               text = "Trust Score: $trustScore",
               style = TextStyle(
                   fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                   fontSize = MaterialTheme.typography.headlineSmall.fontSize,
               ),
               modifier = Modifier
                   .align(Alignment.TopStart)
                   .padding(start = 80.dp, top = 62.dp)
           )

           IconButton(
               onClick = onLocationIconClick,
               modifier = Modifier
                   .align(Alignment.BottomEnd)

           ) {
               Icon(
                   imageVector = Icons.Default.LocationOn,
                   contentDescription = "Location Icon",
                   tint = if (isGPSEnable) MaterialTheme.colorScheme.onTertiary else Color.Gray,

               )

           }

       }



   }
}


