package com.example.mvidecomposetest.presentation.contactlist

import com.example.mvidecomposetest.domain.Contact
import kotlinx.coroutines.flow.StateFlow

interface ContactListComponent {
    val model: StateFlow<ContactListStore.State>

    fun onContactClicked(contact: Contact)
    fun onAddContactClicked()
}