package com.eagskunst.apps.videoworld.app.di.modules

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.telecom.ConnectionService
import com.eagskunst.apps.videoworld.BuildConfig
import com.eagskunst.apps.videoworld.app.VideoWorldApp
import com.eagskunst.apps.videoworld.app.di.qualifiers.TwitchQualifier
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import com.eagskunst.apps.videoworld.utils.ApiKeys
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by eagskunst in 26/4/2020.
 */

@Module
class NetworkModule {

    @Provides
    @AppScope
    fun provideInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @AppScope
    fun provideCacheFile(context: Context): File = File(context.cacheDir, "OkHttp_Cache")

    @Provides
    @AppScope
    fun provideCache(cacheFile: File): Cache = Cache(cacheFile, 10*1000*1000)

    @Provides
    @AppScope
    fun provideOkHttpClientBuilder(loggingInterceptor: HttpLoggingInterceptor,
                                   cache: Cache,
                                   context: Context): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader(
                        "User-Agent",
                        "VideoWorld-ANDROID " + " BUILD VERSION: " + BuildConfig.VERSION_NAME + " SMARTPHONE: " + Build.MODEL + " ANDROID VERSION: " + Build.VERSION.RELEASE
                    )
                    .addHeader("Content-Type", "application/json")
                if(hasNetwork(context)) {
                    /*
                        *  If there is Internet, get the cache that was stored 5 seconds ago.
                        *  If the cache is older than 5 seconds, then discard it,
                        *  and indicate an error in fetching the response.
                        *  The 'max-age' attribute is responsible for this behavior.
                        */
                    requestBuilder.addHeader("Cache-Control", "public, max-age=" + 5)
                }
                else {
                    /*
                        *  If there is no Internet, get the cache that was stored 7 days ago.
                        *  If the cache is older than 7 days, then discard it,
                        *  and indicate an error in fetching the response.
                        *  The 'max-stale' attribute is responsible for this behavior.
                        *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                        */
                    requestBuilder.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .readTimeout(90, TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS)
            .cache(cache)


    fun hasNetwork(context: Context): Boolean {
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


    @Provides
    @AppScope
    @TwitchQualifier
    fun provideOkHttpClientTwitch(builder: OkHttpClient.Builder): OkHttpClient = builder
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Client-ID", ApiKeys.TWITCH_CLIENT_ID)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()


    @Provides
    @AppScope
    fun provideOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient = builder.build()

}