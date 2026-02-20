package com.example.habitflow.presentation.screens.tasks.all

sealed interface BottomSheetEvent {

    data object CloseSheet: BottomSheetEvent

    data object OpenSheet : BottomSheetEvent

}