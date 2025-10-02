package com.hubua.beboena.bl

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object Analytics {

    enum class LevelName(val analyticsName: String) {
        // String values used in Looker Studio analytics filter
        RESEMBLES("Resembles"),
        TRANSCRIPT("Transcript")
    }

    private val firebaseAnalytics = Firebase.analytics

    private val currentLetter get() = GeorgianAlphabet.Cursor.currentLetter

    private fun buildLevelStr(): String {
        // Looker Studio analytics filter match to L \d{3} - \p{L}
        return "L ${currentLetter.learnOrder.toString().padStart(3, '0')} - ${currentLetter.letterModernSpelling}"
    }

    //TODO FIX ALL DEPRECATED and TEST
    fun logScreenView(name: String) {
        // https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-screen_view
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, name)
        }
    }

    fun logLevelEnd(levelName: LevelName, success: String) {
        // https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-level_end
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_END) {
            param(FirebaseAnalytics.Param.LEVEL_NAME, levelName.analyticsName)
            param(FirebaseAnalytics.Param.LEVEL, buildLevelStr())
            param(FirebaseAnalytics.Param.SUCCESS, success)
        }
    }

    fun logPostScore(score: String, percent: Long) {
        // https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-post_score
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE) {
            param(FirebaseAnalytics.Param.LEVEL, buildLevelStr())
            param(FirebaseAnalytics.Param.SCORE, score)
            param("score_percent", percent)
        }
    }

}