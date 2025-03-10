package com.example.wifimapping.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wifimapping.MainActivity
import com.example.wifimapping.components.InputField
import com.example.wifimapping.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, context: MainActivity){
    Scaffold(topBar = {
        TopAppBar(title = { Text("Pendeteksi SSID WiFi", color = Color.Black) },
            colors = TopAppBarDefaults.topAppBarColors(Color(0xFFFFFFFF)
            )
        )
    }) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            MainContent(navController = navController)
        }
    }
}

@Composable
fun MainContent(
    navController: NavController,
    onValChange: (String) -> Unit = {}){

    val inputLength = remember{
        mutableStateOf("")
    }
    val inputWidth = remember{
        mutableStateOf("")
    }
    val inputGrid = remember{
        mutableStateOf("")
    }
    val validStateLength = remember(inputLength.value) {
        inputLength.value.trim().isNotEmpty()
    }
    val validStateWidth= remember(inputWidth.value) {
        inputWidth.value.trim().isNotEmpty()
    }
    val validStateGrid = remember(inputGrid.value) {
        inputGrid.value.trim().isNotEmpty()
    }
    val keyboardController = LocalFocusManager.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp)) {
        Spacer(modifier = Modifier
            .height(5.dp))

        Text("Input Data",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(bottom = 15.dp))

        Card(modifier = Modifier
            .fillMaxWidth()) {
            InputField(valueState = inputLength,
                labelId = "Panjang Ruangan (m)",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validStateLength) return@KeyboardActions
                    onValChange(inputLength.value.trim())
                    keyboardController?.clearFocus()
                }
            )
        }
        Spacer(modifier = Modifier
            .height(5.dp))
        Card(modifier = Modifier
            .fillMaxWidth()) {
            InputField(valueState = inputWidth,
                labelId = "Lebar Ruangan (m)",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validStateWidth) return@KeyboardActions
                    onValChange(inputWidth.value.trim())
                    keyboardController?.clearFocus()
                }
            )
        }
        Spacer(modifier = Modifier
            .height(5.dp))
        Card(modifier = Modifier
            .fillMaxWidth()) {
            InputField(valueState = inputGrid,
                labelId = "Jarak Grid (cm)",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validStateGrid) return@KeyboardActions
                    onValChange(inputGrid.value.trim())
                    keyboardController?.clearFocus()
                }
            )
        }
        Spacer(modifier = Modifier
            .height(5.dp))
        Log.d("validstate","$validStateLength && $validStateWidth && $validStateGrid " +
                "${validStateLength && validStateWidth && validStateGrid}")
        if (validStateLength && validStateWidth && validStateGrid) {
            Button(shape = RoundedCornerShape(5.dp),
                onClick = {
                    var length = inputLength.value
                    var width = inputWidth.value
                    var grid = inputGrid.value
                    navController.navigate(route = Screens.PreviewGridScreen.name+"/$length/$width/$grid"
                    )
                }) {
                Text("Selanjutnya")
            }
        }else{
            Button(shape = RoundedCornerShape(5.dp),
                onClick = {
                }) {
                Text("Selanjutnya")
            }

        }
    }

}