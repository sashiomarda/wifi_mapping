package com.sashiomarda.wifimapping.ui.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sashiomarda.wifimapping.R
import com.sashiomarda.wifimapping.WifiMappingTopAppBar
import com.sashiomarda.wifimapping.ui.home.HomeDestination
import com.sashiomarda.wifimapping.ui.navigation.NavigationDestination
import kotlinx.coroutines.delay

object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.room_params_entry_title
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onLoginFailed: (Exception?) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
) {
    val context = LocalContext.current
    val auth: FirebaseAuth = Firebase.auth
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingGoogle by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Konfigurasi Google Sign-In
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Ambil dari strings.xml
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { taskAuth ->
                    isLoadingGoogle = false
                    if (taskAuth.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        errorMessage = taskAuth.exception?.message.toString()
                        onLoginFailed(taskAuth.exception)
                    }
                }
        } catch (e: ApiException) {
            errorMessage = e.toString()
            isLoadingGoogle = false
            onLoginFailed(e)
        }
    }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            WifiMappingTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (showForgotPasswordDialog) {
                AlertDialog(
                    onDismissRequest = { showForgotPasswordDialog = false },
                    title = { Text("Reset Password") },
                    text = {
                        Column {
                            Text("Masukkan email untuk menerima link reset password.")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = forgotEmail,
                                onValueChange = { forgotEmail = it },
                                label = { Text("Email") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (forgotEmail.isNotEmpty()) {
                                sendPasswordResetEmail(
                                    email = forgotEmail,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Email reset password telah dikirim",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        showForgotPasswordDialog = false
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }) {
                            Text("Kirim")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showForgotPasswordDialog = false
                        }) {
                            Text("Batal")
                        }
                    }
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Login", fontSize = 28.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                TextButton(onClick = {
                    showForgotPasswordDialog = true
                }) {
                    Text("Lupa Password?")
                }

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                isLoading = true
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            onLoginSuccess()
                                        } else {
                                            errorMessage = "Email dan Password salah"
                                        }
                                    }
                            } else {
                                errorMessage = "Email dan password tidak boleh kosong"
                            }
                        },
                    ) {
                        Text("Login")
                    }
                }

                TextButton(onClick = onNavigateToRegister) {
                    Text("Belum punya akun? Daftar di sini")
                }

                Text("atau", fontSize = 12.sp)

                if (isLoadingGoogle) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            isLoadingGoogle = true
                            signInLauncher.launch(googleSignInClient.signInIntent)
                        },
                    ) {
                        Text("Login dengan Google")
                    }
                }
            }
        }
    }
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context,it, Toast.LENGTH_LONG).show()
            delay(2000)
            errorMessage = null
        }
    }
}

fun sendPasswordResetEmail(
    email: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError(task.exception?.message ?: "Gagal mengirim email reset password")
            }
        }
}
