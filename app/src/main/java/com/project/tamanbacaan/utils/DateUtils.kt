package com.caffeinatedr4t.tamanbacaan.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatIsoToLocal(isoDate: String?): String {
        if (isoDate.isNullOrEmpty()) return "-"

        return try {
            val normalized = isoDate
                .replace("Z", "+0000")
                .replace(Regex("([+-]\\d{2}):(\\d{2})"), "$1$2")

            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd'T'HH:mm:ssZ"
            )

            var date: Date? = null

            for (pattern in formats) {
                try {
                    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    date = sdf.parse(normalized)
                    if (date != null) break
                } catch (_: Exception) {}
            }

            if (date == null) return "-"

            val output = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            )
            output.timeZone = TimeZone.getDefault()

            output.format(date)

        } catch (e: Exception) {
            "-"
        }
    }
}


