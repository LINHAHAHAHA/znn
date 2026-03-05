package com.police.dispatch.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * DeepSeek API 接口
 */
interface DeepSeekApiService {
    
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse
    
    companion object {
        private const val BASE_URL = "https://api.deepseek.com/"
        private const val API_KEY = "sk-e4962d2bd777485fa667fb5479ae3a39"
        
        fun create(): DeepSeekApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $API_KEY")
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DeepSeekApiService::class.java)
        }
    }
}

/**
 * 聊天请求
 */
data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>,
    val max_tokens: Int = 2000,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

/**
 * 聊天响应
 */
data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

