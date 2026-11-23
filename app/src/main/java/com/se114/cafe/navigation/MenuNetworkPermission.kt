package com.se114.cafe.navigation

import android.Manifest
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
internal fun menuNetworkReady(back: () -> Unit): Boolean {
    val activity = checkNotNull(LocalActivity.current)
    if (Build.VERSION.SDK_INT < 37 || activity.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE == 0) return true

    val permission = Manifest.permission.ACCESS_LOCAL_NETWORK
    fun hasPermission() = activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    var granted by remember { mutableStateOf(hasPermission()) }
    var requested by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        requested = true
        granted = it
    }
    LifecycleResumeEffect(activity) {
        granted = hasPermission()
        onPauseOrDispose {}
    }
    if (granted) return true

    val openSettings = requested && !activity.shouldShowRequestPermissionRationale(permission)
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        Text("Cho phép truy cập mạng cục bộ để tải thực đơn từ máy chạy backend.")
        Button(onClick = {
            if (openSettings) {
                activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", activity.packageName, null)))
            } else {
                launcher.launch(permission)
            }
        }) { Text(if (openSettings) "Mở cài đặt quyền" else "Cho phép kết nối") }
        TextButton(onClick = back) { Text("Quay lại") }
    }
    return false
}
