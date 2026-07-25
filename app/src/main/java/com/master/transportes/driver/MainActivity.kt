package com.master.transportes.driver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.master.transportes.driver.navigation.AppNavigation
import com.master.transportes.driver.core.session.SessionManager
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.BLACK
            )
        )

        setContent {
            MasterTransportesMobileDriverTheme {
                AppNavigation(sessionManager = sessionManager)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MasterTransportesMobileDriverTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { it
            Text("Preview")
        }
    }
}