package com.example.mvidecomposetest.presentation.addcontact

import kotlinx.coroutines.flow.StateFlow

interface AddContactComponent {
    val model: StateFlow<AddContactStore.State>

    fun onUsernameChanged(username: String)
    fun onPhoneChanged(phone: String)
    fun onSavedContactClicked()
}