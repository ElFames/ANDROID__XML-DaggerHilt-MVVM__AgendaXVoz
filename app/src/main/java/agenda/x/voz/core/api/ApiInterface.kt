package agenda.x.voz.core.api

import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @GET
    suspend fun getLatestVersion(
        @Url url: String
    ): Response<Int>

    /**
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun getToken(
        @Body userForLoginRequest: UserForLoginRequest
    ): Response<HashMap<String, String>>

    @Headers("Content-Type: application/json")
    @PUT("product/toStore/{codeBar}")
    suspend fun moveToStore(
        @Path("codeBar") codeBar: String,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    @Headers("Content-Type: application/json")
    @DELETE("product/store/delete/{codeBar}")
    suspend fun deleteStoreProduct(
        @Path("codeBar") codeBar: String,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    @Multipart
    @POST("product/uploadImage/{codeBar}")
    suspend fun uploadImage(
        @Path("codeBar") codeBar: String,
        @Header("Authorization") authHeader: String,
        @Part filePart: MultipartBody.Part
    ): Response<Boolean>
    **/
}