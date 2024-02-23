package com.example.mvidecomposetest.ui.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.example.mvidecomposetest.presentation.root.RootComponent
import com.example.mvidecomposetest.ui.theme.MviDecomposeTestTheme

@Composable
fun RootContent(
    component: RootComponent
) {
    MviDecomposeTestTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Children(stack = component.stack) {
                when (val instance = it.instance) {
                    is RootComponent.Child.AddContact -> AddContact(component = instance.component)
                    is RootComponent.Child.ContactList -> Contacts(component = instance.component)
                    is RootComponent.Child.EditContact -> EditContact(component = instance.component)
                }
            }
        }
    }
}