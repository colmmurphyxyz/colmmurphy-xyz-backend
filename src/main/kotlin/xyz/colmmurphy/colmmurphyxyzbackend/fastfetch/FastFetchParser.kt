package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.html.FlowContent
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.p
import kotlinx.html.pre
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.unsafe

import org.slf4j.LoggerFactory
import kotlin.text.digitToInt
import kotlin.text.isDigit

class FastFetchParser : IFastFetchParser {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun parseFastFetch(input: String): String {
        val (logoRaw, infoRaw) = input.split("\u001B[?7l")

        return createHTML(prettyPrint = true).div {
            style {
                +"""
                .ff-bold { font-weight: bold; }
                .ff-italic { font-style: italic; }
                .ff-underline { text-decoration: underline; }
                .ff-strikethrough { text-decoration: line-through; }
                
                .ff-fg-black { color: #241f31; }
                .ff-fg-red { color: #c01c28; }
                .ff-fg-green { color: #2ec273; }
                .ff-fg-yellow { color: #f5c211; }
                .ff-fg-blue { color: #1e78e4; }
                .ff-fg-magenta { color: 9841bb; }
                .ff-fg-cyan { color: #0ab9dc; }
                .ff-fg-white { color: #c0bfbc; }
                
                .ff-bg-black { background-color: #241f31; }
                .ff-bg-red { background-color: #c01c28; }
                .ff-bg-green { background-color: #2ec273; }
                .ff-bg-yellow { background-color: #f5c211; }
                .ff-bg-blue { background-color: #1e78e4; }
                .ff-bg-magenta { background-color: 9841bb; }
                .ff-bg-cyan { background-color: #0ab9dc; }
                .ff-bg-white { background-color: #c0bfbc; }
                
                .ff-bg-bright-black { background-color: #5e5c64; }
                .ff-bg-bright-red { background-color: #ed333b; }
                .ff-bg-bright-green { background-color: #57e389; }
                .ff-bg-bright-yellow { background-color: #f8e45c; }
                .ff-bg-bright-blue { background-color: #51a1ff; }
                .ff-bg-bright-magenta { background-color: #c061cb; }
                .ff-bg-bright-cyan { background-color: #4fd2fd; }
                .ff-bg-bright-white { background-color: #f6f5f4; }
                """.trimIndent()
            }
            parseLogo(logoRaw)()
            parseInfo(infoRaw)()
        }
    }

    private fun colorFromAnsiCode(code: Int): String = when (code) {
        30 -> "black"
        31 -> "red"
        32 -> "green"
        33 -> "yellow"
        34 -> "blue"
        35 -> "magenta"
        36 -> "cyan"
        37 -> "white"
        else -> "nocolor"
    }

    private fun parseLine(line: String): FlowContent.() -> Unit {
        var parsedLine = ""
        var color: String
        var iNesting = 0
        var i = 0
        fun parseColor(): String {
            var num = 0
            while (line[i].isDigit()) {
                num *= 10
                num += line[i].digitToInt()
                i++
            }
            // consume 'm'
            i++
            return colorFromAnsiCode(num)
        }
        while (i < line.length) {
            val curr = line[i]
            if (curr == '\u001B') {
                i += 2
                color = parseColor()
                if (iNesting > 0) {
                    parsedLine += "</i>"
                    iNesting -= 1
                }
                iNesting += 1
                parsedLine += "<i style=\"color: $color;\" class=\"fastfetch-${color}\">"
            } else {
                parsedLine += curr
                i++
            }
        }
        while (iNesting > 0) {
            parsedLine += "</i>"
            iNesting -= 1
        }

        return {
            span {
                unsafe { raw(parsedLine) }
                br
            }
        }
    }

    private fun parseLogo(logoRaw: String): FlowContent.() -> Unit {
        val lines = mutableListOf<String>()
        var color: String
        var parsedLine = ""
        for (line in logoRaw.lines()) {
            var iNesting = 0
            var i = 0
            fun parseColor(): String {
                var num = 0
                while (line[i].isDigit()) {
                    num *= 10
                    num += line[i].digitToInt()
                    i++
                }
                // consume 'm'
                i++
                return colorFromAnsiCode(num)
            }
            while (i < line.length) {
                val curr = line[i]
                if (curr == '\u001B') {
                    i += 2
                    color = parseColor()
                    if (iNesting > 0) {
                        parsedLine += "</i>"
                        iNesting -= 1
                    }
                    iNesting += 1
                    parsedLine += "<i style=\"color=$color\">"
                } else {
                    parsedLine += curr
                    i++
                }
            }
            while (iNesting > 0) {
                parsedLine += "</i>"
                iNesting -= 1
            }
            lines.add(parsedLine)
            parsedLine = ""
        }
        return {
            div {
                p { +"LOGO" }
                pre {
                    attributes["style"] = "white-space: pre;"
                    logoRaw
                        .lines()
                        .forEach { line ->
                            parseLine(line)()
                        }
                }
            }
        }
    }

    private fun spanWithClass(className: String): String = "<span class=\"${className}\">"

    private fun parseInfoText(info: String): String {
        var html = ""
        var i = 0
        var nesting = 0
        while (i < info.length && info[i] != 0.toChar()) {
            val char = info[i]
            if (char == '\n') {
                i++
                continue
            } else if (char == '\u001B') {
                i += 2
                if (info[i] == 'm') {
                    html += "</span>"
                    i++
                    continue
                }
                if (info[i] == '?') {
                    i += 3
                    continue
                }
                var num = 0
                while (info[i].isDigit()) {
                    num *= 10
                    num += info[i].digitToInt()
                    i++
                }

                // special case: ignore cursor movement sequences
                if (info[i] == 'C') {
                    i++
                    continue
                }

                when (num) {
                    // reset all modes
                    0 -> {
                        println("Span close")
                        repeat(nesting) {
                            html += "</span>"
                        }
                        nesting = 0
                    }
                    // bold
                    1 -> {
                        html += spanWithClass("ff-bold")
                    }
                    // dim/faint
                    2 -> html += spanWithClass("ff-faint")
                    // italic
                    3 -> html += spanWithClass("ff-italic")
                    // underline
                    4 -> html += spanWithClass("ff-underline")
                    // blinking
                    5 -> html += spanWithClass("ff-blinking")
                    // strikethrough
                    9 -> html += spanWithClass("ff-strikethrough")
                    // foreground colours
                    30 -> html += spanWithClass("ff-fg-black")
                    31 -> html += spanWithClass("ff-fg-red")
                    32 -> html += spanWithClass("ff-fg-green")
                    33 -> html += spanWithClass("ff-fg-yellow")
                    34 -> html += spanWithClass("ff-fg-blue")
                    35 -> html += spanWithClass("ff-fg-magenta")
                    36 -> html += spanWithClass("ff-fg-cyan")
                    37 -> html += spanWithClass("ff-fg-white")
                    // fg colour reset
                    39 -> html += "</span>"
                    // background colours
                    40 -> html += spanWithClass("ff-bg-black")
                    41 -> html += spanWithClass("ff-bg-red")
                    42 -> html += spanWithClass("ff-bg-green")
                    43 -> html += spanWithClass("ff-bg-yellow")
                    44 -> html += spanWithClass("ff-bg-blue")
                    45 -> html += spanWithClass("ff-bg-magenta")
                    46 -> html += spanWithClass("ff-bg-cyan")
                    47 -> html += spanWithClass("ff-bg-white")
                    // bg colour reset
                    49 -> html += "</span>"
                    // bright foreground colours
                    90 -> html += spanWithClass("ff-bg-bright-black")
                    91 -> html += spanWithClass("ff-bg-bright-red")
                    92 -> html += spanWithClass("ff-bg-bright-green")
                    93 -> html += spanWithClass("ff-bg-bright-yellow")
                    94 -> html += spanWithClass("ff-bg-bright-blue")
                    95 -> html += spanWithClass("ff-bg-bright-magenta")
                    96 -> html += spanWithClass("ff-bg-bright-cyan")
                    97 -> html += spanWithClass("ff-bg-bright-white")
                    // bright background colours
                    100 -> html += spanWithClass("ff-bg-bright-black")
                    101 -> html += spanWithClass("ff-bg-bright-red")
                    102 -> html += spanWithClass("ff-bg-bright-green")
                    103 -> html += spanWithClass("ff-bg-bright-yellow")
                    104 -> html += spanWithClass("ff-bg-bright-blue")
                    105 -> html += spanWithClass("ff-bg-bright-magenta")
                    106 -> html += spanWithClass("ff-bg-bright-cyan")
                    107 -> html += spanWithClass("ff-bg-bright-white")
                    // resets
                    22, 23, 24, 25, 27, 28, 29 -> html += "</span>"
                    else -> {}
                }
                i++
                continue
            } else {
                html += char
                i++
                continue
            }
        }

        return html
    }

    private fun parseInfo(infoRaw: String): FlowContent.() -> Unit {
        val info = parseInfoText(infoRaw)
        return {
            div {
                p { +"INFO" }
                pre {
                    attributes["style"] = "white-space: pre;"
                    unsafe {
                        +info
                    }
                }
            }
        }
    }
}