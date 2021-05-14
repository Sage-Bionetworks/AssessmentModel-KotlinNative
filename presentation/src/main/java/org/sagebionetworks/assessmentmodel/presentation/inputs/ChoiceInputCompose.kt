package org.sagebionetworks.assessmentmodel.presentation.inputs

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable

@Composable
fun TextInput() {
    TextField(value = "", onValueChange = { /*TODO*/ })
}

@Composable
fun Greeting(name: String) {
    Text (text = "Hello $name!")
}