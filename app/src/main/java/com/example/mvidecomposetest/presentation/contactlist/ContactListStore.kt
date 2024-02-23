package com.example.mvidecomposetest.presentation.contactlist

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.Contact
import com.example.mvidecomposetest.domain.GetContactsUseCase
import com.example.mvidecomposetest.domain.Repository
import com.example.mvidecomposetest.presentation.contactlist.ContactListStore.Intent
import com.example.mvidecomposetest.presentation.contactlist.ContactListStore.Label
import com.example.mvidecomposetest.presentation.contactlist.ContactListStore.State
import kotlinx.coroutines.launch

interface ContactListStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class EditContact(val contact: Contact) : Intent
        object AddContact : Intent
    }

    data class State(
        val contacts: List<Contact>
    )

    sealed interface Label {
        object AddContact : Label
        data class EditContact(val contact: Contact) : Label
    }
}

class ContactListStoreFactory {
    private val repository: Repository = RepositoryImpl
    private val getContactsUseCase: GetContactsUseCase = GetContactsUseCase(repository)
    private val storeFactory: StoreFactory = LoggingStoreFactory(DefaultStoreFactory())

    fun create(): ContactListStore =
        object : ContactListStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ContactListStore",
            initialState = State(contacts = listOf()),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ContactsLoaded(val contacts: List<Contact>) : Action
    }

    private sealed interface Msg {
        data class ContactsLoaded(val contacts: List<Contact>) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getContactsUseCase().collect {
                    dispatch(Action.ContactsLoaded(it))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.AddContact -> {
                    publish(Label.AddContact)
                }
                is Intent.EditContact -> {
                    publish(Label.EditContact(contact = intent.contact))
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.ContactsLoaded -> {
                    dispatch(Msg.ContactsLoaded(contacts = action.contacts))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ContactsLoaded -> copy(contacts = msg.contacts)
            }
    }
}
