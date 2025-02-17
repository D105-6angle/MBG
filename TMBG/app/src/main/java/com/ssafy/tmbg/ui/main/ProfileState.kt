package com.ssafy.tmbg.ui.main

import android.provider.ContactsContract.Profile
import com.ssafy.tmbg.data.profile.response.ProfileResponse

sealed class ProfileState {
    data object Initial : ProfileState()
    data object Loading : ProfileState()
    data class Success(
        val profileResponse: ProfileResponse? = null,
    ) : ProfileState()
    data class Error(val message: String) : ProfileState()
}