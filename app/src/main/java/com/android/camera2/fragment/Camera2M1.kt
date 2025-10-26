package com.android.camera2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.camera2.Utils
import com.android.camera2.camera2Module1.CameraManager
import com.android.camera2.camera2Module1.PermissionManager
import com.android.camera2.camera2Module1.VideoRecorder
import com.android.camera2.databinding.FragmentCamera2M1Binding


class Camera2M1 : Fragment(){

    val permissionManager by lazy {
        PermissionManager(requireContext())
    }

    lateinit var binding: FragmentCamera2M1Binding

    private lateinit var cameraManager: CameraManager
    private lateinit var videoRecorder: VideoRecorder
    private lateinit var textureView: TextureView
    private var isRecording = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCamera2M1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraManager = CameraManager(requireContext())
        videoRecorder = VideoRecorder(requireContext())

        if (permissionManager.hasCameraPermission()) {
            startCamera()
        } else {
            permissionManager.requestPermissions(requireActivity())
        }

        binding.btnRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    private fun startCamera() {
        cameraManager.openCamera("0")
        cameraManager.startPreview(textureView)
    }

    private fun startRecording() {

        val videoFile = Utils.createFile(requireContext())

        videoRecorder.startRecording(
            cameraManager.cameraDevice!!,
            cameraManager.captureSession!!,
            videoFile,
            cameraManager.cameraHandler
        )
        isRecording = true
    }


    private fun stopRecording() {
        videoRecorder.stopRecording()
        isRecording = false
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.closeCamera()
        videoRecorder.stopRecording()
    }

}