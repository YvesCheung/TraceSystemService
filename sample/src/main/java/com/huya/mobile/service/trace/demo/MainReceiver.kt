package com.huya.mobile.service.trace.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager

/**
 * @author YvesCheung
 * 4/28/21
 */
class MainReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        (context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)
            ?.listen(object : PhoneStateListener() {

                override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                    super.onSignalStrengthsChanged(signalStrength)
                }

            }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
    }
}