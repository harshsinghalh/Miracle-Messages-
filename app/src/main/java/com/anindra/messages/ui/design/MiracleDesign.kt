package com.anindra.messages.ui.design

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Miracle's visual primitives.
 *
 * The system deliberately uses translucency as a depth cue rather than putting
 * every piece of content in a card. This keeps long message histories quiet and
 * makes interactive surfaces feel tactile without obscuring SMS content.
 */
object MiracleTokens {
    val PageHorizontal = 20.dp
    val CompactGap = 8.dp
    val Gap = 14.dp
    val SectionGap = 24.dp
    val SmallRadius = 16.dp
    val Radius = 24.dp
    val LargeRadius = 32.dp
    val Hairline = 0.75.dp
}

@Composable
fun Modifier.miracleGlass(
    shape: RoundedCornerShape = RoundedCornerShape(MiracleTokens.Radius),
    alpha: Float = 0.72f,
    borderAlpha: Float = 0.14f
): Modifier {
    val colors = MaterialTheme.colorScheme
    return clip(shape)
        .background(colors.surfaceContainerHigh.copy(alpha = alpha))
        .border(
            MiracleTokens.Hairline,
            colors.outlineVariant.copy(alpha = borderAlpha),
            shape
        )
}

/**
 * Compact tactile icon control used by headers and composers.
 */
@Composable
fun MiracleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceContainerHigh.copy(alpha = 0.62f))
            .border(
                MiracleTokens.Hairline,
                colors.outlineVariant.copy(alpha = 0.12f),
                RoundedCornerShape(14.dp)
            )
    ) {
        Icon(icon, contentDescription)
    }
}

/**
 * A restrained, full-width action surface. It is intentionally not a Material
 * button so primary messaging actions can keep their own visual identity.
 */
@Composable
fun MiracleActionSurface(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .clip(RoundedCornerShape(MiracleTokens.Radius))
            .background(
                if (enabled) colors.primaryContainer
                else colors.surfaceContainerHighest
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interaction,
                indication = ripple()
            ) { onClick() }
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) colors.onPrimaryContainer else colors.onSurfaceVariant
            )
            Box(Modifier.size(12.dp))
        }
        Text(
            text = label,
            color = if (enabled) colors.onPrimaryContainer else colors.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

/** A soft divider alternative for lists where a hard line would add noise. */
@Composable
fun MiracleListInset(modifier: Modifier = Modifier, height: Dp = 1.dp) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(start = 76.dp)
            .alpha(0.55f)
            .background(MaterialTheme.colorScheme.outlineVariant)
            .height(height)
    )
}
