package com.master.transportes.driver.feature.activity.presentation.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme

@Composable
fun ActivityContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Activity")
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityPreview() {
    MasterTransportesMobileDriverTheme {
        ActivityContent()
    }
}
