package com.example.mvidecomposetest.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.mvidecomposetest.presentation.addcontact.AddContactComponent
import com.example.mvidecomposetest.presentation.contactlist.ContactListComponent
import com.example.mvidecomposetest.presentation.editcontact.EditContactComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class AddContact(val component: AddContactComponent) : Child
        data class EditContact(val component: EditContactComponent) : Child
        data class ContactList(val component: ContactListComponent) : Child
    }
}