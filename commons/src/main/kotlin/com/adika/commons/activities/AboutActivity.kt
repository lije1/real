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
                            onContributorsClick = ::onContributorsClick,
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
                                setupFAQ = setupFAQ,
                                setupKnownIssues = showGithubRelations,
                                onFAQClick = ::launchFAQActivity,
                                onKnownIssuesClick = ::launchIssueTracker,
                                onEmailClick = {
                                    onEmailClick(onEmailClickAlertDialogState::show)
                                }
                            )
                        }
                    },
                    socialSection = {
                        SocialSection(
                            onGithubClick = ::onGithubClick,
                            onRedditClick = ::onRedditClick,
                            onTelegramClick = ::onTelegramClick
                        )
                    }
                ) {
                    val (versionName, packageName) = getPackageInfo()
                    OtherSection(
                        showMoreApps = showGoogleRelations,
                        onMoreAppsClick = ::launchMoreAppsFromUsIntent,
                        onPrivacyPolicyClick = ::onPrivacyPolicyClick,
                        onLicenseClick = ::onLicenseClick,
                        versionName = versionName,
                        packageName = packageName,
                        onVersionClick = ::onVersionClick
                    )
                }
            }
        }
    }

    private fun getGithubUrl(): String {
        return "https://github.com/FossifyOrg/${intent.getStringExtra(APP_REPOSITORY_NAME)}"
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
                        launchFAQActivity()
                    } else {
                        launchEmailIntent()
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

 

    private fun launchIssueTracker() {
        launchViewIntent(
            "${getGithubUrl()}/issues?q=is:open+is:issue+label:bug"
        )
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

   
 

    private fun onPrivacyPolicyClick() {
        val appId = baseConfig.appId.removeSuffix(".debug").removeSuffix(".pro")
            .removePrefix("com.adika.")
        val url = "https://www.contactsmanager.app/policy/$appId"
        launchViewIntent(url)
    }

    private fun onLicenseClick() {
        Intent(applicationContext, LicenseActivity::class.java).apply {
            putExtra(
                APP_ICON_IDS,
                intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList<String>()
            )
            putExtra(APP_LAUNCHER_NAME, intent.getStringExtra(APP_LAUNCHER_NAME) ?: "")
            putExtra(APP_LICENSES, intent.getLongExtra(APP_LICENSES, 0))
            startActivity(this)
        }
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
