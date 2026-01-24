package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi

enum class AnsiGraphic(val code: Int) {
    RESET_ALL(0),
    BOLD(1),
    RESET_BOLD_AND_DIM(22),
    DIM(2),
    ITALIC(3),
    RESET_ITALIC(23),
    UNDERLINE(4),
    RESET_UNDERLINE(24),
    BLINKING(5),
    RESET_BLINKING(25),
    INVERSE(7),
    RESET_INVERSE(27),
    INVISIBLE(8),
    RESET_INVISIBLE(28),
    STRIKETHROUGH(9),
    RESET_STRIKETHROUGH(29),
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    WHITE(37),
    DEFAULT(39),
    BRIGHT_BLACK(90),
    BRIGHT_RED(91),
    BRIGHT_GREEN(92),
    BRIGHT_YELLOW(93),
    BRIGHT_BLUE(94),
    BRIGHT_MAGENTA(95),
    BRIGHT_CYAN(96),
    BRIGHT_WHITE(97),
    BLACK_BG(40),
    RED_BG(41),
    GREEN_BG(42),
    YELLOW_BG(43),
    BLUE_BG(44),
    MAGENTA_BG(45),
    CYAN_BG(46),
    WHITE_BG(47),
    DEFAULT_BG(49),
    BRIGHT_BLACK_BG(100),
    BRIGHT_RED_BG(101),
    BRIGHT_GREEN_BG(102),
    BRIGHT_YELLOW_BG(103),
    BRIGHT_BLUE_BG(104),
    BRIGHT_MAGENTA_BG(105),
    BRIGHT_CYAN_BG(106),
    BRIGHT_WHITE_BG(107);

    companion object {
        fun fromCode(ansiCode: Int): AnsiGraphic? {
            for(entry in AnsiGraphic.entries) {
                if (ansiCode == entry.code) {
                    return entry
                }
            }
            return null
        }
    }
}