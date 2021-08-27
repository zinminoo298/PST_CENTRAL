package com.example.api_test

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface FileDownloadClient{

    @GET("/api/files/download_db")
    @Streaming
    fun downloadfile(): Call<ResponseBody>

    @Multipart
    @POST("/api/files/upload_csv")
    fun upload(
        @Part  file: MultipartBody.Part?,
        @Part("type") type: RequestBody?
    ): Call<ResponseBody?>?

    @GET("/api/files/verify_upload")
    fun checkID(
        @Query ("countName") id:String
    ):Call<ResponseBody>
}
