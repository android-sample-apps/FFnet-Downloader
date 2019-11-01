package fr.ffnet.downloader.repository

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CrawlService {

    @Headers("Cookie: popin_newsletter=1; accepts_cookies=1; PHPSESSID=a7eu3bjqanilh2fc8s8vvri2r1; REMEMBERME=VWRnXENvcmVCdW5kbGVcRW50aXR5XFVzZXI6WW5KaGN5NXFaWEpsYlhsQVoyMWhhV3d1WTI5dDoxNTgwMzEwNzUyOmVmMTc4ZjlkYjIwYWRkMjA5YmI4M2FlYTA1NjU5ZDgzZWVhYmUyMjhkMjExMmVlZGY5OTM5YWJhNGYxOGE4YWY%3D; authenticated=1")
    @GET("{recipe}")
    fun getRecipe(@Path("recipe") recipe: String): Call<ResponseBody>

    @GET("recettes")
    fun getCategories(): Call<ResponseBody>

    @GET("recettes/{category}")
    fun getRecipes(
        @Path(value = "category", encoded = true) categoryUrl: String,
        @Query("page") loadPage: Int
    ): Call<ResponseBody>
}
