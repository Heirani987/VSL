@file:OptIn(ExperimentalMaterial3Api::class)

package com.vsl.ui.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun NomPatientAutoComplete(
    allPatientNames: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Nom du patient"
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    var clearOnNextFocus by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Synchronise la valeur contrôlée externe
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(value)
        }
    }

    val filteredOptions = remember(textFieldValue.text, allPatientNames) {
        if (textFieldValue.text.isEmpty()) allPatientNames
        else allPatientNames.filter {
            it.contains(textFieldValue.text, ignoreCase = true)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
            if (expanded && textFieldValue.text.isEmpty()) {
                // Affiche tout si champ vide
                textFieldValue = TextFieldValue("")
            }
        }
    ) {
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                onValueChange(it.text)
                expanded = true
            },
            label = { Text(label) },
            modifier = modifier
                .menuAnchor()
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && !expanded) {
                        // Efface le champ à la prise de focus pour saisie rapide
                        textFieldValue = TextFieldValue("")
                        onValueChange("")
                        expanded = true
                    }
                },
            trailingIcon = {
                IconButton(
                    onClick = {
                        // Efface si vide, puis affiche la liste complète
                        if (textFieldValue.text.isNotEmpty()) {
                            textFieldValue = TextFieldValue("")
                            onValueChange("")
                        }
                        expanded = !expanded
                    }
                ) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    expanded = false
                }
            )
        )
        ExposedDropdownMenu(
            expanded = expanded && filteredOptions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            filteredOptions.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        textFieldValue = TextFieldValue(selectionOption)
                        onValueChange(selectionOption)
                        expanded = false
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }
}