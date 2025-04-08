package com.adika.commons.compose.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.adika.commons.R
import com.adika.commons.compose.extensions.MyDevices
import com.adika.commons.compose.lists.SimpleColumnScaffold
import com.adika.commons.compose.settings.SettingsGroup
import com.adika.commons.compose.settings.SettingsHorizontalDivider
import com.adika.commons.compose.settings.SettingsListItem
import com.adika.commons.compose.settings.SettingsTitleTextComponent
import com.adika.commons.compose.theme.AppThemeSurface
import com.adika.commons.compose.theme.SimpleTheme

private val titleStartPadding = Modifier.padding(start = 40.dp)

@Composable
internal fun AboutScreen(
    goBack: () -> Unit,
    helpUsSection: @Composable () -> Unit,
    aboutSection: @Composable () -> Unit,
    socialSection: @Composable () -> Unit,
    otherSection: @Composable () -> Unit,
) {
    SimpleColumnScaffold(title = stringResource(id = R.string.about), goBack = goBack) {
        aboutSection()
        helpUsSection()
        socialSection()
        otherSection()
//        SettingsListItem(text = stringResource(id = R.string.about_footer))
    }
}

@Composable
internal fun HelpUsSection(
    onRateUsClick: () -> Unit,
    onInviteClick: () -> Unit,
  
    showRateUs: Boolean,
    showInvite: Boolean,
    showDonate: Boolean,
    onDonateClick: () -> Unit,
) {
    SettingsGroup(title = {
        SettingsTitleTextComponent(
            text = stringResource(id = R.string.help_us),
            modifier = titleStartPadding
        )
    }) {
        if (showRateUs) {
            TwoLinerTextItem(
                text = stringResource(id = R.string.rate_us),
                icon = R.drawable.ic_star_outline_vector,
                click = onRateUsClick
            )
        }

        if (showInvite) {
            TwoLinerTextItem(
                text = stringResource(id = R.string.invite_friends),
                icon = R.drawable.ic_share_outline_vector,
                click = onInviteClick
            )
        }

      

        if (showDonate) {
            TwoLinerTextItem(
                click = onDonateClick,
                text = stringResource(id = R.string.donate_to_fossify),
                icon = R.drawable.ic_donate_outline_vector
            )
        }

        SettingsHorizontalDivider()
    }
}

@Composable
internal fun OtherSection(
    showMoreApps: Boolean,
    onMoreAppsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
 
    versionName: String,
    packageName: String,
    onVersionClick: () -> Unit,
) {
    SettingsGroup(title = {
        SettingsTitleTextComponent(
            text = stringResource(id = R.string.other),
            modifier = titleStartPadding
        )
    }) {
        if (showMoreApps) {
            TwoLinerTextItem(
                click = onMoreAppsClick,
                text = stringResource(id = R.string.more_apps_from_us),
                icon = R.drawable.ic_apps_vector
            )
        }

        TwoLinerTextItem(
            click = onPrivacyPolicyClick,
            text = stringResource(id = R.string.privacy_policy),
            icon = R.drawable.ic_policy_outline_vector
        )
 
        SettingsListItem(
            tint = SimpleTheme.colorScheme.onSurface,
            click = onVersionClick,
            text = versionName,
            description = packageName,
            icon = R.drawable.ic_info_outline_vector,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        SettingsHorizontalDivider()
    }
}


@Composable
internal fun AboutSection(
     
    onEmailClick: () -> Unit,
) {
    SettingsGroup(title = {
        SettingsTitleTextComponent(
            text = stringResource(id = R.string.support),
            modifier = titleStartPadding
        )
    }) {
        
     

        TwoLinerTextItem(
            click = onEmailClick,
            text = stringResource(id = R.string.my_email),
            icon = R.drawable.ic_contact_support_outline_vector
        )
        SettingsHorizontalDivider()
    }
}

@Composable
internal fun SocialSection(
     
     
    onTelegramClick: () -> Unit,
) {
    SettingsGroup(title = {
        SettingsTitleTextComponent(
            text = stringResource(id = R.string.social),
            modifier = titleStartPadding
        )
    }) {
         
       
        SocialText(
            click = onTelegramClick,
            text = stringResource(id = R.string.telegram),
            icon = R.drawable.ic_telegram_vector,
        )
        SettingsHorizontalDivider()
    }
}

@Composable
internal fun SocialText(
    text: String,
    icon: Int,
    tint: Color? = null,
    click: () -> Unit,
) {
    SettingsListItem(
        click = click,
        text = text,
        icon = icon,
        isImage = true,
        tint = tint,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun TwoLinerTextItem(text: String, icon: Int, click: () -> Unit) {
    SettingsListItem(
        tint = SimpleTheme.colorScheme.onSurface,
        click = click,
        text = text,
        icon = icon,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@MyDevices
@Composable
private fun AboutScreenPreview() {
    AppThemeSurface {
        AboutScreen(
            goBack = {},
            helpUsSection = {
                HelpUsSection(
                    onRateUsClick = {},
                    onInviteClick = {},
                    
                    showRateUs = true,
                    showInvite = true,
                    showDonate = true,
                    onDonateClick = {}
                )
            },
            aboutSection = {
                AboutSection(  
                    onEmailClick = {})
            },
            socialSection = {
                SocialSection(
                     
                     
                    onTelegramClick = {}
                )
            }
        ) {
            OtherSection(
                showMoreApps = true,
                onMoreAppsClick = {},
                onPrivacyPolicyClick = {},
                onLicenseClick = {},
                versionName = "5.0.4",
                packageName = "com.adika.commons.samples",
                onVersionClick = {}
            )
        }
    }
}
