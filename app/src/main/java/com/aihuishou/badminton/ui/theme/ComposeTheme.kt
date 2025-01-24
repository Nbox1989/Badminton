package com.aihuishou.badminton.ui.theme

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposeTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalRippleConfiguration provides null) {
        content()
//        ProvideTextStyle(value = MaterialTheme.typography.body1, content = content)
    }
}
