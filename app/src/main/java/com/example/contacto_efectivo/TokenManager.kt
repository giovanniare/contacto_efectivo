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

class UserManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveUser(data: User) {
        preferences.edit().putInt("uId", data.id).apply()
        preferences.edit().putString("nombre", data.nombre).apply()
        preferences.edit().putString("user_name", data.usuario_nombre).apply()
    }

    fun getName(): String? {
        return preferences.getString("nombre", null)
    }

    fun getId(): Int {
        return preferences.getInt("uId", 0)
    }

    fun clearUser() {
        preferences.edit().remove("uId").apply()
        preferences.edit().remove("nombre").apply()
        preferences.edit().remove("user_name").apply()
    }
}
