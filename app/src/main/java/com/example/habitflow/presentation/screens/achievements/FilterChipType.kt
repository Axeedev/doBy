package com.example.habitflow.presentation.screens.achievements

import com.example.habitflow.R

enum class FilterChipType(val titleId: Int ) {

    IN_PROGRESS(R.string.filter_chip_in_progress),
    COMPLETED(R.string.filter_chip_completed),
    ALL(R.string.filter_chip_all)
}