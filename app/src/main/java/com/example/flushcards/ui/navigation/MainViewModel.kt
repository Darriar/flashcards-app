package com.example.flushcards.ui.navigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flushcards.data.model.Module
import com.example.flushcards.data.storage.ModuleStorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class MainUiState(
    val currentScreen: Screen = Screen.MyModules,
    val modules: List<Module> = emptyList(),
    val currentModule: Module = Module(1, "", mutableListOf())
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun loadModules(context: Context) {
        viewModelScope.launch {
            val loadedStrings = ModuleStorageService.loadModule(context)
            val loadedModules = loadedStrings.map { string ->
                Json.decodeFromString<Module>(string)
            }
            _uiState.update { state ->
                state.copy(
                    modules = loadedModules,
                    currentModule = loadedModules.firstOrNull() ?: state.currentModule
                )
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectModule(module: Module) {
        _uiState.update { it.copy(currentModule = module, currentScreen = Screen.CurrentModule) }
    }

    fun addNewModule() {
        val id = if (_uiState.value.modules.isEmpty()) 1 else _uiState.value.modules.maxOf { it.id } + 1
        val newModule = Module(id, "Новый модуль", mutableListOf())
        _uiState.update { it.copy(currentModule = newModule, currentScreen = Screen.EditModule) }
    }

    fun saveModule(savedModule: Module) {
        _uiState.update { state ->
            val updatedList = state.modules.toMutableList()
            val index = updatedList.indexOfFirst { it.id == savedModule.id }
            if (index == -1) updatedList.add(savedModule) else updatedList[index] = savedModule
            state.copy(modules = updatedList, currentModule = savedModule, currentScreen = Screen.CurrentModule)
        }
    }

    fun deleteModuleFromList(deletedModule: Module) {
        _uiState.update { state ->
            val updatedList = state.modules.filter { it.id != deletedModule.id }
            val nextModule = updatedList.firstOrNull() ?: Module(1, "", mutableListOf())
            state.copy(
                modules = updatedList,
                currentModule = if (state.currentModule.id == deletedModule.id) nextModule else state.currentModule,
                currentScreen = Screen.MyModules
            )
        }
    }
}
