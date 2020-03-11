package com.zfr.network.view

import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.zfr.network.NetworkService
import com.zfr.network.R

// import com.zfr.network.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        public final val PARAM_RESULT: String = "com.zfr.network.extra.RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val http2_enabled: Switch = findViewById(R.id.http2_enabled)
        val sslpinning_enabled: Switch = findViewById(R.id.sslpinning_enabled)

        val ip_textinput: TextInputEditText = findViewById(R.id.input_ip)
        val port_textinput: TextInputEditText = findViewById(R.id.input_port)

        val output: TextView = findViewById(R.id.textView_output)

        val ws_request_button: Button = findViewById(R.id.button_websocket)
        val http_request_button: Button = findViewById(R.id.button_httprequest)
        val http_robots_button: Button = findViewById(R.id.button_httprobots)


        http_request_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val intent_service = Intent(this, NetworkService::class.java).apply {
                action = NetworkService.ACTION_HTTP_REQUEST
                putExtra(NetworkService.EXTRA_IP_ADDR, ip_textinput.text.toString())
                putExtra(NetworkService.EXTRA_PORT, port_textinput.text.toString())
                putExtra(NetworkService.EXTRA_SSLPINNING, sslpinning_enabled.isChecked)
                putExtra(NetworkService.EXTRA_HTTP2, http2_enabled.isChecked)
                putExtra(NetworkService.EXTRA_PENDING_INTENT, pendingIntent)
            }

            startService(intent_service)
        }

        http_robots_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val intent_service = Intent(this, NetworkService::class.java).apply {
                action = NetworkService.ACTION_HTTP_ROBOTS
                putExtra(NetworkService.EXTRA_IP_ADDR, ip_textinput.text.toString())
                putExtra(NetworkService.EXTRA_PORT, port_textinput.text.toString())
                putExtra(NetworkService.EXTRA_SSLPINNING, sslpinning_enabled.isChecked)
                putExtra(NetworkService.EXTRA_HTTP2, http2_enabled.isChecked)
                putExtra(NetworkService.EXTRA_PENDING_INTENT, pendingIntent)
            }

            startService(intent_service)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val output: TextView = findViewById(R.id.textView_output)
        output.text = intent!!.getStringExtra(PARAM_RESULT)
    }

}
