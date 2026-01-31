package xyz.colmmurphy.colmmurphyxyzbackend.ntfy

interface AdminNotificationService {
    fun sendNotification(body: String): Result<Unit>
}
