package com.example.sertifikasijmp.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.sertifikasijmp.databinding.DialogPickPhotoBinding

class PickPhotoDialog : DialogFragment() {

    private var _binding: DialogPickPhotoBinding? = null
    private val binding get() = _binding!!

    interface PhotoSelectionListener {
        fun onCameraSelected()
        fun onGallerySelected()
    }

    private var listener: PhotoSelectionListener? = null

    fun setPhotoSelectionListener(listener: PhotoSelectionListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPickPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle camera button click with permission check
        binding.btnCamera.setOnClickListener {
            if (isCameraPermissionGranted()) {
                listener?.onCameraSelected()
                dismiss()
            } else {
                requestCameraPermission()
            }
        }

        // Handle gallery button click with permission check
        binding.btnGallery.setOnClickListener {
            if (isGalleryPermissionGranted()) {
                listener?.onGallerySelected()
                dismiss()
            } else {
                requestGalleryPermission()
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isGalleryPermissionGranted(): Boolean {
        // Different permission for Android 13+
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    private fun requestGalleryPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestPermissions(permissions, REQUEST_GALLERY_PERMISSION)
    }

    override fun onStart() {
        super.onStart()
        // Set the dialog to match parent width
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 100
        const val REQUEST_GALLERY_PERMISSION = 101
    }
}
