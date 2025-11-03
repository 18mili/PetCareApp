package cl.duoc.petcare

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// extensión del DataStore (usa la misma preferencia que las mascotas si se desea, pero la separamos)
val Context.foodPrefs by preferencesDataStore(name = "petcare_food_prefs")

private val FOODS_RAW = stringPreferencesKey("foods_raw")

data class Food(
    val owner: String,
    val text: String
)

class FoodLocalDataStore(private val context: Context) {

    private suspend fun getRaw(): String {
        return context.foodPrefs.data.map { prefs ->
            prefs[FOODS_RAW] ?: ""
        }.first()
    }

    suspend fun getAllFoods(): List<Food> {
        val raw = getRaw()
        if (raw.isBlank()) return emptyList()

        return raw.split(";")
            .filter { it.isNotBlank() }
            .map { one ->
                val parts = one.split("|")
                val owner = parts.getOrNull(0) ?: ""
                val text = parts.getOrNull(1) ?: ""
                Food(owner = owner, text = text)
            }
    }

    private suspend fun saveAll(foods: List<Food>) {
        val raw = foods.joinToString(separator = ";") { f ->
            "${f.owner}|${f.text}"
        }
        context.foodPrefs.edit { prefs ->
            prefs[FOODS_RAW] = raw
        }
    }

    suspend fun addFood(food: Food) {
        val current = getAllFoods()
        saveAll(current + food)
    }

    suspend fun getFoodsByOwner(owner: String): List<Food> {
        return getAllFoods().filter { it.owner.equals(owner, ignoreCase = true) }
    }
}
