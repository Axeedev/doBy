package com.example.habitflow.presentation.screens.settings

sealed interface BottomSheetType {

    data object NotifyBefore: BottomSheetType

    data object NightTimeNotification : BottomSheetType

    data object MorningTimeNotification : BottomSheetType

}