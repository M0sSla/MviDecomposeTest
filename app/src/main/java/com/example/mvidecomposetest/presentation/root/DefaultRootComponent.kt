package com.example.mvidecomposetest.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.mvidecomposetest.domain.Contact
import com.example.mvidecomposetest.presentation.addcontact.DefaultAddContactComponent
import com.example.mvidecomposetest.presentation.contactlist.DefaultContactListComponent
import com.example.mvidecomposetest.presentation.editcontact.DefaultEditContactComponent
import kotlinx.parcelize.Parcelize

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.ContactList,
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        context: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            Config.AddContact -> {
                val component = DefaultAddContactComponent(
                    componentContext = context
                ) {
                    navigation.pop()
                }
                RootComponent.Child.AddContact(component)
            }
            Config.ContactList -> {
                val component = DefaultContactListComponent(
                    componentContext = context,
                    onEditingContactRequested = {
                        navigation.push(Config.EditContact(contact = it))
                    },
                    onAddContactRequested = {
                        navigation.push(Config.AddContact)
                    }
                )
                RootComponent.Child.ContactList(component)
            }
            is Config.EditContact -> {
                val component = DefaultEditContactComponent(
                    componentContext = context,
                    contact = config.contact
                ) {
                    navigation.pop()
                }
                RootComponent.Child.EditContact(component)
            }
        }
    }

    private sealed interface Config : Parcelable {
        @Parcelize
        object ContactList : Config
        @Parcelize
        object AddContact : Config
        @Parcelize
        data class EditContact(val contact: Contact) : Config
    }

}