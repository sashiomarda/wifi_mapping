package com.sashiomarda.wifimapping.ui.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.ui.home.HomeDestination
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination

object ProfileDestination : NavigationDestination {
    override val route = "profile"
    override val titleRes = R.string.room_params_entry_title
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val userId = user?.uid

    var profile by remember { mutableStateOf(UserProfile()) }
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    val fullNameState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val addressState = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) {innerPadding ->
        // Fetch data
        LaunchedEffect(userId) {
            if (userId != null) {
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            profile = doc.toObject(UserProfile::class.java) ?: UserProfile()
                        }
                        fullNameState.value = profile.fullName
                        phoneState.value = profile.phone
                        addressState.value = profile.address
                        isLoading = false
                    }
                    .addOnFailureListener {
                        message = "Gagal memuat profil"
                        isLoading = false
                    }
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Profil Pengguna", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Email: ${user?.email ?: "-"}")

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = fullNameState.value,
                    onValueChange = { fullNameState.value = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneState.value,
                    onValueChange = { phoneState.value = it },
                    label = { Text("Nomor HP") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = addressState.value,
                    onValueChange = { addressState.value = it },
                    label = { Text("Alamat") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Button(onClick = {
                        val updated = UserProfile(
                            fullName = fullNameState.value,
                            phone = phoneState.value,
                            address = addressState.value
                        )
                        userId?.let {
                            db.collection("users").document(it).set(updated)
                                .addOnSuccessListener {
                                    profile = updated
                                    isEditing = false
                                    message = "Profil berhasil diperbarui"
                                }
                                .addOnFailureListener {it ->
                                    message = "Gagal menyimpan profil"
                                }
                        }
                    }) {
                        Text("Simpan")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(onClick = {
                        isEditing = false
                    }) {
                        Text("Batal")
                    }
                }
            } else {
                Text("Nama: ${profile.fullName}")
                Text("HP: ${profile.phone}")
                Text("Alamat: ${profile.address}")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    isEditing = true
                }) {
                    Text("Edit Profil")
                }
            }

            message?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = Color.Green)
            }
        }
    }
}

data class UserProfile(
    val fullName: String = "",
    val phone: String = "",
    val address: String = ""
)