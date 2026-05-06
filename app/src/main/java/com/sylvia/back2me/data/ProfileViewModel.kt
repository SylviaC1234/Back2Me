package com.sylvia.back2me.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object CloudinaryManager {

    private const val cloudName = "dpvid1u8d"
    private const val uploadPreset = "back2me"
    private const val url =
        "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

    fun init(context: Context) {
        // Not needed anymore (kept for compatibility)
    }

    fun uploadImage(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            try {

                val bytes = context.contentResolver.openInputStream(imageUri)?.use {
                    it.readBytes()
                } ?: throw Exception("Image read failed")

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "profile.jpg",
                        bytes.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", uploadPreset)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = OkHttpClient().newCall(request).execute()
                val body = response.body?.string()

                if (!response.isSuccessful || body.isNullOrEmpty()) {
                    throw Exception("Cloudinary error: $body")
                }

                val imageUrl = JSONObject(body).getString("secure_url")

                (context as android.app.Activity).runOnUiThread {
                    onSuccess(imageUrl)
                }

            } catch (e: Exception) {
                (context as android.app.Activity).runOnUiThread {
                    onError(e.message ?: "Upload failed")
                }
            }
        }.start()
    }
}