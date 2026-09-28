package com.krushiedge.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * KrushiEdge AI Design System — Color Palette
 *
 * Inspired by agricultural landscapes:
 * - Earth tones for grounding and trust
 * - Greens for vegetation and health
 * - Warm neutrals for readability
 *
 * Color communicates meaning:
 *   Green  = Normal / Healthy
 *   Yellow = Monitor / Attention needed
 *   Orange = Inspect / Closer look required
 *   Red    = Urgent / Immediate action needed
 *   Gray   = Insufficient data
 */
object KrushiColors {

    // ── Primary ──────────────────────────────────────────
    // Deep agricultural green — trust, growth, reliability
    val Primary = Color(0xFF2D6A4F)
    val PrimaryDark = Color(0xFF1B4332)
    val PrimaryLight = Color(0xFF52B788)
    val PrimaryContainer = Color(0xFFD8F3DC)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFF1B4332)

    // ── Secondary ────────────────────────────────────────
    // Warm earth tone — soil, dependability
    val Secondary = Color(0xFF8B6914)
    val SecondaryDark = Color(0xFF5C4A0E)
    val SecondaryLight = Color(0xFFD4A937)
    val SecondaryContainer = Color(0xFFFFF3D6)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFF5C4A0E)

    // ── Surface & Background ─────────────────────────────
    val Background = Color(0xFFFAF8F5)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF5F0EB)
    val SurfaceContainer = Color(0xFFF0EBE5)
    val SurfaceContainerHigh = Color(0xFFE8E2DA)
    val OnBackground = Color(0xFF1A1C18)
    val OnSurface = Color(0xFF1A1C18)
    val OnSurfaceVariant = Color(0xFF49454F)

    // ── Risk Semantic Colors ─────────────────────────────
    // These must always pair with Icon + Text (never color alone)
    val RiskNormal = Color(0xFF2D6A4F)
    val RiskNormalBg = Color(0xFFD8F3DC)
    val RiskMonitor = Color(0xFFC49000)
    val RiskMonitorBg = Color(0xFFFFF3D6)
    val RiskInspect = Color(0xFFD47518)
    val RiskInspectBg = Color(0xFFFFE8D6)
    val RiskUrgent = Color(0xFFC62828)
    val RiskUrgentBg = Color(0xFFFFEBEE)
    val RiskInsufficient = Color(0xFF757575)
    val RiskInsufficientBg = Color(0xFFF5F5F5)

    // ── Accent / Functional ──────────────────────────────
    val Water = Color(0xFF1565C0)
    val WaterLight = Color(0xFFE3F2FD)
    val Sun = Color(0xFFF9A825)
    val SunLight = Color(0xFFFFF8E1)
    val Soil = Color(0xFF795548)
    val SoilLight = Color(0xFFEFEBE9)

    // ── Text ─────────────────────────────────────────────
    val TextPrimary = Color(0xFF1A1C18)
    val TextSecondary = Color(0xFF5F6368)
    val TextTertiary = Color(0xFF9AA0A6)
    val TextOnDark = Color(0xFFFFFFFF)
    val TextLink = Color(0xFF2D6A4F)

    // ── Borders & Dividers ───────────────────────────────
    val Border = Color(0xFFDAD4CB)
    val BorderLight = Color(0xFFE8E2DA)
    val Divider = Color(0xFFEBE6DF)

    // ── Overlay ──────────────────────────────────────────
    val Scrim = Color(0x52000000)

    // ── Dark Theme variants ──────────────────────────────
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkSurfaceVariant = Color(0xFF2C2C2C)
    val DarkOnBackground = Color(0xFFE8E2DA)
    val DarkOnSurface = Color(0xFFE8E2DA)
    val DarkPrimary = Color(0xFF52B788)
    val DarkPrimaryContainer = Color(0xFF1B4332)
}

// Top-level aliases for convenient Compose consumption
val KrushiGreenPrimary = KrushiColors.Primary
val KrushiGreenDark = KrushiColors.PrimaryDark
val KrushiGreenLight = KrushiColors.PrimaryLight
val RiskNormal = KrushiColors.RiskNormal
val RiskMonitor = KrushiColors.RiskMonitor
val RiskInspect = KrushiColors.RiskInspect
val RiskUrgent = KrushiColors.RiskUrgent
