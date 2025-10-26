package com.android.camera2

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun createFile(context: Context):File{
        val videosDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val videoFile = File(videosDir, "VID_$timestamp.mp4")
        return videoFile
    }

}

