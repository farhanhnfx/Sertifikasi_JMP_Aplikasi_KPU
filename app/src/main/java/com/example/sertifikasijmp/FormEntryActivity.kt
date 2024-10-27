package com.example.sertifikasijmp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.media.MediaScannerConnection
import com.example.sertifikasijmp.dialog.PickPhotoDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.sertifikasijmp.databinding.ActivityFormEntryBinding
import com.example.sertifikasijmp.dialog.CekLokasiDialogFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormEntryActivity : AppCompatActivity(), CekLokasiDialogFragment.LocationListener, PickPhotoDialog.PhotoSelectionListener {
    private val binding by lazy {
        ActivityFormEntryBinding.inflate(layoutInflater)
    }
    private var photoUri: Uri? = null
    private var currentLatLng: LatLng? = null
    private val dbHelper by lazy {
        DBHelper(this)
    }

    companion object {
        var MODE = 0
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        photoUri = it

        try {
            if (photoUri != null) {
                binding.imgFoto.visibility = View.VISIBLE
                Glide.with(this)
                    .load(photoUri)
                    .into(binding.imgFoto)
                contentResolver.takePersistableUriPermission(photoUri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            else {
                binding.imgFoto.visibility = View.GONE
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            success ->
        if (success) {
            binding.imgFoto.visibility = View.VISIBLE
//            refreshGallery()
            if (photoUri != null) {
                photoUri?.let {
                    binding.imgFoto.setImageURI(photoUri)
                }
                saveImageToGallery(photoUri)
            }
        }
        else {
            binding.imgFoto.visibility = View.GONE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        MODE = 0

        val pemilihId = intent.getIntExtra(ListPemilihActivity.EXTRA_PEMILIH_ID, -1)

        if (pemilihId != -1) {
            ChangeToDetailMode(pemilihId)
            return
        }

        with(binding) {
            toolbar.txtTitle.text = "Form Entri"
            toolbar.icBack.setOnClickListener { finish() }

            imgFoto.visibility = View.GONE

            inpTanggal.editText?.apply {
                isClickable = true
                isFocusable = false
            }

            inpTanggal.editText?.setOnClickListener {
                if (MODE != 1) {
                    val datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Pilih Tanggal Pendataan")
                        .build()
                    datePicker.show(supportFragmentManager, "Date Picker")
                    datePicker.addOnPositiveButtonClickListener {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val date = sdf.format(it)
                        inpTanggal.editText?.setText(date)
                    }
                }
            }
            inpLokasi.setOnClickListener {
                val dialog = CekLokasiDialogFragment()
                dialog.show(supportFragmentManager, "Location Dialog")
            }
            btnAmbilGambar.setOnClickListener {
                val dialog = PickPhotoDialog()
                dialog.setPhotoSelectionListener(this@FormEntryActivity)
                dialog.show(supportFragmentManager, "PickPhotoDialog")
            }

            btnSubmit.setOnClickListener {
                checkAndSubmitData()
            }

        }
    }

    override fun onLocationSelected(latLng: LatLng) {
        currentLatLng = latLng
        getAddress(latLng)
    }

    fun getAddress(latLng: LatLng){
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var fulladdress = ""
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses!!.isNotEmpty()) {
            address = addresses[0]
            fulladdress = address.getAddressLine(0)
        } else{
            fulladdress = "Alamat tidak ditemukan"
        }
        binding.inpAlamat.editText?.setText(fulladdress)
    }

    override fun onCameraSelected() {
        // Take a picture from the camera
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
        if (photoUri != null) {
            cameraLauncher.launch(photoUri!!)
        }
    }

    override fun onGallerySelected() {
        // Pick an image from the gallery
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, launch gallery
            galleryLauncher.launch(arrayOf("image/*"))
        } else {
            // Request storage permission
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PickPhotoDialog.REQUEST_GALLERY_PERMISSION)
        }
    }

    // Helper function to create a file for the camera image
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun refreshGallery() {
        MediaScannerConnection.scanFile(this, arrayOf(photoUri?.path), null, null)
    }

    private fun saveImageToGallery(uri: Uri?) {
        if (uri == null) return

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Captured Image")
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // For Android 10 and above
        }

        val newUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        newUri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.copyTo(outputStream!!) // Copy the image to the new URI
                }
            }
        }
    }

    private fun checkAndSubmitData() {
        with(binding) {
            // Collect data from input fields
            val nik = inpNik.editText?.text.toString()
            val nama = inpNama.editText?.text.toString()
            val phone = inpNomor.editText?.text.toString()
            val tanggal = inpTanggal.editText?.text.toString()
            val alamat = inpAlamat.editText?.text.toString()
            val latitude = currentLatLng?.latitude
            val longitude = currentLatLng?.longitude
            val fotoPath = photoUri.toString()

            val selectedJk = inpJenisKelamin.checkedRadioButtonId
            val jk = if (selectedJk != -1) {
                findViewById<RadioButton>(selectedJk).text.toString()
            } else {
                null
            }

            if (!isNikValid(nik)) {
                Toast.makeText(this@FormEntryActivity, "NIK tidak valid!", Toast.LENGTH_SHORT).show()
                return
            }

            if (dbHelper.isNikOrIdExists(nik)) {
                Toast.makeText(this@FormEntryActivity, "Sudah terdapat data NIK yang sama!", Toast.LENGTH_SHORT).show()
                val pemilih = dbHelper.getPemilih(nik)
                ChangeToDetailMode(pemilih!!.id)
                return
            }

            if (nik.isEmpty() || nama.isEmpty() || phone.isEmpty() || tanggal.isEmpty() || alamat.isEmpty() || latitude == null || longitude == null || jk.isNullOrEmpty()) {
                Toast.makeText(this@FormEntryActivity, "Mohon isi semua data!", Toast.LENGTH_SHORT).show()
                return
            }

            // Insert data into the database
            val result = dbHelper.insertPemilih(nik, nama, phone, jk, tanggal, alamat, latitude, longitude, fotoPath)

            if (result != -1L) {
                Toast.makeText(this@FormEntryActivity, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                ChangeToDetailMode(result.toInt())
            } else {
                Toast.makeText(this@FormEntryActivity, "Gagal disimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNikValid(nik: String): Boolean {
        Log.d("NIK_LENGTH", nik.length.toString())
        Log.d("NIK_LENGTH_BOOL", (nik.length == 16).toString())
        return nik.length == 16
    }

    private fun ChangeToDetailMode(pemilihId: Int) {
        MODE = 1

        val pemilih = dbHelper.getPemilih(pemilihId)

        with(binding) {
            toolbar.apply {
                txtTitle.text = "Data Pemilih"
                icBack.setOnClickListener { finish() }
            }
            inpNik.editText?.apply {
                setText(pemilih?.nik)
                isClickable = false
                isFocusable = false
            }
            inpNama.editText?.apply {
                setText(pemilih?.nama)
                isClickable = false
                isFocusable = false
            }
            inpNomor.editText?.apply {
                setText(pemilih?.phone)
                isClickable = false
                isFocusable = false
            }
            inpTanggal.editText?.apply {
                setText(pemilih?.tanggal)
                isClickable = false
                isFocusable = false
            }
            inpAlamat.editText?.apply {
                setText(pemilih?.alamat)
                isClickable = false
                isFocusable = false
            }
            inpTanggal.editText?.apply {
                setText(pemilih?.tanggal)
                isClickable = false
                isFocusable = false
            }
            inpJenisKelamin.getChildAt(0).apply {
                isClickable = false
                isFocusable = false
            }
            inpJenisKelamin.getChildAt(1).apply {
                isClickable = false
                isFocusable = false
            }
            when(pemilih?.jenisKelamin) {
                "Laki-Laki" -> (inpJenisKelamin.getChildAt(0) as RadioButton).isChecked = true
                "Perempuan" -> (inpJenisKelamin.getChildAt(1) as RadioButton).isChecked = true
            }
            if (pemilih?.imgPath != null) {
                val uri = Uri.parse(pemilih.imgPath)

                Log.d("IMG_VIEW_URI", uri.toString())
                uri.path?.let { Log.d("IMG_VIEW_PATH", it) }

                imgFoto.visibility = View.VISIBLE
                Glide.with(this@FormEntryActivity)
                    .load(uri)
                    .override(300, 300)
                    .into(imgFoto)
                Log.d("IMG_VIEW_WIDTH", imgFoto.width.toString())
            }
            inpLokasi.visibility = View.GONE
            btnAmbilGambar.visibility = View.GONE
            btnSubmit.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PickPhotoDialog.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted
                onCameraSelected()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == PickPhotoDialog.REQUEST_GALLERY_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Gallery permission granted
                onGallerySelected()
            } else {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
