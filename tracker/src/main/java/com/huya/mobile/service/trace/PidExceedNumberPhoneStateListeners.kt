package com.huya.mobile.service.trace

import android.util.Log
import java.lang.reflect.Method

/**
 * ```
 * java.lang.IllegalStateException: Pid 19445 has exceeded the number of permissible registered listeners. Ignoring request to add.
 *  at android.os.Parcel.createExceptionOrNull(Parcel.java:2384)
 *  at android.os.Parcel.createException(Parcel.java:2360)
 *  at android.os.Parcel.readException(Parcel.java:2343)
 *  at android.os.Parcel.readException(Parcel.java:2285)
 *  at com.android.internal.telephony.ITelephonyRegistry$Stub$Proxy.listenForSubscriber(ITelephonyRegistry.java:1140)
 *  at android.telephony.TelephonyRegistryManager.listenForSubscriber(TelephonyRegistryManager.java:231)
 *  at android.telephony.TelephonyManager.listen(TelephonyManager.java:5617)
 * ```
 * debug究竟哪里注册了过多的listener！
 *
 * @author YvesCheung
 * 4/28/21
 */
class PidExceedNumberPhoneStateListeners @JvmOverloads constructor(
    private val logInfo: (info: String) -> Unit =
    //default implementation for log
        { info ->
            Log.i("TraceMethod", info)
        }
) : TraceMethod {

    override val serviceName: String = "telephony.registry"

    /**
     * 统计方法调用次数
     */
    private val happenTime = mutableMapOf<String, Int>()

    override fun invoke(proxy: Any?, actual: Any, method: Method, args: Array<out Any>): Any? {
        if (method.name == "transact") {
            val callStack = Log.getStackTraceString(CallStack())
            val cnt =
                synchronized(happenTime) {
                    val c = (happenTime[callStack] ?: 0) + 1
                    happenTime[callStack] = c
                    c
                }
            logInfo("method = ${method.name}, happen times = $cnt, $callStack")
        }
        return method.invoke(actual, *args)
    }
}