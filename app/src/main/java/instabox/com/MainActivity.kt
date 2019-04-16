package instabox.com


import android.Manifest
//import android.app.DownloadManager
import android.content.Intent

import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
//
import android.provider.MediaStore
//
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
//import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
//import kotlinx.android.synthetic.main.activity_main.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

const val NICKNAME = "Almaz"

class MainActivity : AppCompatActivity() {

    lateinit var photosView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        photosView = findViewById(R.id.photosView)
        photosView.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )
        loadPhotos()
        findViewById<Button>(R.id.addPhoto).setOnClickListener {
            if (ContextCompat
                    .checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
            ) {
                addPhoto()

            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA), 0
                )
            }
        }
    }

    fun loadPhotos() {
        api.getPhotos().enqueue(object : Callback<List<Photo>> {
            override fun onResponse(
                call: Call<List<Photo>>,
                response: Response<List<Photo>>
            ) {
                val photos = response.body() ?: listOf()
                photosView.adapter = PhotosAdapter(photos)
            }

            override fun onFailure(
                call: Call<List<Photo>>,
                t: Throwable
            ) {
            }
        })
    }

    fun addPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            val bitmap = data.extras.get("data") as Bitmap
            uploadPhoto(bitmap)
        }
    }

    fun uploadPhoto(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val photoReq = RequestBody.create(
        MediaType.parse("image/jpeg"),
        stream.toByteArray()
        )
        val body = MultipartBody.Part.createFormData(
            "image", "asd.jpg", photoReq
        ).body()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.png", body)
            .addFormDataPart("author", NICKNAME)
            .build()
        api.addPhoto(requestBody).enqueue(object:Callback<Photo>{
            override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                loadPhotos()
            }

            override fun onFailure(call: Call<Photo>, t: Throwable) {
            }

        })
    }
}