package com.android.camera2.camera2Module1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import java.io.File


class CameraManager(private val context: Context) {
    var cameraDevice: CameraDevice? = null
    var captureSession: CameraCaptureSession? = null
    private var previewRequestBuilder: CaptureRequest.Builder? = null
    private lateinit var cameraThread: HandlerThread
     lateinit var cameraHandler: Handler
    private val cameraManagerCompat = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
        }
    }

    fun openCamera(cameraId: String = "0") {
        startCameraThread()

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraManagerCompat.openCamera(cameraId, cameraStateCallback, cameraHandler)
        }
    }

    fun startPreview(textureView: TextureView) {
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                createPreviewSession(surface)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
        }
    }

    private fun createPreviewSession(surfaceTexture: SurfaceTexture? = null) {
        val camera = cameraDevice ?: return

        try {
            previewRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            val surface = if (surfaceTexture != null) Surface(surfaceTexture) else null

            surface?.let {
                previewRequestBuilder?.addTarget(it)
                camera.createCaptureSession(
                    listOf(it),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            captureSession = session
                            updatePreview()
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {}
                    },
                    cameraHandler
                )
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        previewRequestBuilder?.set(
            CaptureRequest.CONTROL_MODE,
            CameraMetadata.CONTROL_MODE_AUTO
        )
        previewRequestBuilder?.set(
            CaptureRequest.CONTROL_AE_MODE,
            CameraMetadata.CONTROL_AE_MODE_ON
        )

        try {
            captureSession?.setRepeatingRequest(
                previewRequestBuilder?.build()!!,
                null,
                cameraHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun closeCamera() {
        captureSession?.close()
        cameraDevice?.close()
        stopCameraThread()
    }

    private fun startCameraThread() {
        cameraThread = HandlerThread("CameraThread")
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper)
    }

    private fun stopCameraThread() {
        cameraThread.quitSafely()
        try {
            cameraThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}

class VideoRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var recordingFile: File? = null

    fun startRecording(
        cameraDevice: CameraDevice,
        captureSession: android.hardware.camera2.CameraCaptureSession,
        outputFile: File,
        cameraHandler: android.os.Handler
    ) {
        if (!canRecord()) return

        recordingFile = outputFile
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncodingBitRate(10000000)
            setVideoFrameRate(30)
            setVideoSize(1920, 1080)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(outputFile.absolutePath)

            try {
                prepare()
                val recordingSurface = surface
                val recordingRequest = cameraDevice.createCaptureRequest(
                    CameraDevice.TEMPLATE_RECORD
                ).apply {
                    addTarget(recordingSurface)
                }.build()

                captureSession.stopRepeating()
                captureSession.capture(recordingRequest, null, cameraHandler)
                start()
                isRecording = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        if (!isRecording) return

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isRecording(): Boolean = isRecording

    fun getRecordingFile(): File? = recordingFile

    private fun canRecord(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}