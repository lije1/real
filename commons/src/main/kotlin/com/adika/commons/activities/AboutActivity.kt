package com.adika.commons.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.adika.commons.R
import com.adika.commons.compose.alert_dialog.rememberAlertDialogState
import com.adika.commons.compose.extensions.enableEdgeToEdgeSimple
import com.adika.commons.compose.extensions.rateStarsRedirectAndThankYou
import com.adika.commons.compose.screens.*
import com.adika.commons.compose.theme.AppThemeSurface
import com.adika.commons.dialogs.ConfirmationAdvancedAlertDialog
import com.adika.commons.dialogs.RateStarsAlertDialog
import com.adika.commons.extensions.*
import com.adika.commons.helpers.*
import com.adika.commons.models.FAQItem

class AboutActivity : BaseComposeActivity() {
    private val appName get() = intent.getStringExtra(APP_NAME) ?: ""

    private var firstVersionClickTS = 0L
    private var clicksSinceFirstClick = 0

    companion object {
        private const val EASTER_EGG_TIME_LIMIT = 3000L
        private const val EASTER_EGG_REQUIRED_CLICKS = 7
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            val context = LocalContext.current
            val resources = context.resources
            AppThemeSurface {
                val showGoogleRelations = remember { !resources.getBoolean(R.bool.hide_google_relations) }
                val showGithubRelations = showGithubRelations()
                val showDonationLinks = remember { resources.getBoolean(R.bool.show_donate_in_about) }
                val onEmailClickAlertDialogState = getOnEmailClickAlertDialogState()
                val rateStarsAlertDialogState = getRateStarsAlertDialogState()
                val onRateUsClickAlertDialogState = getOnRateUsClickAlertDialogState(rateStarsAlertDialogState::show)
                AboutScreen(
                    goBack = ::finish,
                    helpUsSection = {
                        HelpUsSection(
                            onRateUsClick = {
                                onRateUsClick(
                                    showConfirmationAdvancedDialog = onRateUsClickAlertDialogState::show,
                                    showRateStarsDialog = rateStarsAlertDialogState::show
                                )
                            },
                            onInviteClick = ::onInviteClick,
                           
                            showDonate = showDonationLinks,
                            onDonateClick = ::onDonateClick,
                            showInvite = showGoogleRelations || showGithubRelations,
                            showRateUs = showGoogleRelations
                        )
                    },
                    aboutSection = {
                        val setupFAQ = showFAQ()
                        if (setupFAQ || showGithubRelations) {
                            AboutSection(
                                 onEmailClick = {
                                    onEmailClick(onEmailClickAlertDialogState::show)
                                }
                            )
                        }
                    },
                    socialSection = {
                        SocialSection(
                             
                         
                            onTelegramClick = ::onTelegramClick
                        )
                    }
                ) {
                    val (versionName, packageName) = getPackageInfo()
                    OtherSection(
                        showMoreApps = showGoogleRelations,
                        onMoreAppsClick = ::launchMoreAppsFromUsIntent,
                        onPrivacyPolicyClick = ::onPrivacyPolicyClick,
                        versionName = versionName,
                        packageName = packageName,
                        onVersionClick = ::onVersionClick
                    )
                }
            }
        }
    }

  

 
    @Composable
    private fun getPackageInfo(): Pair<String, String> {
        var versionName = remember { intent.getStringExtra(APP_VERSION_NAME) ?: "" }
        val packageName = remember { intent.getStringExtra(APP_PACKAGE_NAME) ?: "" }
        if (baseConfig.appId.removeSuffix(".debug").endsWith(".pro")) {
            versionName += " ${getString(R.string.pro)}"
        }

        val fullVersion = stringResource(R.string.version_placeholder, versionName)
        return Pair(fullVersion, packageName)
    }

    @Composable
    private fun getRateStarsAlertDialogState() =
        rememberAlertDialogState().apply {
            DialogMember {
                RateStarsAlertDialog(
                    alertDialogState = this,
                    onRating = ::rateStarsRedirectAndThankYou
                )
            }
        }

    @Composable
    private fun getOnEmailClickAlertDialogState() =
        rememberAlertDialogState().apply {
            DialogMember {
                ConfirmationAdvancedAlertDialog(
                    alertDialogState = this,
                    message = "${getString(R.string.before_asking_question_read_faq)}\n\n${getString(R.string.make_sure_latest)}",
                    messageId = null,
                    positive = R.string.read_faq,
                    negative = R.string.skip
                ) { success ->
                    if (success) {
                        
                    } else {
                        launchEmailIntent()
                    }
                }
            }
        }

    @Composable
    private fun getOnRateUsClickAlertDialogState(showRateStarsDialog: () -> Unit) =
        rememberAlertDialogState().apply {
            DialogMember {
                ConfirmationAdvancedAlertDialog(
                    alertDialogState = this,
                    message = "${getString(R.string.before_asking_question_read_faq)}\n\n${getString(R.string.make_sure_latest)}",
                    messageId = null,
                    positive = R.string.read_faq,
                    negative = R.string.skip
                ) { success ->
                    if (success) {
 
                    } else {
                        launchRateUsPrompt(showRateStarsDialog)
                    }
                }
            }
        }

    private fun onEmailClick(
        showConfirmationAdvancedDialog: () -> Unit,
    ) {
        if (intent.getBooleanExtra(SHOW_FAQ_BEFORE_MAIL, false)
            && !baseConfig.wasBeforeAskingShown
        ) {
            baseConfig.wasBeforeAskingShown = true
            showConfirmationAdvancedDialog()
        } else {
            launchEmailIntent()
        }
    }

 

    private fun launchEmailIntent() {
        val appVersion = getString(R.string.app_version, intent.getStringExtra(APP_VERSION_NAME))
        val deviceOS = getString(R.string.device_os, Build.VERSION.RELEASE)
        val newline = "\n"
        val separator = "------------------------------"
        val body = "$appVersion$newline$deviceOS$newline$separator$newline$newline"

        val address = if (packageName.startsWith("com.adika")) {
            getString(R.string.my_email)
        } else {
            getString(R.string.my_fake_email)
        }

        val selectorIntent = Intent(ACTION_SENDTO)
            .setData("mailto:$address".toUri())
        val emailIntent = Intent(ACTION_SEND).apply {
            putExtra(EXTRA_EMAIL, arrayOf(address))
            putExtra(EXTRA_SUBJECT, appName)
            putExtra(EXTRA_TEXT, body)
            selector = selectorIntent
        }

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            val chooser = createChooser(emailIntent, getString(R.string.send_email))
            try {
                startActivity(chooser)
            } catch (e: Exception) {
                toast(R.string.no_email_client_found)
            }
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }

    private fun onRateUsClick(
        showConfirmationAdvancedDialog: () -> Unit,
        showRateStarsDialog: () -> Unit,
    ) {
        if (baseConfig.wasBeforeRateShown) {
            launchRateUsPrompt(showRateStarsDialog)
        } else {
            baseConfig.wasBeforeRateShown = true
            showConfirmationAdvancedDialog()
        }
    }

    private fun launchRateUsPrompt(
        showRateStarsDialog: () -> Unit,
    ) {
        if (baseConfig.wasAppRated) {
            redirectToRateUs()
        } else {
            showRateStarsDialog()
        }
    }

    private fun onInviteClick() {
        val storeUrl = when {
            resources.getBoolean(R.bool.hide_google_relations) -> getGithubUrl()
            else -> getStoreUrl()
        }

        val text = String.format(getString(R.string.share_text), appName, storeUrl)
        Intent().apply {
            action = ACTION_SEND
            putExtra(EXTRA_SUBJECT, appName)
            putExtra(EXTRA_TEXT, text)
            type = "text/plain"
            startActivity(createChooser(this, getString(R.string.invite_via)))
        }
    }

   

    private fun onDonateClick() {
        startActivity(Intent(applicationContext, DonationActivity::class.java))
    }

   

   


    private fun onTelegramClick() {
        launchViewIntent("https://t.me/adikaapps")
    }

    private fun onPrivacyPolicyClick() {
        val appId = baseConfig.appId.removeSuffix(".debug").removeSuffix(".pro")
            .removePrefix("com.adika.")
        val url = "https://contactsmanager.app/privacy-policy-contacts/"
        launchViewIntent(url)
    }
 

    private fun onVersionClick() {
        if (firstVersionClickTS == 0L) {
            firstVersionClickTS = System.currentTimeMillis()
            Handler(Looper.getMainLooper()).postDelayed({
                firstVersionClickTS = 0L
                clicksSinceFirstClick = 0
            }, EASTER_EGG_TIME_LIMIT)
        }

        clicksSinceFirstClick++
        if (clicksSinceFirstClick >= EASTER_EGG_REQUIRED_CLICKS) {
            toast(R.string.hello)
            firstVersionClickTS = 0L
            clicksSinceFirstClick = 0
        }
    }
}
