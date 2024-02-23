package com.example.mvidecomposetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.example.mvidecomposetest.presentation.root.DefaultRootComponent
import com.example.mvidecomposetest.ui.content.RootContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponent = DefaultRootComponent(defaultComponentContext())
        setContent {
            RootContent(component = rootComponent)
        }
    }
}