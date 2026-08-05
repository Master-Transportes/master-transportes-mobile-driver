package com.master.transportes.driver.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnlineActionButton(
    isOnline: Boolean,
    onGoOnline: () -> Unit,
    onGoOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isOnline) Color(0xFFFF3B30) else Color(0xFF0A84FF),
        shape = CircleShape,
        modifier = modifier
            .size(60.dp)
            .clickable { if (isOnline) onGoOffline() else onGoOnline() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (isOnline) "FINALIZAR" else "INICIAR",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
