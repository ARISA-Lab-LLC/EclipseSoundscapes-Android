package org.eclipsesoundscapes.extensions

import android.content.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope = GlobalScope,
    block: suspend () -> Unit
) {
    val result = goAsync()
    coroutineScope.launch {
        try {
            block()
        } finally {
            // Always call finish(), even if the coroutineScope was cancelled
            result.finish()
        }
    }
}