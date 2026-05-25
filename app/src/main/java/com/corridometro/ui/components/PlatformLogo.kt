package com.corridometro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corridometro.domain.Platform
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.InDriverForeground
import com.corridometro.ui.theme.InDriverTeal
import com.corridometro.ui.theme.NinetyNineForeground
import com.corridometro.ui.theme.NinetyNineYellow
import com.corridometro.ui.theme.BoltForeground
import com.corridometro.ui.theme.BoltGreen
import com.corridometro.ui.theme.CabifyForeground
import com.corridometro.ui.theme.CabifyPurple
import com.corridometro.ui.theme.IFoodForeground
import com.corridometro.ui.theme.IFoodRed
import com.corridometro.ui.theme.LadyDriverForeground
import com.corridometro.ui.theme.LadyDriverPink
import com.corridometro.ui.theme.AmazonForeground
import com.corridometro.ui.theme.AmazonTeal
import com.corridometro.ui.theme.LoggiBlue
import com.corridometro.ui.theme.LoggiForeground
import com.corridometro.ui.theme.MercadoForeground
import com.corridometro.ui.theme.MercadoYellow
import com.corridometro.ui.theme.ShopeeForeground
import com.corridometro.ui.theme.ShopeeOrange
import com.corridometro.ui.theme.UberEatsForeground
import com.corridometro.ui.theme.UberEatsGreen
import com.corridometro.ui.theme.OutroForeground
import com.corridometro.ui.theme.OutroGray
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.RappiForeground
import com.corridometro.ui.theme.RappiOrange
import com.corridometro.ui.theme.TextPrimary
import com.corridometro.ui.theme.TextSecondary
import com.corridometro.ui.theme.UberBlack
import com.corridometro.ui.theme.UberForeground

@Composable
fun PlatformLogo(
    platform: Platform,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 44.dp,
) {
    val (bg, fg, text) = platformBrandStyle(platform)
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = fg,
            fontWeight = FontWeight.Bold,
            fontSize = when (platform) {
                Platform.UBER -> 11.sp
                Platform.NINETY_NINE -> 16.sp
                Platform.INDRIVER -> 9.sp
                Platform.CABIFY -> 9.sp
                Platform.BOLT -> 11.sp
                Platform.LADY_DRIVER -> 7.sp
                Platform.IFOOD -> 10.sp
                Platform.RAPPI -> 10.sp
                Platform.LOGGI -> 9.sp
                Platform.UBER_EATS -> 7.sp
                Platform.MERCADO_LIVRE -> 8.sp
                Platform.SHOPEE -> 9.sp
                Platform.AMAZON_FLEX -> 7.sp
                Platform.OUTRO -> 10.sp
            },
        )
    }
}

@Composable
fun PlatformLogoChip(
    platform: Platform?,
    selected: Boolean,
    onClick: () -> Unit,
    label: String? = null,
    showMoreBadge: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val displayLabel = label ?: platform?.label ?: "Todas"
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFFF0FDF4) else Color.White,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Primary else Border,
        ),
        shadowElevation = if (selected) 2.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                if (platform != null) {
                    PlatformLogo(platform = platform, size = 40.dp)
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE4E7EC)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (showMoreBadge) "+" else "∗",
                            fontSize = if (showMoreBadge) 22.sp else 18.sp,
                            color = if (showMoreBadge) Primary else TextSecondary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Text(
                text = displayLabel,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) Primary else TextPrimary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

private fun platformBrandStyle(platform: Platform): Triple<Color, Color, String> =
    when (platform) {
        Platform.UBER -> Triple(UberBlack, UberForeground, "Uber")
        Platform.NINETY_NINE -> Triple(NinetyNineYellow, NinetyNineForeground, "99")
        Platform.INDRIVER -> Triple(InDriverTeal, InDriverForeground, "inDrive")
        Platform.CABIFY -> Triple(CabifyPurple, CabifyForeground, "Cabify")
        Platform.BOLT -> Triple(BoltGreen, BoltForeground, "Bolt")
        Platform.LADY_DRIVER -> Triple(LadyDriverPink, LadyDriverForeground, "Lady")
        Platform.IFOOD -> Triple(IFoodRed, IFoodForeground, "iFood")
        Platform.RAPPI -> Triple(RappiOrange, RappiForeground, "Rappi")
        Platform.LOGGI -> Triple(LoggiBlue, LoggiForeground, "Loggi")
        Platform.UBER_EATS -> Triple(UberEatsGreen, UberEatsForeground, "Eats")
        Platform.MERCADO_LIVRE -> Triple(MercadoYellow, MercadoForeground, "MELI")
        Platform.SHOPEE -> Triple(ShopeeOrange, ShopeeForeground, "Shop")
        Platform.AMAZON_FLEX -> Triple(AmazonTeal, AmazonForeground, "Amz")
        Platform.OUTRO -> Triple(OutroGray, OutroForeground, "···")
    }
