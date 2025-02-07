package com.example.road_app_dip

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class UploadFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var captionEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        imageView = view.findViewById(R.id.imageView)
        captionEditText = view.findViewById(R.id.captionEditText)
        uploadButton = view.findViewById(R.id.uploadButton)
        progressBar = view.findViewById(R.id.progressBar)

        imageView.setOnClickListener {
            pickImageFromGallery()
        }

        uploadButton.setOnClickListener {
            uploadImage()
        }

        return view
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imageView.setImageURI(selectedImageUri)
        }
    }


    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = cursor.getString(index)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "unknown_file"
    }



    private fun uploadImage() {
        val uri = selectedImageUri
        if (uri == null) {
            Toast.makeText(requireContext(), "Select an image first", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
                val byteArray = inputStream?.use { it.readBytes() }
                inputStream?.close()


                if (byteArray == null) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error reading file", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val fileName = getFileName(uri)
                val requestBody = RequestBody.Companion.create("image/*".toMediaTypeOrNull(), byteArray)
                val imagePart = MultipartBody.Part.createFormData("file", fileName, requestBody)
                val captionPart = RequestBody.Companion.create("text/plain".toMediaTypeOrNull(), captionEditText.text.toString())

                val requestBodyMulti = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, requestBody)
                    .addFormDataPart("caption", captionEditText.text.toString())
                    .build()

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/api/uploads")
                    .post(requestBodyMulti)
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3OTY3OTIzZGZkYjgzNWMzNmI3Zjk4OSIsImlhdCI6MTczODg3NzIyMSwiZXhwIjoxNzM4ODgwODIxfQ.lYUY8buxx9LKB18PbRbE1J5zScwK3zTTYfczNpGZBTk")
                    .build()

                val response = client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Upload failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error uploading image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
