package com.adika.commons.samples

import com.github.ajalt.reprint.core.Reprint
import com.adika.commons.FossifyApp

class App : FossifyApp() {
    override fun onCreate() {
        super.onCreate()
        Reprint.initialize(this)
    }
}
