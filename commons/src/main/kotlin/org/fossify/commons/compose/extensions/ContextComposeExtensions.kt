package com.adika.commons.compose.extensions

import android.app.Activity
import android.content.Context
import com.adika.commons.R
import com.adika.commons.extensions.baseConfig
import com.adika.commons.extensions.redirectToRateUs
import com.adika.commons.extensions.toast
import com.adika.commons.helpers.BaseConfig

val Context.config: BaseConfig get() = BaseConfig.newInstance(applicationContext)

fun Activity.rateStarsRedirectAndThankYou(stars: Int) {
    if (stars == 5) {
        redirectToRateUs()
    }
    toast(R.string.thank_you)
    baseConfig.wasAppRated = true
}
