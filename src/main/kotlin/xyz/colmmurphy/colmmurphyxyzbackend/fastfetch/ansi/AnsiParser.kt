package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi

import org.slf4j.LoggerFactory

const val ESCAPE = '\u001b'

/**
 * Helper class to parse ANSI text. Ignores most escape sequence not related to colour/formatting
 * e.g. cursor movement, line wrap
 */
class AnsiParser(private val text: String) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var i: Int = 0
    private var fgColor: AnsiColor = AnsiColor.DEFAULT
    private var bgColor: AnsiColor = AnsiColor.DEFAULT
    private var dim = false
    private var bold = false
    private var italic = false
    private var underline = false
    private var blinking = false
    private var inverse = false
    private var strikethrough = false
    private var invisible = false

    private fun mkTextSegment(contents: String): TextSegment {
        return TextSegment(
            contents,
            fgColor,
            bgColor,
            dim,
            bold,
            italic,
            underline,
            blinking,
            inverse,
            strikethrough,
            invisible
        )
    }

    private fun resetModifiers() {
        fgColor = AnsiColor.DEFAULT
        bgColor = AnsiColor.DEFAULT
        dim = false
        bold = false
        italic = false
        underline = false
        blinking = false
        inverse = false
        strikethrough = false
        invisible = false
    }

    private fun handleSequence(sequence: AnsiGraphic) {
        when (sequence) {
            // resets
            AnsiGraphic.RESET_ALL -> resetModifiers()
            AnsiGraphic.RESET_BOLD_AND_DIM -> {
                bold = false
                dim = false
            }

            AnsiGraphic.RESET_ITALIC -> italic = false
            AnsiGraphic.RESET_UNDERLINE -> underline = false
            AnsiGraphic.RESET_BLINKING -> blinking = false
            AnsiGraphic.RESET_INVERSE -> inverse = false
            AnsiGraphic.RESET_INVISIBLE -> invisible = false
            AnsiGraphic.RESET_STRIKETHROUGH -> strikethrough = false
            // text styling
            AnsiGraphic.BOLD -> bold = true
            AnsiGraphic.DIM -> dim = true
            AnsiGraphic.ITALIC -> italic = true
            AnsiGraphic.UNDERLINE -> underline = true
            AnsiGraphic.BLINKING -> blinking = true
            AnsiGraphic.INVERSE -> inverse = true
            AnsiGraphic.INVISIBLE -> invisible = !invisible
            AnsiGraphic.STRIKETHROUGH -> strikethrough = true
            // colours
            // foreground colours
            AnsiGraphic.BLACK -> fgColor = AnsiColor.BLACK
            AnsiGraphic.RED -> fgColor = AnsiColor.RED
            AnsiGraphic.GREEN -> fgColor = AnsiColor.GREEN
            AnsiGraphic.YELLOW -> fgColor = AnsiColor.YELLOW
            AnsiGraphic.BLUE -> fgColor = AnsiColor.BLUE
            AnsiGraphic.MAGENTA -> fgColor = AnsiColor.MAGENTA
            AnsiGraphic.CYAN -> fgColor = AnsiColor.CYAN
            AnsiGraphic.WHITE -> fgColor = AnsiColor.WHITE
            AnsiGraphic.DEFAULT -> fgColor = AnsiColor.DEFAULT
            // background colours
            AnsiGraphic.BLACK_BG -> bgColor = AnsiColor.BLACK
            AnsiGraphic.RED_BG -> bgColor = AnsiColor.RED
            AnsiGraphic.GREEN_BG -> bgColor = AnsiColor.GREEN
            AnsiGraphic.YELLOW_BG -> bgColor = AnsiColor.YELLOW
            AnsiGraphic.BLUE_BG -> bgColor = AnsiColor.BLUE
            AnsiGraphic.MAGENTA_BG -> bgColor = AnsiColor.MAGENTA
            AnsiGraphic.CYAN_BG -> bgColor = AnsiColor.CYAN
            AnsiGraphic.WHITE_BG -> bgColor = AnsiColor.WHITE
            AnsiGraphic.DEFAULT_BG -> bgColor = AnsiColor.DEFAULT
            // bright foreground colours
            AnsiGraphic.BRIGHT_BLACK -> fgColor = AnsiColor.BRIGHT_BLACK
            AnsiGraphic.BRIGHT_RED -> fgColor = AnsiColor.BRIGHT_RED
            AnsiGraphic.BRIGHT_GREEN -> fgColor = AnsiColor.BRIGHT_GREEN
            AnsiGraphic.BRIGHT_YELLOW -> fgColor = AnsiColor.BRIGHT_YELLOW
            AnsiGraphic.BRIGHT_BLUE -> fgColor = AnsiColor.BRIGHT_BLUE
            AnsiGraphic.BRIGHT_MAGENTA -> fgColor = AnsiColor.BRIGHT_MAGENTA
            AnsiGraphic.BRIGHT_CYAN -> fgColor = AnsiColor.BRIGHT_CYAN
            AnsiGraphic.BRIGHT_WHITE -> fgColor = AnsiColor.BRIGHT_WHITE
            // bright background colours
            AnsiGraphic.BRIGHT_BLACK_BG -> bgColor = AnsiColor.BRIGHT_BLACK
            AnsiGraphic.BRIGHT_RED_BG -> bgColor = AnsiColor.BRIGHT_RED
            AnsiGraphic.BRIGHT_GREEN_BG -> bgColor = AnsiColor.BRIGHT_GREEN
            AnsiGraphic.BRIGHT_YELLOW_BG -> bgColor = AnsiColor.BRIGHT_YELLOW
            AnsiGraphic.BRIGHT_BLUE_BG -> bgColor = AnsiColor.BRIGHT_BLUE
            AnsiGraphic.BRIGHT_MAGENTA_BG -> bgColor = AnsiColor.BRIGHT_MAGENTA
            AnsiGraphic.BRIGHT_CYAN_BG -> bgColor = AnsiColor.BRIGHT_CYAN
            AnsiGraphic.BRIGHT_WHITE_BG -> bgColor = AnsiColor.BRIGHT_WHITE
        }
    }

    private fun parseNumber(): Int {
        var num = 0
        while (text[i].isDigit()) {
            num *= 10
            num += text[i].digitToInt()
            i++
        }
        return num
    }

    fun parseAnsiText(): List<TextSegment> {
        i = 0
        resetModifiers()

        val segments = mutableListOf<TextSegment>()
        var currentSegmentText = ""

        while (i < text.length) {
            val char = text[i]
            if (char == ESCAPE) {
                if (currentSegmentText != "") {
                    segments.add(mkTextSegment(currentSegmentText))
                    currentSegmentText = ""
                }

                i += 2

                if (text[i] == 'm') {
                    resetModifiers()
                    i++
                    continue
                }
                if (text[i] == '?') {
                    // ignore
                    i += 3
                    continue
                }

                val num = parseNumber()

                if (text[i] == 'C') {
                    // ignore cursor control sequences
                    i++
                    continue
                }

                val sequence = AnsiGraphic.fromCode(num)
                if (sequence == null) {
                    log.info("Unrecognized ANSI escape sequence {}", num)
                } else {
                    handleSequence(sequence)
                }
            } else {
                currentSegmentText += char
            }
            i++
        }

        segments.add(mkTextSegment(currentSegmentText))
        return segments
    }
}