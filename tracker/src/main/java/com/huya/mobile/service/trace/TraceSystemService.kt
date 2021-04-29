package com.huya.mobile.service.trace

import android.os.IBinder
import android.os.ServiceManager
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author YvesCheung
 * 4/28/21
 */
object TraceSystemService {

    @Suppress("UNCHECKED_CAST")
    private val binders: MutableMap<String, IBinder> by lazy {
        val cache = ServiceManager::class.java.getDeclaredField("sCache")
        cache.isAccessible = true
        cache.get(null) as MutableMap<String, IBinder>
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun trace(target: TraceMethod) {
        try {
            val serviceName = target.serviceName
            binders[serviceName] =
                Proxy.newProxyInstance(
                    ServiceManager::class.java.classLoader,
                    arrayOf(IBinder::class.java),
                    ProxyHandler(target, ServiceManager.getService(serviceName))
                ) as IBinder
        } catch (e: Throwable) {
            Log.e("TraceMethod", "Can't hook ServiceManager!", e)
        }
    }

    private class ProxyHandler(val target: TraceMethod, val actual: Any) : InvocationHandler {

        override fun invoke(proxy: Any?, method: Method, args: Array<out Any>): Any? {
            return target.invoke(proxy, actual, method, args)
        }
    }
}