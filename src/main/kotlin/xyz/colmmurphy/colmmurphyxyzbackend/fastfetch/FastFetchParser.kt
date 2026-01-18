package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.html.div
import kotlinx.html.stream.createHTML
import kotlinx.html.style

import org.slf4j.LoggerFactory
import xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi.AnsiParser

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
            textSegmentsToHtml(AnsiParser(logoRaw).parseAnsiText())()
            textSegmentsToHtml(AnsiParser(infoRaw).parseAnsiText())()
        }
    }
}