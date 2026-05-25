package com.corridometro.ui.mockup.appa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/** Área tocável mínima 48dp + ripple Material (APK A). */
fun Modifier.appATouch(
    onClick: () -> Unit,
    enabled: Boolean = true,
    label: String? = null,
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    clickable(
        interactionSource = interaction,
        indication = ripple(bounded = true),
        enabled = enabled,
        role = Role.Button,
        onClickLabel = label,
        onClick = onClick,
    )
        .defaultMinSize(minHeight = 48.dp)
}

fun Modifier.appAAnimateSize(): Modifier = animateContentSize(
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
)

@Composable
fun AppAExpandable(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        content()
    }
}
