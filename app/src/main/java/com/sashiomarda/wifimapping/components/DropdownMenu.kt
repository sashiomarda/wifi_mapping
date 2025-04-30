package com.sashiomarda.wifimapping.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DropDownMenu(
    menuItemData: List<Int>,
    selectedLayer: (Int) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    var layerTxt by remember { mutableStateOf("1") }
    Box(
        modifier = Modifier
            .clickable(onClick = {
                if (menuItemData.size > 1) {
                    expanded = !expanded
                }
            })
    ) {
        Row {
            Text(
                "Layer ${layerTxt}",
                fontWeight = FontWeight.Bold
            )
            if (menuItemData.size > 1) {
                if (!expanded) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Arrow Down"
                    )
                } else {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Arrow Up"
                    )
                }
            }
        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        menuItemData.forEach { option ->
            DropdownMenuItem(
                text = { Text("Layer $option") },
                onClick = {
                    layerTxt = option.toString()
                    expanded = !expanded
                    selectedLayer(option)
                }
            )
        }
    }
}