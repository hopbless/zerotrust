package com.hopeinyang.zeroknowledge.common.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun DropdownContextMenu(
    options: List<String>,
    isExpanded: Boolean,
    modifier: Modifier,
    onExpandedChanged:(Boolean)-> Unit,
    onActionClick: (String) -> Unit,

) {
    //var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        modifier = modifier,
        onExpandedChange =  {onExpandedChanged(it) }
    ) {

           Icon(
               imageVector = Icons.Filled.MoreVert,
               contentDescription = "More",
               modifier = modifier
                   .padding(8.dp, 0.dp)
                   .menuAnchor(),
           )


        ExposedDropdownMenu(
            modifier = Modifier.width(180.dp),
            expanded = isExpanded,
            onDismissRequest = { onExpandedChanged(false) }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    { Text(text = selectionOption) },
                    onClick = {
                        onActionClick(selectionOption)
                    }
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun DropdownSelector(
    @StringRes label: Int,
    options: List<String>,
    selection: String,
    isExpanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier,
    onNewValue: (String,) -> Unit,
    onDismiss: () -> Unit,
) {


    ExposedDropdownMenuBox(
        expanded = isExpanded,
        modifier = modifier,
        onExpandedChange = { onExpandedChanged(it) }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
               ,
            readOnly = true,
            value = selection,
            onValueChange = {},
            label = { Text(stringResource(label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isExpanded) },
            colors = dropdownColors()
        )

        ExposedDropdownMenu(
            expanded = isExpanded, onDismissRequest = onDismiss,
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    {

                        Text(text = selectionOption,)
                    },
                    onClick = {
                        //val index = options.indexOf(selectionOption)
                        onNewValue(selectionOption)

                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dropdownColors(): TextFieldColors {
    return ExposedDropdownMenuDefaults.textFieldColors(
        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.primary
    )
}