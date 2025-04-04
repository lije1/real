package com.adika.commons.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.adika.commons.R
import com.adika.commons.compose.alert_dialog.rememberAlertDialogState
import com.adika.commons.dialogs.ConfirmationAlertDialog
import com.adika.commons.extensions.launchViewIntent

@Composable
fun FakeVersionCheck() {
    val context = LocalContext.current
    val confirmationDialogAlertDialogState = rememberAlertDialogState().apply {
        DialogMember {
            ConfirmationAlertDialog(
                alertDialogState = this,
                message = FAKE_VERSION_APP_LABEL,
                positive = R.string.ok,
                negative = null
            ) {
                context.getActivity().launchViewIntent(DEVELOPER_PLAY_STORE_URL)
            }
        }
    }
    LaunchedEffect(Unit) {
        context.fakeVersionCheck(confirmationDialogAlertDialogState::show)
    }
}

@Composable
fun CheckAppOnSdCard() {
    val context = LocalContext.current.getComponentActivity()
    val confirmationDialogAlertDialogState = rememberAlertDialogState().apply {
        DialogMember {
            ConfirmationAlertDialog(
                alertDialogState = this,
                messageId = R.string.app_on_sd_card,
                positive = R.string.ok,
                negative = null
            ) {}
        }
    }
    LaunchedEffect(Unit) {
        context.appOnSdCardCheckCompose(confirmationDialogAlertDialogState::show)
    }
}
