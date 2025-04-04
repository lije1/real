package com.adika.commons

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.adika.commons.extensions.appLockManager
import com.adika.commons.extensions.checkUseEnglish

open class FossifyApp : Application() {

    open val isAppLockFeatureAvailable = false

    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
        setupAppLockManager()
    }

    private fun setupAppLockManager() {
        if (isAppLockFeatureAvailable) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(appLockManager)
        }
    }
}
