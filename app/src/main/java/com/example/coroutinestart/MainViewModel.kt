package com.example.coroutinestart

import android.util.Log
import androidx.lifecycle.ViewModel
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
*/

class MainViewModel: ViewModel() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    fun method() {
        val childJob1 = coroutineScope.launch {
            delay(3000)
            Log.d(TAG, "first coroutine finished")
        }
        val childJob2 = coroutineScope.launch {
            delay(2000)
            childJob1.cancel()
            Log.d(TAG, "second coroutine finished")
            Log.d(TAG, "parent coroutine cancelled: ${parentJob.isCancelled}")
        }
        Log.d(TAG, "childJob1 is child of parentJob: ${parentJob.children.contains(childJob1)}")
        Log.d(TAG, "childJob2 is child of parentJob: ${parentJob.children.contains(childJob2)}")
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }


    companion object {
        const val TAG = "MainViewModel"
    }
}