package com.ssafy.mbg.ui.page

sealed class SatisfactionUiState {
    data object Loading: SatisfactionUiState()
    data object Success : SatisfactionUiState()
    data class Error(val message: String) : SatisfactionUiState()
}