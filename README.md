# TraceSystemService

> A tools to debug or intercept the methods call for Android `Context.getSystemService`.

### 遇到的问题

线上监控到了应用在 Android11 上出现较多这样的崩溃：

```
Caused by: java.lang.IllegalStateException: Pid 25211 has exceeded the number of permissible registered listeners. Ignoring request to add.
	at android.os.Parcel.createExceptionOrNull(Parcel.java:2395)
	at android.os.Parcel.createException(Parcel.java:2371)
	at android.os.Parcel.readException(Parcel.java:2354)
	at android.os.Parcel.readException(Parcel.java:2296)
	at com.android.internal.telephony.ITelephonyRegistry$Stub$Proxy.listenForSubscriber(ITelephonyRegistry.java:1105)
	at android.telephony.TelephonyRegistryManager.listenForSubscriber(TelephonyRegistryManager.java:242)
	at android.telephony.TelephonyManager.listen(TelephonyManager.java:5886)
```

多次调用了 `getSystemService(Context.TELEPHONY_SERVICE).listen(PhoneStateListener)` 方法，导致注册的监听器过多。
在 Android11 以前监听器的数量限制比较宽松，随着使用 Android11 的用户越来越多，问题会越来越突出。
从堆栈上看最后一次调用的地方，可能只是压死骆驼的最后一个监听器，未必是注册最多的地方。
所以就需要一种定位问题的手段，监控通过 `getSystemService(Context.TELEPHONY_SERVICE)` 来调用方法的代码堆栈和调用次数！

**TraceSystemService** 通过动态代理 `ServiceManager.getService()` 的对象，从而在系统服务方法调用的时候，打印出调用的堆栈和次数！

### 使用

```
class App : Application() {

    override fun onCreate() {
        TraceSystemService.trace(PidExceedNumberPhoneStateListeners())
    }
}
```

尽可能早地调用 `TraceSystemService.trace` 方法，追踪要监听的系统服务和方法。
在Logcat中可以看到方法调用的堆栈：

```
2021-04-29 12:10:16.043 8961-8961/com.huya.mobile.service.trace I/TraceMethod: method = transact, happen times = 1, Call Stack
        at com.huya.mobile.service.trace.PidExceedNumberPhoneStateListeners.invoke(PidExceedNumberPhoneStateListeners.kt:33)
        at com.huya.mobile.service.trace.TraceSystemService$ProxyHandler.invoke(TraceSystemService.kt:42)
        at java.lang.reflect.Proxy.invoke(Proxy.java:1006)
        at $Proxy1.transact(Unknown Source)
        at com.android.internal.telephony.ITelephonyRegistry$Stub$Proxy.listenForSubscriber(ITelephonyRegistry.java:1100)
        at android.telephony.TelephonyRegistryManager.listenForSubscriber(TelephonyRegistryManager.java:231)
        at android.telephony.TelephonyManager.listen(TelephonyManager.java:6706)
        at com.huya.mobile.service.trace.demo.MainReceiver.onReceive(MainReceiver.kt:18)
        at android.app.ActivityThread.handleReceiver(ActivityThread.java:4443)
        at android.app.ActivityThread.access$1600(ActivityThread.java:301)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2159)
        at android.os.Handler.dispatchMessage(Handler.java:106)
        at android.os.Looper.loop(Looper.java:246)
        at android.app.ActivityThread.main(ActivityThread.java:8506)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:602)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1130)
```

可以发现 `at com.huya.mobile.service.trace.demo.MainReceiver.onReceive(MainReceiver.kt:18)` 在这一行代码中有注册监听器的代码。
而且到目前为止通过这行代码注册的监听器数量是 `happen times = 1` 。

### 安装

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.YvesCheung:TraceSystemService:x.y.z'
}
```