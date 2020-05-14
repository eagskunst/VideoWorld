package com.eagskunst.apps.videoworld.app.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.eagskunst.apps.videoworld.BuildConfig
import com.eagskunst.apps.videoworld.utils.ApiKeys
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by eagskunst in 30/4/2020.
 */
var SESSION_TOKEN = ""
val networkModule = module {

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { File(androidContext().cacheDir, "okhttp_cache") }

    single { Cache(get(), 10*1000*1000) }

    single {
        OkHttpClient.Builder()
            .addInterceptor( get<HttpLoggingInterceptor>() )
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader(
                        "User-Agent",
                        "VideoWorld-ANDROID " + " BUILD VERSION: " + BuildConfig.VERSION_NAME + " SMARTPHONE: " + Build.MODEL + " ANDROID VERSION: " + Build.VERSION.RELEASE
                    )
                    .addHeader("Content-Type", "application/json")
                if(hasNetwork(androidContext())) {
                    requestBuilder.addHeader("Cache-Control", "public, max-age=" + 5)
                }
                else {
                    requestBuilder.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .readTimeout(90, TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS)
            .cache(get())
    }

    factory (named(KoinQualifiers.TwitchApi)) {
        get<OkHttpClient.Builder>().addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Client-ID", ApiKeys.TWITCH_CLIENT_ID)
                .addHeader("Authorization", "Bearer $SESSION_TOKEN")
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()
    }

    factory (named(KoinQualifiers.TwitchAuth)) {
        get<OkHttpClient.Builder>().addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Client-ID", ApiKeys.TWITCH_CLIENT_ID)
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()
    }

    single { get<OkHttpClient.Builder>().build() }
}

private fun hasNetwork(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw      = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

