package com.example.mvidecomposetest.presentation.editcontact

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.mvidecomposetest.core.componentScope
import com.example.mvidecomposetest.domain.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultEditContactComponent(
    componentContext: ComponentContext,
    private val contact: Contact,
    private val onContactSaved: () -> Unit
) : EditContactComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        EditContactStoreFactory().create(contact)
    }

    init {
        componentScope.launch {
            store.labels.collect {
                when(it) {
                    EditContactStore.Label.ContactSaved -> onContactSaved()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<EditContactStore.State>
        get() = store.stateFlow

    override fun onUsernameChanged(username: String) {
        store.accept(intent = EditContactStore.Intent.ChangeUsername(username = username))
    }

    override fun onPhoneChanged(phone: String) {
        store.accept(intent = EditContactStore.Intent.ChangePhone(phone = phone))
    }

    override fun onSavedContactClicked() {
        store.accept(intent = EditContactStore.Intent.SaveContact)
    }
}