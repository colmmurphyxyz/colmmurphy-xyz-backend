package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi

data class TextSegment(
    val text: String,
    val fgColor: AnsiColor,
    val bgColor: AnsiColor,
    val dim: Boolean,
    val bold: Boolean,
    val italic: Boolean,
    val underline: Boolean,
    val blinking: Boolean,
    val inverse: Boolean,
    val strikethrough: Boolean,
    val invisible: Boolean
)