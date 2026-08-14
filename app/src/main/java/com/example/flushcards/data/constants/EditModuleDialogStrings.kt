package com.example.flushcards.data.constants

object EditModuleDialogStrings {
    // Выход без сохранения
    const val EXIT_TITLE = "У вас есть несохраненные изменения. Вы уверены, что хотите выйти?"
    const val EXIT_AGREE = "Выйти без сохранения"
    const val EXIT_DISAGREE = "Отмена"

    // Предупреждение об удалении невалидных карточек
    const val INVALID_CARDS_TITLE =
        "При сохранении карточки с пустыми полями или с одинаковыми терминами будут удалены, вы уверены?"
    const val INVALID_CARDS_AGREE = "Сохранить изменения"
    const val INVALID_CARDS_DISAGREE = "Отмена"

    // Дубликат названия
    const val DUPLICATE_NAME_TITLE = "Модуль с таким названием уже существует. Переименуйте модуль"
    const val DUPLICATE_NAME_AGREE = "Хорошо"

    // Недостаточно карточек
    fun notEnoughCardsTitle(missingCount: Int) = "Недостаточно карточек, добавьте еще $missingCount"
    const val NOT_ENOUGH_CARDS_AGREE = "Хорошо"
}