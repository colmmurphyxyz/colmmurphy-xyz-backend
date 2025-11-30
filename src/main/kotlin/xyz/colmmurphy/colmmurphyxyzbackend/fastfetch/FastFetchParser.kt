package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.html.FlowContent
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.p
import kotlinx.html.pre
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe

import org.slf4j.LoggerFactory

class FastFetchParser : IFastFetchParser {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun parseFastFetch(input: String): String {
        val lines = input.lines()
        val separatorIndex = lines.indexOfFirst { it.contains("--------") }

        val logoRaw = lines.take(separatorIndex).joinToString("\n")
        val infoRaw = lines.drop(separatorIndex + 1).joinToString("\n")

        return createHTML(prettyPrint = true).div {
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
        else -> "inherit"
    }

    private fun parseLogoLine(line: String): FlowContent.() -> Unit {
        var parsedLine = ""
        var color = "inherit"
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
                parsedLine += "<i style=\"color: $color;\">"
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
                unsafe { raw(parsedLine)}
                br
            }
        }
    }

    private fun parseLogo(logoRaw: String): FlowContent.() -> Unit {
        val lines = mutableListOf<String>()
        var color = "inherit"
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
                            parseLogoLine(line)()
                        }
                }
            }
        }
    }

    private fun parseInfo(infoRaw: String): FlowContent.() -> Unit {
        return {
            div {
                p { +"INFO" }
            }
        }
    }
}