package com.example.mvidecomposetest.presentation.contactlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposetest.core.componentScope
import com.example.mvidecomposetest.domain.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultContactListComponent(
    componentContext: ComponentContext,
    private val onEditingContactRequested: (Contact) -> Unit,
    private val onAddContactRequested: () -> Unit,

) : ContactListComponent, ComponentContext by componentContext {
    private val store = instanceKeeper.getStore {
        ContactListStoreFactory().create()
    }

    init {
        componentScope.launch {
            store.labels.collect {
                when(it) {
                    ContactListStore.Label.AddContact -> {
                        onAddContactRequested()
                    }
                    is ContactListStore.Label.EditContact -> {
                        onEditingContactRequested(it.contact)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<ContactListStore.State>
        get() = store.stateFlow

    override fun onContactClicked(contact: Contact) {
        store.accept(ContactListStore.Intent.EditContact(contact = contact))
    }

    override fun onAddContactClicked() {
        store.accept(ContactListStore.Intent.AddContact)
    }
}