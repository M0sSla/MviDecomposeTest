package com.example.mvidecomposetest.presentation.addcontact

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposetest.core.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultAddContactComponent(
    componentContext: ComponentContext,
    private val onContactSaved: () -> Unit
) : AddContactComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        AddContactStoreFactory().create()
    }

    init {
        componentScope.launch {
            store.labels.collect {
                when(it) {
                    AddContactStore.Label.ContactSaved -> onContactSaved()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AddContactStore.State>
        get() = store.stateFlow


    override fun onUsernameChanged(username: String) {
        store.accept(intent = AddContactStore.Intent.ChangeUsername(username = username))
    }

    override fun onPhoneChanged(phone: String) {
        store.accept(intent = AddContactStore.Intent.ChangePhone(phone = phone))
    }

    override fun onSavedContactClicked() {
        store.accept(intent = AddContactStore.Intent.SaveContact)
    }
}