package com.example.beta.retrofit

import com.example.beta.response.DeleteRefugeeResponse
import com.example.beta.response.DeleteUserResponse
import com.example.beta.response.LoginRequest
import com.example.beta.response.LoginResponse
import com.example.beta.response.LogoutResponse
import com.example.beta.response.RefugeeDetailResponse
import com.example.beta.response.RefugeeRequest
import com.example.beta.response.RefugeeResponse
import com.example.beta.response.UserInfoResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.example.beta.response.*

const val BASE_URL = "https://c241pr574backend-xd67kjleiq-et.a.run.app"

interface ApiService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("logout")
    suspend fun logout(): LogoutResponse

    @GET("user/info")
    suspend fun getUserInfo(): UserInfoResponse

    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: String): DeleteUserResponse

    @POST("refugee")
    suspend fun inputRefugee(@Body refugeeRequest: RefugeeRequest): RefugeeResponse

    @GET("refugee/search")
    suspend fun searchRefugee(@Query("name") name: String, @Query("posko") posko: String): List<RefugeeResponse>

    @GET("refugee/{id}")
    suspend fun getRefugeeById(@Path("id") id: String): RefugeeDetailResponse

    @PUT("refugee/{id}")
    suspend fun updateRefugee(@Path("id") id: String, @Body refugeeRequest: RefugeeRequest): RefugeeResponse

    @DELETE("refugee/{id}")
    suspend fun deleteRefugee(@Path("id") id: String): DeleteRefugeeResponse
}

object RetrofitInstance {
    private val client = OkHttpClient.Builder().build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}