package com.example.coroutinestart

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Structured concurrency
1. Любая корутина запускается внутри scope с определенным жизненным циклом
2. Все корутины запускаются в виде иерархии (иерархия наследования Job внутри coroutineScope)
3. Пока дочерние Job-ы не будут выполнены, родительская будет активна
4. Если отменяется родительская Job, то дочерние также отменяются. Но не наоборот
5. Ошибка, полученная в одной из Job, передается вверх по иерархии. Будут отменены все Job данного scope, если только исключение не было получено в SupervisorJob

 */

class MainViewModel : ViewModel() {

    private val parentJob = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, "exception caught: $throwable")
    }
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob + exceptionHandler)

    fun method() {
        val childJob1 = coroutineScope.launch {
            delay(3000)
            Log.d(TAG, "first coroutine finished")
        }
        val childJob2 = coroutineScope.launch {
            delay(2000)
            Log.d(TAG, "second coroutine finished")
        }
        val childJob3 = coroutineScope.launch {
            delay(1000)
            error()
            Log.d(TAG, "third coroutine finished")
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    private fun error() {
        throw RuntimeException()
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}