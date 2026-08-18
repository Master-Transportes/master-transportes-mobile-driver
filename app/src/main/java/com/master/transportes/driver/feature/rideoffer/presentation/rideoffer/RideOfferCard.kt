package com.master.transportes.driver.feature.rideoffer.presentation.rideoffer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.master.transportes.driver.feature.rideoffer.domain.model.RideOffer
import com.master.transportes.driver.feature.rideoffer.domain.model.RidePoint
import com.master.transportes.driver.ui.theme.MasterTransportesMobileDriverTheme
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.delay

@Composable
fun RideOfferCard(
    offer: RideOffer,
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remainingSeconds = rememberRemainingSeconds(offer.offerExpiresAt)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color(0xFF1C1C1E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Master Transportes",
                    color = Color(0xFF1C1C1E),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Fechar oferta",
                        tint = Color(0xFF3A3A3C)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFECECEE), thickness = 1.dp)
            Spacer(Modifier.height(8.dp))

            RideStopRow(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                },
                label = "EMBARQUE",
                address = offer.origin.name,
                showConnector = true
            )

            RideStopRow(
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = "DESTINO",
                address = offer.destination.name
            )

            if (remainingSeconds > 0) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Expira em ${remainingSeconds}s",
                    color = Color(0xFF6E6E73),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "ACEITAR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RideStopRow(
    icon: @Composable () -> Unit,
    label: String,
    address: String,
    showConnector: Boolean = false
) {
    Row {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            icon()
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
                        .background(Color(0xFFC7C7CC))
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color(0xFF6E6E73),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = address,
                color = Color(0xFF1C1C1E),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun rememberRemainingSeconds(offerExpiresAt: String): Int {
    val expiresAtMillis = remember(offerExpiresAt) { parseOfferExpiration(offerExpiresAt) }
    var remainingSeconds by remember(expiresAtMillis) {
        mutableIntStateOf(((expiresAtMillis - System.currentTimeMillis()) / 1000L).toInt().coerceAtLeast(0))
    }
    LaunchedEffect(expiresAtMillis) {
        while (remainingSeconds > 0) {
            delay(1_000)
            remainingSeconds = ((expiresAtMillis - System.currentTimeMillis()) / 1000L).toInt().coerceAtLeast(0)
        }
    }
    return remainingSeconds
}

private val offerIsoFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun parseOfferExpiration(offerExpiresAt: String): Long =
    runCatching { offerIsoFormat.parse(offerExpiresAt)?.time ?: 0L }.getOrDefault(0L)

internal fun offerExpirationIso(millis: Long): String =
    offerIsoFormat.format(millis)

// ========== PREVIEWS ==========

private val sampleOffer: RideOffer = RideOffer(
    offerId = "offer_1",
    rideId = "ride_1",
    origin = RidePoint(
        name = "Av. Doutor Teixeira de Barros, Vila Boa Vista",
        lat = -23.5505,
        lng = -46.6333
    ),
    destination = RidePoint(
        name = "Rua Exemplo, 456 - Centro",
        lat = -23.6100,
        lng = -46.6900
    ),
    offerExpiresAt = offerExpirationIso(System.currentTimeMillis() + 20_000),
    timestamp = offerExpirationIso(System.currentTimeMillis())
)

@Preview(showBackground = true, name = "Oferta de corrida")
@Composable
fun RideOfferCardPreview() {
    MasterTransportesMobileDriverTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RideOfferCard(
                offer = sampleOffer,
                onAccept = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Oferta de corrida – endereços longos")
@Composable
fun RideOfferCardLongAddressesPreview() {
    MasterTransportesMobileDriverTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RideOfferCard(
                offer = sampleOffer.copy(
                    origin = RidePoint(
                        name = "Avenida Paulista, 1578 - Bela Vista, São Paulo - SP, 01310-200",
                        lat = -23.5614,
                        lng = -46.6559
                    ),
                    destination = RidePoint(
                        name = "Rua Oscar Freire, 1510 - Pinheiros, São Paulo - SP, 05409-012",
                        lat = -23.5658,
                        lng = -46.6841
                    )
                ),
                onAccept = {},
                onDismiss = {}
            )
        }
    }
}