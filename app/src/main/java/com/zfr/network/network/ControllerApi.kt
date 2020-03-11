package com.zfr.network.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.zfr.network.network.interfaces.HttpApi
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

class ControllerApi {
    val BASE_URL: String = "https://192.168.1.65:8080/"

    private val loggingInterceptor =  HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun config_http_client(http2_support: Boolean = false): OkHttpClient.Builder {

        val client = OkHttpClient().newBuilder()

        client.readTimeout(30, TimeUnit.SECONDS)
        client.writeTimeout(30, TimeUnit.SECONDS)
        client.connectTimeout(30, TimeUnit.SECONDS)
        client.addInterceptor(loggingInterceptor)

        /*
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, null)
        val sslSocketFactory = sslContext.socketFactory
        client.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        client.hostnameVerifier(HostnameVerifier {_, _ -> true})
        // client.connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
        */
        if (http2_support) {
            // client.protocols(Arrays.asList(Protocol.HTTP_2, Protocol.H2_PRIOR_KNOWLEDGE))
            client.protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
        }
        return client
    }

    fun getHttpSSLPinningApi(host: String, port: Int, sslPinningEnabled: Boolean, http2_support: Boolean): HttpApi {
        lateinit var retrofit: Retrofit

        val httpClient = config_http_client(http2_support)

        if (sslPinningEnabled) {
            httpClient.certificatePinner(
                CertificatePinner.Builder()
                    .add("publicobject.com", "sha256//r8udi/Mxd6pLO7y7hZyUMWq8YnFnIWXCqeHsTDRqy8=")
                    .build()
            )
        }
        retrofit = Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl("https://${host}/")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return retrofit.create(HttpApi::class.java)
    }

    fun getHttpApi(host: String, port: Int, sslPinningEnabled: Boolean, http2_support: Boolean): HttpApi {
        lateinit var retrofit: Retrofit

        val httpClient = config_http_client(http2_support)

        if (sslPinningEnabled) {
            httpClient.certificatePinner(
                CertificatePinner.Builder()
                    .add("*", "sha256/3MnSPmEDhbuEDGiiUeg0oMw0m92SKIVvp8Aruhx4N0M=")
                    .build()
            )
        }
        retrofit = Retrofit.Builder()
            .client(httpClient.build())
            .baseUrl("https://${host}:${port}/")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return retrofit.create(HttpApi::class.java)
    }

}