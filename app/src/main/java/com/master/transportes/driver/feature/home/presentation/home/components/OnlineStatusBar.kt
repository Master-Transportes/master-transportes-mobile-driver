package com.master.transportes.driver.feature.home.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun OnlineStatusBar(
    isOnline: Boolean,
    onGoOnline: () -> Unit,
    onGoOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .navigationBarsPadding()
    ) {
        Box {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = "Ajustes",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            )

            Text(
                text = if (isOnline) "Você está online" else "Você está offline",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .clickable { if (isOnline) menuExpanded = true }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ViewList,
                    contentDescription = "Lista",
                    tint = Color.Black
                )
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    if (isOnline) {
                        DropdownMenuItem(
                            text = { Text("Finalizar") },
                            onClick = {
                                menuExpanded = false
                                onGoOffline()
                            }
                        )
                    }
                }
            }
        }
    }
}
