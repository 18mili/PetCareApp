package cl.duoc.petcare

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// extensión del DataStore
val Context.petPrefs by preferencesDataStore(name = "petcare_prefs")

// clave donde vamos a guardar TODO
private val PETS_RAW = stringPreferencesKey("pets_raw")

/**
 * Vamos a guardar las mascotas en un formato simple dentro de un string,
 * así no necesitamos kotlinx-serialization ni plugins.
 *
 * Formato:
 *  owner|name|species|age;owner2|name2|species2|age2;...
 */
class PetLocalDataStore(private val context: Context) {

    // 1) leer el string completo
    private suspend fun getRaw(): String {
        return context.petPrefs.data.map { prefs ->
            prefs[PETS_RAW] ?: ""
        }.first()
    }

    // 2) convertir string -> lista de Pet (todas)
    suspend fun getAllPets(): List<Pet> {
        val raw = getRaw()
        if (raw.isBlank()) return emptyList()

        return raw.split(";")
            .filter { it.isNotBlank() }
            .map { one ->
                val parts = one.split("|")
                val owner = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                val species = parts.getOrNull(2) ?: ""
                val age = parts.getOrNull(3) ?: ""
                val medical = parts.getOrNull(4) ?: ""
                val behavior = parts.getOrNull(5) ?: ""
                Pet(
                    owner = owner,
                    name = name,
                    species = species,
                    age = age,
                    medicalInfo = medical,
                    behavior = behavior
                )
            }
    }

    // 3) guardar lista -> string
    private suspend fun saveAll(pets: List<Pet>) {
        val raw = pets.joinToString(separator = ";") { pet ->
            // guardamos formato extendido owner|name|species|age|medical|behavior
            "${pet.owner}|${pet.name}|${pet.species}|${pet.age}|${pet.medicalInfo}|${pet.behavior}"
        }
        context.petPrefs.edit { prefs ->
            prefs[PETS_RAW] = raw
        }
    }

    // 4) agregar una mascota nueva
    suspend fun addPet(pet: Pet) {
        val current = getAllPets()
        saveAll(current + pet)
    }

    // 5) obtener solo las mascotas de un dueño
    suspend fun getPetsByOwner(owner: String): List<Pet> {
        return getAllPets().filter { it.owner.equals(owner, ignoreCase = true) }
    }

    // actualiza el nombre del dueño en todas las mascotas (cuando el usuario cambia su nombre)
    suspend fun updateOwnerName(oldName: String, newName: String) {
        val current = getAllPets()
        val updated = current.map { pet ->
            if (pet.owner.equals(oldName, ignoreCase = true)) pet.copy(owner = newName) else pet
        }
        saveAll(updated)
    }

    // 6) (opcional) borrar todo
    suspend fun clear() {
        context.petPrefs.edit { prefs ->
            prefs[PETS_RAW] = ""
        }
    }
}
