package com.sajith.scrolllock

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF171717)
private val Muted = Color(0xFF8B8B90)
private val Accent = Color(0xFF6877E8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var dark by remember { mutableStateOf(false) }
            ScrollLockTheme(dark) {
                ScrollLockHome(
                    dark = dark,
                    onTheme = { dark = !dark },
                    onAccessibility = { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
                )
            }
        }
    }
}

@Composable
private fun ScrollLockTheme(dark: Boolean, content: @Composable () -> Unit) {
    val scheme = if (dark) darkColorScheme(
        background = Color(0xFF101010), surface = Color(0xFF1B1B1D),
        primary = Color(0xFFAAB2FF), onBackground = Color(0xFFF4F4F5),
        onSurface = Color(0xFFF4F4F5)
    ) else lightColorScheme(
        background = Color(0xFFF7F7F8), surface = Color.White,
        primary = Accent, onBackground = Ink, onSurface = Ink
    )
    MaterialTheme(colorScheme = scheme, content = content)
}

@Composable
private fun ScrollLockHome(dark: Boolean, onTheme: () -> Unit, onAccessibility: () -> Unit) {
    var minutes by remember { mutableIntStateOf(25) }
    var sessionStarted by remember { mutableStateOf(false) }
    var instagram by remember { mutableStateOf(true) }
    var youtube by remember { mutableStateOf(false) }
    var snapchat by remember { mutableStateOf(false) }

    val bg = MaterialTheme.colorScheme.background
    val card = MaterialTheme.colorScheme.surface
    val fg = MaterialTheme.colorScheme.onBackground

    Column(
        Modifier.fillMaxSize().background(bg).padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("SCROLLLOCK", fontSize = 12.sp, letterSpacing = 2.5.sp, color = Muted)
                Spacer(Modifier.height(5.dp))
                Text("Make room\nfor real life.", fontSize = 32.sp, lineHeight = 37.sp, color = fg)
            }
            IconButton(onClick = onTheme) {
                Icon(if (dark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode, "Toggle appearance", tint = fg)
            }
        }

        Surface(color = card, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(72.dp).background(if (sessionStarted) Accent.copy(.14f) else bg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Lock, null, tint = if (sessionStarted) Accent else fg, modifier = Modifier.size(30.dp))
                }
                Spacer(Modifier.height(14.dp))
                Text(if (sessionStarted) "Focus is on" else "Ready when you are", fontSize = 20.sp, color = fg)
                Text("A little less scrolling. A little more living.", fontSize = 13.sp, color = Muted)
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 25, 45, 60).forEach { value ->
                        FilterChip(selected = minutes == value, onClick = { minutes = value },
                            label = { Text("${value}m") }, shape = RoundedCornerShape(50))
                    }
                }
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = { sessionStarted = !sessionStarted },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(if (sessionStarted) Icons.Rounded.Stop else Icons.Rounded.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (sessionStarted) "End focus session" else "Start focus session")
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("YOUR DISTRACTIONS", fontSize = 11.sp, letterSpacing = 1.8.sp, color = Muted, modifier = Modifier.weight(1f))
            Text("Choose apps", fontSize = 12.sp, color = Accent)
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AppChoice("Instagram", "instagram", instagram, { instagram = it }, card, fg)
            AppChoice("YouTube", "play", youtube, { youtube = it }, card, fg)
            AppChoice("Snapchat", "snap", snapchat, { snapchat = it }, card, fg)
        }

        Surface(color = card, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.VerifiedUser, null, tint = Accent)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Accessibility permission", color = fg, fontSize = 14.sp)
                    Text("Needed to detect selected apps", color = Muted, fontSize = 12.sp)
                }
                TextButton(onClick = onAccessibility) { Text("Enable") }
            }
        }
        Spacer(Modifier.weight(1f))
        Text("Designed for mindful moments  ·  v1.0", color = Muted, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun AppChoice(name: String, glyph: String, selected: Boolean, onSelected: (Boolean) -> Unit, card: Color, fg: Color) {
    Surface(color = card, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(when(glyph) {
                    "instagram" -> Icons.Rounded.CameraAlt
                    "play" -> Icons.Rounded.PlayCircle
                    else -> Icons.Rounded.Forum
                }, null, tint = fg)
            }
            Spacer(Modifier.width(12.dp))
            Text(name, color = fg, modifier = Modifier.weight(1f), fontSize = 15.sp)
            Switch(checked = selected, onCheckedChange = onSelected)
        }
    }
}
