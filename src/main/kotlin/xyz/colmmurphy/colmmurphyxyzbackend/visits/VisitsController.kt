package xyz.colmmurphy.colmmurphyxyzbackend.visits

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.beans.factory.annotation.Autowired

@CrossOrigin(origins = ["*"])
@RestController
class VisitsController(
    @Autowired private val store: VisitsStore
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/api/visits")
    fun addVisit(
        request: HttpServletRequest,
        @RequestHeader(value = "User-Agent") userAgent: String?,
        @RequestHeader(value = "X-Forwarded-For") forwardedFor: String?,
        @RequestBody data: VisitDto
    ): ResponseEntity<Unit> {
        val timestamp = System.currentTimeMillis()
        val ip = forwardedFor ?: request.remoteAddr

        val url = data.url
        val fingerprint = data.fingerprint

        store.saveVisit(Visit(ip, userAgent ?: "N/A", url, fingerprint, timestamp))
        return ResponseEntity.ok().build()
    }
}