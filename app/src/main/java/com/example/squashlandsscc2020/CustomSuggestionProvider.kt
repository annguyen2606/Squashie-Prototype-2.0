package com.example.squashlandsscc2020

import android.content.SearchRecentSuggestionsProvider

class CustomSuggestionProvider: SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
        sdfdfgfghgfhjghjgh
    }

    companion object {
        const val AUTHORITY = "com.example.CustomSuggestionProvider"
        const val MODE: Int = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES
    }
}