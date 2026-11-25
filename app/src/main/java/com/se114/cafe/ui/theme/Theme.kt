package com.se114.cafe.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class CafeRole { Staff, Customer, Admin }

private fun palette(primary: Long, selected: Long, background: Long, ink: Long, muted: Long, line: Long, tint: Long) =
    lightColorScheme(
        primary = Color(primary), secondary = Color(selected), background = Color(background),
        surface = Color.White, onSurface = Color(ink), onBackground = Color(ink),
        onSurfaceVariant = Color(muted), outline = Color(line), outlineVariant = Color(line),
        surfaceVariant = Color(tint), secondaryContainer = Color(tint), onSecondaryContainer = Color(ink),
        onPrimary = Color.White, onSecondary = Color.White, error = Color(0xFF8F2C22),
        errorContainer = Color(0xFFF3DAD5)
    )

private val staff = palette(0xFF4A2E1F, 0xFF7A4B2F, 0xFFF1EAE3, 0xFF2B1B14, 0xFF6F5A4C, 0xFFD8C9BB, 0xFFE6D6C6)
private val customer = palette(0xFF8A5232, 0xFF6B3E24, 0xFFFBF5EE, 0xFF3B2A20, 0xFF7A6657, 0xFFEADBC8, 0xFFF0E0CC)

private val admin = palette(0xFF1A1A1A, 0xFF8B5E3C, 0xFFFFFFFF, 0xFF1A1A1A, 0xFF6B635B, 0xFFE4DDD5, 0xFFEFE6DC)
    .copy(surfaceContainer = Color(0xFFF6F3EF))

@Composable
fun CafeTheme(role: CafeRole = CafeRole.Staff, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = when (role) {
            CafeRole.Staff -> staff
            CafeRole.Customer -> customer
            CafeRole.Admin -> admin
        },
        typography = Typography,
        shapes = Shapes(small = RoundedCornerShape(8.dp), medium = RoundedCornerShape(12.dp), large = RoundedCornerShape(14.dp)),
        content = content
    )
}
