package com.example.wifimapping

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.wifimapping.ui.chooseWifi.PERMISSIONS_REQUEST_CODE
import com.example.wifimapping.ui.theme.WifiMappingTheme
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import kotlin.apply

class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WifiMappingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    askTurnOnLocation(PRIORITY_HIGH_ACCURACY, this)
                    WifiMappingApp()
                }
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode:Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode,data)
        if (requestCode == 100){
            if (resultCode == RESULT_OK){
                Log.d("onActivityResult","onActivityResult gps enable")
            }else{
                Log.d("onActivityResult","onActivityResult gps declined")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        askTurnOnLocation(PRIORITY_HIGH_ACCURACY, this)
    }
}

fun askTurnOnLocation(PRIORITY_HIGH_ACCURACY: Int, context: Context) {
    val activity = context as Activity
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        if (checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(activity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE
                ),
                PERMISSIONS_REQUEST_CODE
            );
        }
    }
    val locationRequest = LocationRequest.create().apply {
        interval = 3000
        priority = PRIORITY_HIGH_ACCURACY
    }
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val task = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())
    task.addOnSuccessListener { response ->
        val states = response.locationSettingsStates
        if (states!!.isLocationPresent){
            Log.d("askTurnOnLocation", "askTurnOnLocation ready")
        }
    }
        .addOnFailureListener { e ->
            val statusCode = (e as ResolvableApiException).statusCode
            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                try {
                    Log.d("askTurnOnLocation", "askTurnOnLocation not ready")
                    e.startResolutionForResult(activity,100)
                }catch (sendEx: IntentSender.SendIntentException){}
            }
        }
}