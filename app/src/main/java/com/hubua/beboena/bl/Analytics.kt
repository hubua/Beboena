package com.hubua.beboena.bl

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object Analytics {

    private val firebaseAnalytics = Firebase.analytics

    fun logScreenView(name: String) {
        // https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-screen_view
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, name)
        }
    }

    fun logLevelEnd(levelName: String, success: String) {
        // https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-level_end
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_END) {
            param(FirebaseAnalytics.Param.LEVEL_NAME, levelName)
            param(FirebaseAnalytics.Param.LEVEL, "Letter ${GeorgianAlphabet.Cursor.currentLetter.learnOrder} - ${GeorgianAlphabet.Cursor.currentLetter.letterModernSpelling}")
            param(FirebaseAnalytics.Param.SUCCESS, success)
        }
    }

    fun logPostScore(score: String, percent: Long) {
        // https://firebase.google.com/docs/reference/android/com/google/firebase/analytics/FirebaseAnalytics.Event#public-static-final-string-post_score
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE) {
            param(FirebaseAnalytics.Param.LEVEL, "Letter ${GeorgianAlphabet.Cursor.currentLetter.learnOrder} - ${GeorgianAlphabet.Cursor.currentLetter.letterModernSpelling}")
            param(FirebaseAnalytics.Param.SCORE, score)
            param("score_percent", percent)
        }
    }

}