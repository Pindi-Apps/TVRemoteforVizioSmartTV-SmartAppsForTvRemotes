package com.tv.remote.smarttv.control.smartcast.vizio.activities.background

import android.util.Log
import com.tv.remote.smarttv.control.smartcast.vizio.activities.model.GalleryImagesModel
import kotlinx.coroutines.*
import java.util.concurrent.Executors

abstract class AsyncTaskForFiles<Params, Progress, Result>(val taskName: String) {

    val TAG by lazy {
        AsyncTaskForFiles::class.java.simpleName
    }

    companion object {
        private var threadPoolExecutor: CoroutineDispatcher? = null
    }

    var status: StatusFiles = StatusFiles.PENDING
    var preJob: Job? = null
    var bgJob: Deferred<Result>? = null
    abstract fun doInBackground(vararg params: Params?): Result
    open fun onProgressUpdate(values: GalleryImagesModel?) {}
    open fun onPostExecute(result: Result?) {}
    open fun onPreExecute() {}
    open fun onCancelled(result: Result?) {}
    protected var isCancelled = false

    /**
     * Executes background task parallel with other background tasks in the queue using
     * default thread pool
     */
    @DelicateCoroutinesApi
    fun execute(vararg params: Params?) {
        execute(Dispatchers.Default, *params)
    }

    /**
     * Executes background tasks sequentially with other background tasks in the queue using
     * single thread executor @Executors.newSingleThreadExecutor().
     */
    @DelicateCoroutinesApi
    fun executeOnExecutor(vararg params: Params?) {
        if (threadPoolExecutor == null) {
            threadPoolExecutor = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        }
        execute(threadPoolExecutor!!, *params)
    }

    @DelicateCoroutinesApi
    private fun execute(dispatcher: CoroutineDispatcher, vararg params: Params?) {

        if (status != StatusFiles.PENDING) {
            when (status) {
                StatusFiles.RUNNING -> throw IllegalStateException("Cannot execute task:" + " the task is already running.")
                StatusFiles.FINISHED -> throw IllegalStateException("Cannot execute task:"
                        + " the task has already been executed "
                        + "(a task can be executed only once)")
                else -> {
                }
            }
        }

        status = StatusFiles.RUNNING
        GlobalScope.launch(Dispatchers.Main) {
            preJob = launch(Dispatchers.Main) {
                printLog("$taskName onPreExecute started")
                onPreExecute()
                printLog("$taskName onPreExecute finished")
                bgJob = async(dispatcher) {
                    printLog("$taskName doInBackground started")
                    doInBackground(*params)
                }

            }
            preJob?.join()
            if (!isCancelled) {
                withContext(Dispatchers.Main) {
                    onPostExecute(bgJob?.await())
                    printLog("$taskName doInBackground finished")
                    status = StatusFiles.FINISHED
                }
            }
        }
    }

    @DelicateCoroutinesApi
    fun cancel(mayInterruptIfRunning: Boolean) {
        if (preJob == null || bgJob == null) {
            printLog("$taskName has already been cancelled/finished/not yet started.")
            return
        }
        if (mayInterruptIfRunning || (!preJob!!.isActive && !bgJob!!.isActive)) {
            isCancelled = true
            status = StatusFiles.FINISHED
            if (bgJob!!.isCompleted) {
                GlobalScope.launch(Dispatchers.Main) {
                    onCancelled(bgJob!!.await())
                }
            }
            preJob?.cancel(CancellationException("PreExecute: Coroutine Task cancelled"))
            bgJob?.cancel(CancellationException("doInBackground: Coroutine Task cancelled"))
            printLog("$taskName has been cancelled.")
        }
    }

    @DelicateCoroutinesApi
    fun publishProgress(progress: GalleryImagesModel) {
        //need to update main thread
        GlobalScope.launch(Dispatchers.Main) {
            if (!isCancelled) {
                onProgressUpdate(progress)
            }
        }
    }

    private fun printLog(message: String) {
        Log.d(TAG, message)
    }

    enum class StatusFiles {
        PENDING,
        RUNNING,
        FINISHED
    }
}