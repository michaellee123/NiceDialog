package dog.abcd.nicedialog.demo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.e("lifecycle", "onActivityCreated$activity")
            }

            override fun onActivityStarted(activity: Activity) {
                Log.e("lifecycle", "onActivityStarted$activity")
            }

            override fun onActivityResumed(activity: Activity) {
                Log.e("lifecycle", "onActivityResumed$activity")
            }

            override fun onActivityPaused(activity: Activity) {
                Log.e("lifecycle", "onActivityPaused$activity")
            }

            override fun onActivityStopped(activity: Activity) {
                Log.e("lifecycle", "onActivityStopped$activity")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.e("lifecycle", "onActivitySaveInstanceState$activity")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.e("lifecycle", "onActivityDestroyed$activity")
            }

        })
    }
}