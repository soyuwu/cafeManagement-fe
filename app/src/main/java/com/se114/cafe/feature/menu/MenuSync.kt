package com.se114.cafe.feature.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay

internal data class MenuSync(val loading: Boolean, val error: String?, val retry: () -> Unit)

@Composable
internal fun rememberMenuSync(refreshProducts: suspend () -> Unit, refreshAvailability: suspend () -> Unit): MenuSync {
    var attempt by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle, attempt) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var needsSnapshot = true
            loading = true
            while (true) {
                try {
                    if (needsSnapshot) refreshProducts() else refreshAvailability()
                    needsSnapshot = false
                    loadError = null
                } catch (e: java.io.IOException) {
                    loadError = "Không đồng bộ được thực đơn"
                } catch (e: retrofit2.HttpException) {
                    loadError = "Không đồng bộ được thực đơn (HTTP ${e.code()})"
                } finally {
                    loading = false
                }
                delay(30_000)
            }
        }
    }
    return MenuSync(loading, loadError) { attempt++ }
}

@Composable
internal fun MenuSyncStatus(sync: MenuSync) {
    if (sync.loading) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
    } else if (sync.error != null) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(sync.error, Modifier.weight(1f))
            TextButton(onClick = sync.retry) { Text("Thử lại") }
        }
    }
}
