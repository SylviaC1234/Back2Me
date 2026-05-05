package com.sylvia.back2me.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.sylvia.back2me.models.LostItem
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.Any

class LostItemViewModel : ViewModel() {

    private val _items = mutableStateListOf<LostItem>()
    val items: List<LostItem> = _items

    private val cloudinaryUrl =
        "https://api.cloudinary.com/v1_1/dpvid1u8d/image/upload"

    private val uploadPreset = "back2me"

    // ✅ REALTIME FIREBASE FIX
    fun fetchItems(context: Context) {
        val ref = FirebaseDatabase.getInstance().getReference("Items")

        ref.addValueEventListener(object :
            com.google.firebase.database.ValueEventListener {

            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {

                _items.clear()

                for (child in snapshot.children) {
                    val item = child.getValue(LostItem::class.java)
                    item?.let {
                        it.id = child.key.toString()
                        _items.add(it)
                    }
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(context, "Failed to load items", Toast.LENGTH_LONG).show()
            }
        })
    }

    // ✅ UPLOAD ITEM
    fun uploadItem(
        imageUri: Uri?,
        title: String,
        type: String,
        location: String,
        description: String,
        contact: String,
        userId: String, // <--- Pass the current user's ID here
        context: Context,
        onSuccess: () -> Unit
    ) {
        Thread {
            try {
                val imageUrl = imageUri?.let { uploadToCloudinary(context, it) }

                val ref = FirebaseDatabase.getInstance().getReference("Items").push()

                val item = LostItem(
                    id = ref.key,
                    title = title,
                    type = type,
                    location = location,
                    description = description,
                    imageUrl = imageUrl,
                    contact = contact,
                    userId = userId // <--- Save it to the database
                )

                ref.setValue(item).addOnSuccessListener {
                    (context as android.app.Activity).runOnUiThread {
                        Toast.makeText(context, "Item posted successfully", Toast.LENGTH_LONG).show()
                        onSuccess()
                    }
                }

            } catch (e: Exception) {
                (context as android.app.Activity).runOnUiThread {
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }


    // ✅ SAFE CLOUDINARY UPLOAD
    private fun uploadToCloudinary(context: Context, uri: Uri): String {

        val bytes = context.contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        } ?: throw Exception("Image read failed")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "image.jpg",
                bytes.toRequestBody("image/*".toMediaTypeOrNull())
            )
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url(cloudinaryUrl)
            .post(requestBody)
            .build()

        val response = OkHttpClient().newCall(request).execute()
        val body = response.body?.string()

        if (!response.isSuccessful || body.isNullOrEmpty()) {
            throw Exception("Cloudinary failed: $body")
        }

        return JSONObject(body).getString("secure_url")
    }

    fun addItem(newItem: LostItem) {
        _items.add(0, newItem)


    }

    fun deleteItem(context: android.content.Context, id: String) {
        val db = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Items/$id")
        db.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                // Optionally re-fetch items or the list will update via the listener automatically
            } else {
                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        }
    }

}