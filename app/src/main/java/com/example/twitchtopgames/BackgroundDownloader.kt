package com.example.twitchtopgames

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.twitchtopgames.api.TwitchServices
import com.example.twitchtopgames.api.games.model.Streams
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

private const val TAG = "Background downloader"
private const val MESSAGE_DOWNLOAD = 0

class BackgroundDownloader <in T> (
    private val clientId: String,
    private val responseHandler: Handler,
    private val onBackgroundDownloader: (T, Streams) -> Unit) : HandlerThread(TAG) {

    lateinit var token: String

    val fragmentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup(){
            Log.i(TAG, "Starting background thread +${Random.nextBoolean()}")
            start()
            looper
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun end(){
            Log.i(TAG, "Destroy thread")
            quit()
        }
    }

    val viewLifecycleObserver : LifecycleObserver =
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun clearQueue(){
                Log.i(TAG, "Clearing all requests from queue")
                requestHandler.removeMessages(MESSAGE_DOWNLOAD)
                requestMap.clear()
            }
        }

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, Int>()
    private val service = TwitchServices.gamesService

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                if(msg.what == MESSAGE_DOWNLOAD){
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for ID: ${requestMap[target]}")
                    Thread(Runnable{
                        handleRequest(target)
                    }).start()
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }



    fun queueDownload(target: T, id: Int, cursor: String = String()){
        Log.i(TAG, "Got you!!")
        requestMap[target] = id
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    fun clearQueue(){
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestMap.clear()
    }



    private fun handleRequest(target: T) {
        val id = requestMap[target] ?: return
        val streams = service.loadStreams(clientId, "Bearer $token", id) ?: return
        responseHandler.post(Runnable {
            if (requestMap[target] != id || hasQuit) {
                return@Runnable
            }

            requestMap.remove(target)
            onBackgroundDownloader(target, streams)
        })
    }
}