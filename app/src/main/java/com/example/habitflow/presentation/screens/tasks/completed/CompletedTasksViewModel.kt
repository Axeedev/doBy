package com.example.habitflow.presentation.screens.tasks.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.domain.usecases.tasks.GetCompletedTaskUseCase
import com.example.habitflow.domain.usecases.tasks.ReturnTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletedTasksViewModel @Inject constructor(
    private val getCompletedTaskUseCase: GetCompletedTaskUseCase,
    private val returnTaskUseCase: ReturnTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CompletedTasksScreenState())

    val state
        get() = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getCompletedTaskUseCase()
                .collect {
                    _state.value = CompletedTasksScreenState(it)
                }
        }
    }

    fun processCommand(command: CompletedTasksCommand){
        when(command){
            is CompletedTasksCommand.ClickReturnTask -> {
                viewModelScope.launch {
                    returnTaskUseCase(command.taskId)
                }
            }
        }
    }


}