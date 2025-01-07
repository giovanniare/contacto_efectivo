package com.example.contacto_efectivo

import android.content.Context;
import android.content.SharedPreferences;

class TokenManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveToken(data: AuthData) {
        preferences.edit().putString("token", data.token).apply()
        preferences.edit().putInt("id", data.id).apply()
        preferences.edit().putString("user_id", data.user_id).apply()
        preferences.edit().putString("created", data.created).apply()
    }

    fun getToken(): String? {
        return preferences.getString("token", null)
    }

    fun getId(): Int {
        return preferences.getInt("id", 0)
    }

    fun getUserId(): String? {
        return preferences.getString("user_id", null)
    }

    fun clearToken() {
        preferences.edit().remove("token").apply()
        preferences.edit().remove("id").apply()
        preferences.edit().remove("user_id").apply()
        preferences.edit().remove("created").apply()
    }
}
