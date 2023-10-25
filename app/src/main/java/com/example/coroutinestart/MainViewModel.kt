package com.example.coroutinestart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/** Structured concurrency
1. Любая корутина запускается внутри scope с определенным жизненным циклом
2. Все корутины запускаются в виде иерархии (иерархия наследования Job внутри coroutineScope)
3. Пока дочерние Job-ы не будут выполнены, родительская будет активна
4. Если отменяется родительская Job, то дочерние также отменяются. Но не наоборот
5. Ошибка, полученная в одной из Job, передается вверх по иерархии. Будут отменены все Job данного scope, если только исключение не было получено в SupervisorJob

 */

class MainViewModel : ViewModel() {

    fun method() {
        val job = viewModelScope.launch(Dispatchers.Default) {
            Log.d(TAG, "Started")
            val before = System.currentTimeMillis()
            var count = 0
            for (i in 0 until 100_000_000) {
                for (j in 0 until 100) {
                    ensureActive()
                    count++

//                    or
//
//                    if (isActive) {
//                        count++
//                    } else {
//                        throw CancellationException()
//                    }
                }
            }
            Log.d(TAG, "Finished: ${System.currentTimeMillis() - before}")
        }

        job.invokeOnCompletion {
            Log.d(TAG, "Coroutine was finished: $it")
        }

        viewModelScope.launch {
            delay(3000)
            job.cancel()
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}