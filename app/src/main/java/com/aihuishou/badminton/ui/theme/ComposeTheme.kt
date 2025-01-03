package com.aihuishou.badminton.ui.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

@Composable
fun ComposeTheme(
    content: @Composable () -> Unit
) {
    // 设置全局参数，去除默认点击效果
    CompositionLocalProvider(
        LocalIndication provides NoIndication
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.body1, content = content)
    }
}

object NoIndication : Indication {
    private object NoIndicationInstance : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return NoIndicationInstance
    }
}
