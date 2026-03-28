package com.darekbx.lightlauncher.repository.remote.stocks

class ResponseParser {

    fun parseResponse(response: String, partIndex: Int = 0): Double? {
        val start = response.indexOf("(")
        val end = response.indexOf(")", start + 1)
        val value = response.substring(start + 1, end)
        return (if (value.contains("|")) {
            val parts = value.split("|")
            extractFromPart(parts[partIndex])
        } else {
            extractFromPart(response)
        }).toDoubleOrNull()
    }

    private fun extractFromPart(part: String): String {
        val start = part.split("~")
        return start[3]
    }
}
