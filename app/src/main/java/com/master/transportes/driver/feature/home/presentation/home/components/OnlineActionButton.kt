package com.master.transportes.driver.feature.home.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun OnlineActionButton(
    isOnline: Boolean,
    onGoOnline: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = colors.primary
    val contentColor = colors.onPrimary

    if(!isOnline){
        Surface(
            color = containerColor.copy(alpha = if (enabled) 1f else 0.5f),
            shape = CircleShape,
            modifier = modifier
                .size(60.dp)
                .clickable(enabled = enabled) {
                   onGoOnline()
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "INICIAR",
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
