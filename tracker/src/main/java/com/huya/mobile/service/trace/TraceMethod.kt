package com.huya.mobile.service.trace

import java.lang.reflect.Method

/**
 * @author YvesCheung
 * 4/28/21
 */
interface TraceMethod {

    val serviceName: String

    fun invoke(proxy: Any?, actual: Any, method: Method, args: Array<out Any>): Any?
}