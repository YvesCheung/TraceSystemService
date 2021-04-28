package com.huya.mobile.service.trace.demo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager

/**
 * @author YvesCheung
 * 4/28/21
 */
class MainService : Service() {

    override fun onCreate() {
        super.onCreate()

        (getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)
            ?.listen(object : PhoneStateListener() {

                override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                    super.onSignalStrengthsChanged(signalStrength)
                }

            }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}