package org.bombayneurosciences.bna_2023.Data

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.GsonBuilder
import org.bombayneurosciences.bna_2023.utils.ConstanstsApp




class BNA_RetrofitInstance {

    companion object {
        private val gson = GsonBuilder()
            .setLenient()
            .create()

        private val logging = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        private fun createRetrofit(baseUrl: String): Retrofit {
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(ConnectionPool(5, 10, TimeUnit.MINUTES))
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        private val productionRetrofit by lazy {
            createRetrofit(ConstanstsApp.BASE_URL)
        }

        val productionApi: BNA_API by lazy {
            productionRetrofit.create(BNA_API::class.java)
        }
    }
}

