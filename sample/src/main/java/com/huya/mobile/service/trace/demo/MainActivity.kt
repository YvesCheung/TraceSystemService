package com.huya.mobile.service.trace.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import com.huya.mobile.service.trace.PidExceedNumberPhoneStateListeners
import com.huya.mobile.service.trace.R
import com.huya.mobile.service.trace.TraceSystemService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TraceSystemService.trace(PidExceedNumberPhoneStateListeners())

        startService(Intent(this, MainService::class.java))
        sendBroadcast(Intent(this, MainReceiver::class.java))

        crash_btn.setOnClickListener {
            var n = 1000
            while (n-- > 0) {
                (getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)
                    ?.listen(object : PhoneStateListener() {

                        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                            super.onCallStateChanged(state, phoneNumber)
                        }

                    }, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }
}