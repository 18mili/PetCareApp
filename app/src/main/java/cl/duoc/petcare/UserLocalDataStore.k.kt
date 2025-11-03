package cl.duoc.petcare

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.userPrefs by preferencesDataStore(name = "petcare_user_prefs")

private val USERS_RAW = stringPreferencesKey("users_raw")

data class User(
    val name: String,
    val email: String,
    val password: String,
    val profileImage: String = ""
)

class UserLocalDataStore(private val context: Context) {

    private suspend fun getRaw(): String {
        return context.userPrefs.data.map { prefs ->
            prefs[USERS_RAW] ?: ""
        }.first()
    }

    suspend fun getAllUsers(): List<User> {
        val raw = getRaw()
        if (raw.isBlank()) return emptyList()

        return raw.split(";")
            .filter { it.isNotBlank() }
            .map { one ->
                val parts = one.split("|")
                val name = parts.getOrNull(0) ?: ""
                val email = parts.getOrNull(1) ?: ""
                val password = parts.getOrNull(2) ?: ""
                val profileImage = parts.getOrNull(3) ?: ""
                User(name = name, email = email, password = password, profileImage = profileImage)
            }
    }

    private suspend fun saveAll(users: List<User>) {
        val raw = users.joinToString(separator = ";") { u ->
            "${u.name}|${u.email}|${u.password}"
        }
        context.userPrefs.edit { prefs ->
            prefs[USERS_RAW] = raw
        }
    }

    suspend fun addUser(user: User) {
        val current = getAllUsers()
        saveAll(current + user)
    }

    suspend fun updateUser(user: User) {
        val current = getAllUsers()
        val updated = current.map { if (it.email.equals(user.email, ignoreCase = true)) user else it }
        saveAll(updated)
    }

    suspend fun getUserByEmail(email: String): User? {
        return getAllUsers().firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    suspend fun getUserByName(name: String): User? {
        return getAllUsers().firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
