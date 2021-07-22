package com.example.twitchtopgames.api.games

import android.os.HandlerThread
import android.util.Log

private const val TAG = "Downloader"

class ThumbnailDownloader<in T> : HandlerThread (TAG) {
    private var hasQuit = false

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThubnail(target: T, url: String){
        Log.i(TAG, "Got a URL: $url")
    }
}