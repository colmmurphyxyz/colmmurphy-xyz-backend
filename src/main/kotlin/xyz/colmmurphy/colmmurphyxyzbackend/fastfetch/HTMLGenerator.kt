package xyz.colmmurphy.colmmurphyxyzbackend.fastfetch

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.span
import xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi.AnsiColor
import xyz.colmmurphy.colmmurphyxyzbackend.fastfetch.ansi.TextSegment

private fun classListFor(segment: TextSegment): List<String> {
    val classes = mutableListOf<String>()
    if (segment.dim) classes.add("ff-dim")
    if (segment.bold) classes.add("ff-bold")
    if (segment.italic) classes.add("ff-italic")
    if (segment.underline) classes.add("ff-underline")
    if (segment.strikethrough) classes.add("ff-strikethrough")
    if (segment.invisible) classes.add("ff-invisible")
    if (segment.blinking) classes.add("ff-blinking")
    if (segment.inverse) classes.add("ff-inverse")

    if (segment.fgColor != AnsiColor.DEFAULT) {
        classes.add("ff-fg-${segment.fgColor.toString().lowercase()}")
    }
    if (segment.bgColor != AnsiColor.DEFAULT) {
        classes.add("ff-bg-${segment.fgColor.toString().lowercase()}")
    }

    return classes
}

fun textSegmentToHtml(segment: TextSegment): FlowContent.() -> Unit {
    return {
        span {
            attributes["class"] = classListFor(segment).joinToString(" ")
            +segment.text
        }
    }
}

fun textSegmentsToHtml(segments: List<TextSegment>): FlowContent.() -> Unit {
    return {
        segments.forEach {
            textSegmentToHtml(it)()
        }
    }
}