package com.nemogz.mantracounter.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
//import coil3.compose.I
//import org.jetbrains.compose.resources.DrawableResource

@Composable
fun SvgImage(
    resource: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    val colorFilter = tint?.let { ColorFilter.tint(it) }

    AsyncImage(
        model = resource,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter
    )
}