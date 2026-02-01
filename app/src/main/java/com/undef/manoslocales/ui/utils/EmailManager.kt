package com.undef.manoslocales.ui.utils

import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class EmailRequest(
    val service_id: String,
    val template_id: String,
    val user_id: String,
    val template_params: Map<String, String>,
    val accessToken: String? = null
)

interface EmailService {
    @POST("email/send")
    suspend fun sendEmail(@Body request: EmailRequest): retrofit2.Response<Unit>
}

object EmailManager {
    private const val BASE_URL = "https://api.emailjs.com/api/v1.0/"
    private const val SERVICE_ID = "service_tecno"
    private const val PUBLIC_KEY = "EbkHCWwu2AUfMKG-p"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(EmailService::class.java)

    suspend fun sendVerificationEmail(email: String, code: String): Boolean {
        // Log para verificar el payload antes de enviar
        Log.d("EmailJS_Payload", "Enviando código de verificación: $code a $email")

        val params = mapOf(
            "to_email" to email,
            "verification_code" to code
        )
        val request = EmailRequest(
            service_id = SERVICE_ID,
            template_id = "template_ung9kay",
            user_id = PUBLIC_KEY,
            template_params = params
        )
        
        val jsonBody = Gson().toJson(request)
        Log.d("EmailJS_Debug", "Enviando JSON: $jsonBody")

        return try {
            val response = service.sendEmail(request)
            if (response.isSuccessful) {
                true
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("EmailJS_Debug", "Código de respuesta: ${response.code()} - Mensaje: $errorMsg")
                false
            }
        } catch (e: Exception) {
            Log.e("EmailJS_Debug", "Error al enviar correo: ${e.message}")
            false
        }
    }

    suspend fun sendResetPasswordEmail(email: String, code: String): Boolean {
        // Log para verificar el payload antes de enviar
        Log.d("EmailJS_Payload", "Enviando código de reset: $code a $email")

        val params = mapOf(
            "to_email" to email,
            "verification_code" to code // Corregido: antes era reset_code
        )
        val request = EmailRequest(
            service_id = SERVICE_ID,
            template_id = "template_mz6wv8d",
            user_id = PUBLIC_KEY,
            template_params = params
        )

        val jsonBody = Gson().toJson(request)
        Log.d("EmailJS_Debug", "Enviando JSON: $jsonBody")

        return try {
            val response = service.sendEmail(request)
            if (response.isSuccessful) {
                true
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("EmailJS_Debug", "Código de respuesta: ${response.code()} - Mensaje: $errorMsg")
                false
            }
        } catch (e: Exception) {
            Log.e("EmailJS_Debug", "Error al enviar correo: ${e.message}")
            false
        }
    }

    fun generateCode(): String {
        return (100000..999999).random().toString()
    }
}
