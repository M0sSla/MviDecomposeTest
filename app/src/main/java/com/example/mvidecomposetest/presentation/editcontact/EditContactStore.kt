package com.example.mvidecomposetest.presentation.editcontact

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.example.mvidecomposetest.data.RepositoryImpl
import com.example.mvidecomposetest.domain.Contact
import com.example.mvidecomposetest.domain.EditContactUseCase
import com.example.mvidecomposetest.domain.Repository
import com.example.mvidecomposetest.presentation.editcontact.EditContactStore.Intent
import com.example.mvidecomposetest.presentation.editcontact.EditContactStore.Label
import com.example.mvidecomposetest.presentation.editcontact.EditContactStore.State

interface EditContactStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class ChangeUsername(val username: String) : Intent
        data class ChangePhone(val phone: String) : Intent
        object SaveContact : Intent
    }

    data class State(
        val id: Int,
        val username: String,
        val phone: String
    )

    sealed interface Label {
        object ContactSaved : Label
    }
}

class EditContactStoreFactory {
    private val repository: Repository = RepositoryImpl
    private val editContactUseCase: EditContactUseCase = EditContactUseCase(repository)
    private val storeFactory: StoreFactory = LoggingStoreFactory(DefaultStoreFactory())

    fun create(contact: Contact): EditContactStore =
        object : EditContactStore, Store<Intent, State, Label> by storeFactory.create(
            name = "EditContactStore",
            initialState = State(id = contact.id, username = contact.username, phone = contact.phone),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action

    private sealed interface Msg {
        data class ChangeUsername(val username: String) : Msg
        data class ChangePhone(val phone: String) : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.ChangePhone -> {
                    dispatch(Msg.ChangePhone(phone = intent.phone))
                }

                is Intent.ChangeUsername -> {
                    dispatch(Msg.ChangeUsername(username = intent.username))
                }

                Intent.SaveContact -> {
                    val state = getState()
                    editContactUseCase(contact = Contact(username = state.username, phone = state.phone))
                    publish(Label.ContactSaved)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.ChangePhone -> copy(phone = msg.phone)
                is Msg.ChangeUsername -> copy(username = msg.username)
            }
    }
}
