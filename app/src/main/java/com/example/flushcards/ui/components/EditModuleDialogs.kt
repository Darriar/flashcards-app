package com.example.flushcards.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.example.flushcards.data.constants.EditModuleDialogStrings

sealed interface EditModuleDialogState {
    object None : EditModuleDialogState
    object ExitConfirm : EditModuleDialogState
    object SaveWithInvalidCards : EditModuleDialogState
    object DuplicateName : EditModuleDialogState
    data class NotEnoughCards(val missingCount: Int) : EditModuleDialogState
}

@Composable
fun EditModuleDialogs(
    dialogState: EditModuleDialogState,
    onDismiss: () -> Unit,
    onConfirmExit: () -> Unit,
    onConfirmSaveInvalid: () -> Unit
) {
    when (dialogState) {
        EditModuleDialogState.None -> {
        }

        EditModuleDialogState.ExitConfirm -> {
            Dialog(onDismissRequest = onDismiss) {
                NotificationCard(
                    title = EditModuleDialogStrings.EXIT_TITLE,
                    agreeText = EditModuleDialogStrings.EXIT_AGREE,
                    disagreeText = EditModuleDialogStrings.EXIT_DISAGREE,
                    onAgree = {
                        onDismiss()
                        onConfirmExit()
                    },
                    onDisagree = onDismiss
                )
            }
        }

        EditModuleDialogState.SaveWithInvalidCards -> {
            Dialog(onDismissRequest = onDismiss) {
                NotificationCard(
                    title = EditModuleDialogStrings.INVALID_CARDS_TITLE,
                    agreeText = EditModuleDialogStrings.INVALID_CARDS_AGREE,
                    disagreeText = EditModuleDialogStrings.INVALID_CARDS_DISAGREE,
                    onAgree = {
                        onDismiss()
                        onConfirmSaveInvalid()
                    },
                    onDisagree = onDismiss
                )
            }
        }

        EditModuleDialogState.DuplicateName -> {
            Dialog(onDismissRequest = onDismiss) {
                NotificationCard(
                    title = EditModuleDialogStrings.DUPLICATE_NAME_TITLE,
                    agreeText = EditModuleDialogStrings.DUPLICATE_NAME_AGREE,
                    disagreeText = null,
                    onAgree = onDismiss,
                    onDisagree = onDismiss
                )
            }
        }

        is EditModuleDialogState.NotEnoughCards -> {
            Dialog(onDismissRequest = onDismiss) {
                NotificationCard(
                    title = EditModuleDialogStrings.notEnoughCardsTitle(dialogState.missingCount),
                    agreeText = EditModuleDialogStrings.NOT_ENOUGH_CARDS_AGREE,
                    disagreeText = null,
                    onAgree = onDismiss,
                    onDisagree = onDismiss
                )
            }
        }
    }
}