package com.quickcopy.app.util

import android.net.Uri
import java.util.regex.Pattern

object UrlCleaner {

    private val TRACKING_PARAMS = setOf(
        "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content", "utm_id",
        "fbclid", "gclid", "gclsrc", "dclid", "msclkid", "yclid",
        "si", // YouTube tracker
        "igsh", "igshid", // Instagram tracker
        "s", "t", // Twitter/X tracker
        "ref", "ref_src", "ref_url", "trk", "trackingId", "feature"
    )

    private val URL_PATTERN = Pattern.compile("https?://[^\\s]+")

    data class CleanResult(
        val cleanedText: String,
        val wasModified: Boolean,
        val paramsRemovedCount: Int
    )

    fun clean(text: String): CleanResult {
        if (text.isBlank()) return CleanResult(text, false, 0)

        val trimmed = text.trim()
        val matcher = URL_PATTERN.matcher(trimmed)
        var totalRemoved = 0
        val sb = StringBuffer()

        while (matcher.find()) {
            val rawUrl = matcher.group()
            val (cleanedUrl, removedCount) = cleanSingleUrl(rawUrl)
            totalRemoved += removedCount
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(cleanedUrl))
        }
        matcher.appendTail(sb)

        val resultText = sb.toString()
        return CleanResult(
            cleanedText = resultText,
            wasModified = totalRemoved > 0,
            paramsRemovedCount = totalRemoved
        )
    }

    private fun cleanSingleUrl(rawUrl: String): Pair<String, Int> {
        return try {
            val uri = Uri.parse(rawUrl)
            val queryParams = uri.queryParameterNames
            if (queryParams.isEmpty()) return Pair(rawUrl, 0)

            var removedCount = 0
            val uriBuilder = uri.buildUpon().clearQuery()

            for (param in queryParams) {
                val lowerParam = param.lowercase()
                if (TRACKING_PARAMS.contains(lowerParam) || lowerParam.startsWith("utm_")) {
                    removedCount++
                } else {
                    for (value in uri.getQueryParameters(param)) {
                        uriBuilder.appendQueryParameter(param, value)
                    }
                }
            }

            Pair(uriBuilder.build().toString(), removedCount)
        } catch (e: Exception) {
            Pair(rawUrl, 0)
        }
    }
}