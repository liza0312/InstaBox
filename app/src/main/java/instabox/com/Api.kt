package instabox.com

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class Photo(
    val id:Long,
    val author:String,
    val image:String,
    val likes:List<String>
)
interface Api{
    @GET ("photos")
    fun getPhotos(): Call<List<Photo>>


@POST("photos")
fun addPhoto(@Body body: RequestBody):Call<Photo>
}
var api = Retrofit.Builder()
    .baseUrl("https://skillbox.trinitydigital.ru/api/photo")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(Api::class.java)

