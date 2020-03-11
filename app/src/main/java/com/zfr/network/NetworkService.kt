package com.zfr.network

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import com.zfr.network.network.ControllerApi
import com.zfr.network.view.MainActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class NetworkService : IntentService("NetworkService") {

    companion object {
        // TODO: Rename actions, choose action names that describe tasks that this
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        const val ACTION_HTTP_REQUEST = "com.zfr.network.action.HTTP_REQUEST"
        const val ACTION_WS_REQUEST = "com.zfr.network.action.WS_REQUEST"

        // TODO: Rename parameters
        const val EXTRA_IP_ADDR = "com.zfr.network.extra.IP_ADDR"
        const val EXTRA_PORT = "com.zfr.network.extra.PORT"
        const val EXTRA_SSLPINNING = "com.zfr.network.extra.SSLPINNING_ENABLED"
        const val EXTRA_HTTP2 = "com.zfr.network.extra.HTTP2_ENABLED"
        const val EXTRA_PENDING_INTENT = "com.zfr.network.extra.PENDING_INTENT"

    }

    override fun onHandleIntent(intent: Intent?) {
        val ip_addr = intent?.getStringExtra(EXTRA_IP_ADDR)
        val port = intent?.getIntExtra(EXTRA_PORT, 8080)
        val sslpinning_enabled = intent?.getBooleanExtra(EXTRA_SSLPINNING, false)
        val http2_enabled = intent?.getBooleanExtra(EXTRA_HTTP2, false)
        val pending_intent = intent?.getParcelableExtra<PendingIntent>(EXTRA_PENDING_INTENT)

        when (intent?.action) {
            ACTION_HTTP_REQUEST -> {
                handleActionHttpRequest(ip_addr!!, port!!, sslpinning_enabled!!, http2_enabled!!, pending_intent!!)
            }
            ACTION_WS_REQUEST -> {
                handleActionWebSocketsRequest(ip_addr!!, port!!, sslpinning_enabled!!, http2_enabled!!, pending_intent!!)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionHttpRequest(ip_addr: String, port: Int, sslpinning_enabled: Boolean, http2_enabled: Boolean, pendingIntent: PendingIntent) {
        val api = ControllerApi().getHttpApi(ip_addr, port, sslpinning_enabled, http2_enabled)
        var output: String

        api.request().enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    output = response.body()!!.string()
                }
                else {
                    output = "Something wrong.. response code - ${response.code()}"
                }
                
                pendingIntent.send(applicationContext, 0, Intent().putExtra(MainActivity.PARAM_RESULT, output))
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                output = "Something wrong.. Message: ${t.message}"
                pendingIntent.send(applicationContext, 0, Intent().putExtra(MainActivity.PARAM_RESULT, output))
            }

        })

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionWebSocketsRequest(ip_addr: String, port: Int, sslpinning_enabled: Boolean, http2_enabled: Boolean, pendingIntent: PendingIntent) {
        TODO("Handle action Baz")
    }

}
