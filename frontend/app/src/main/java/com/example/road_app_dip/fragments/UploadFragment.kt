package com.example.road_app_dip

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.road_app_dip.network.ApiInterface
import com.example.road_app_dip.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream

class UploadFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var captionEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var progressBar: ProgressBar

    private val api: ApiInterface = ApiService.create()


    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
        return sharedPreferences.getString("bearer_token", null)
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        imageView = view.findViewById(R.id.imageView)
        captionEditText = view.findViewById(R.id.captionEditText)
        uploadButton = view.findViewById(R.id.uploadButton)
        progressBar = view.findViewById(R.id.progressBar)

        imageView.setOnClickListener { pickImageFromGallery() }
        uploadButton.setOnClickListener { uploadImage() }

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
        val token = getToken()
        if (token == null) {
            Toast.makeText(requireContext(), "Няма активен токен!", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = selectedImageUri
        if (uri == null) {
            Toast.makeText(requireContext(), "Изберете изображение първо!", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), "Грешка при четене на файла", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val fileName = getFileName(uri)
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
                val imagePart = MultipartBody.Part.createFormData("file", fileName, requestBody)
                val captionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), captionEditText.text.toString())

                val response = api.uploadPost("Bearer $token", captionPart, imagePart) // 🛠️ Токенът се подава в Header-а

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Качването е успешно!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Грешка при качване! Код: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Грешка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
