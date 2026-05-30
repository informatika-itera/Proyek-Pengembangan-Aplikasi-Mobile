package com.example.foodsaver.presentation.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.model.FoodStatus
import com.example.foodsaver.domain.model.RecipeRecommendation
import com.example.foodsaver.domain.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CookFromStockUiState(
    val isLoading: Boolean = false,
    val ingredients: List<FoodItem> = emptyList(),
    val selectedIngredientIds: Set<Long> = emptySet(),
    val manualIngredients: List<String> = emptyList(),
    val prioritizeExpired: Boolean = true,
    val preference: String = "Praktis",
    val recommendation: RecipeRecommendation? = null,
    val error: String? = null
)

class CookFromStockViewModel(
    private val repository: FoodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CookFromStockUiState())
    val state: StateFlow<CookFromStockUiState> = _state.asStateFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllFoodItems()
                .map { items -> 
                    items.filter { !it.isConsumed && !it.isDiscarded }
                        .sortedBy { it.getDaysRemaining() }
                }
                .collect { items ->
                    _state.update { it.copy(isLoading = false, ingredients = items) }
                }
        }
    }

    fun toggleIngredientSelection(id: Long) {
        _state.update { currentState ->
            val newSelection = if (currentState.selectedIngredientIds.contains(id)) {
                currentState.selectedIngredientIds - id
            } else {
                currentState.selectedIngredientIds + id
            }
            currentState.copy(selectedIngredientIds = newSelection)
        }
    }

    fun addManualIngredient(input: String) {
        if (input.isBlank()) {
            if (_state.value.selectedIngredientIds.isEmpty() && _state.value.manualIngredients.isEmpty()) {
                _state.update { it.copy(error = "Pilih atau masukkan minimal satu bahan terlebih dahulu.") }
            }
            return
        }
        
        val newItems = input.split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
        
        _state.update { currentState ->
            val currentList = currentState.manualIngredients.toMutableList()
            newItems.forEach { item ->
                if (!currentList.contains(item)) {
                    currentList.add(item)
                }
            }
            currentState.copy(manualIngredients = currentList, error = null)
        }
    }

    fun removeManualIngredient(ingredient: String) {
        _state.update { currentState ->
            currentState.copy(manualIngredients = currentState.manualIngredients - ingredient)
        }
    }

    fun setPrioritizeExpired(prioritize: Boolean) {
        _state.update { it.copy(prioritizeExpired = prioritize) }
    }

    fun setPreference(preference: String) {
        _state.update { it.copy(preference = preference) }
    }

    fun resetIngredients() {
        _state.update { it.copy(
            selectedIngredientIds = emptySet(),
            manualIngredients = emptyList(),
            recommendation = null,
            error = null
        ) }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun generateRecommendation(
        ingredientIds: List<Long>,
        manualIngredients: List<String>,
        prioritize: Boolean,
        pref: String
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Wait until ingredients are loaded if they are not yet
            if (_state.value.ingredients.isEmpty()) {
                repository.getAllFoodItems()
                    .map { items -> items.filter { !it.isConsumed && !it.isDiscarded } }
                    .first().let { items ->
                        _state.update { it.copy(ingredients = items) }
                    }
            }
            
            val inventoryItems = _state.value.ingredients.filter { ingredientIds.contains(it.id) }
            
            if (inventoryItems.isEmpty() && manualIngredients.isEmpty()) {
                _state.update { it.copy(isLoading = false, error = "Pilih atau masukkan minimal satu bahan terlebih dahulu.") }
                return@launch
            }

            val recommendation = runRecipeEngine(inventoryItems, manualIngredients, prioritize, pref)
            _state.update { it.copy(isLoading = false, recommendation = recommendation, error = null) }
        }
    }

    fun markIngredientsAsUsed(ids: List<Long>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            ids.forEach { id ->
                repository.getFoodItemById(id)?.let { item ->
                    repository.updateFoodItem(item.copy(isConsumed = true))
                }
            }
            onSuccess()
        }
    }

    private fun runRecipeEngine(
        inventoryItems: List<FoodItem>,
        manualItems: List<String>,
        prioritize: Boolean,
        pref: String
    ): RecipeRecommendation {
        val allIngredientNames = (inventoryItems.map { it.name.lowercase() } + manualItems.map { it.lowercase() })
        val hasExpired = inventoryItems.any { it.getStatus() == FoodStatus.EXPIRED || it.getStatus() == FoodStatus.EXPIRED_TODAY }
        
        val mostUrgent = inventoryItems.minByOrNull { it.getDaysRemaining() }
        val reasonPrefix = if (prioritize && mostUrgent != null && mostUrgent.getDaysRemaining() <= 3) {
            "${mostUrgent.name} akan expired dalam ${mostUrgent.getDaysRemaining()} hari, jadi sebaiknya digunakan lebih dulu. "
        } else ""

        fun has(name: String) = allIngredientNames.any { it.contains(name.lowercase()) }

        return when {
            has("nasi") && has("telur") -> RecipeRecommendation(
                title = "Nasi Goreng Telur",
                description = "Menu praktis dari bahan yang kamu punya.",
                usedIngredients = listOf("nasi", "telur"),
                optionalIngredients = listOf("bawang putih", "kecap", "garam"),
                cookingTimeMinutes = 15,
                difficulty = "Mudah",
                reason = reasonPrefix + "Resep ini cocok karena menggunakan nasi dan telur yang tersedia.",
                steps = listOf("Siapkan nasi dan telur.", "Tumis bawang putih hingga harum.", "Masukkan telur dan orak-arik.", "Tambahkan nasi.", "Aduk rata dengan bumbu.", "Sajikan selagi hangat."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            has("nasi") && has("bakso") -> RecipeRecommendation(
                title = "Nasi Goreng Bakso",
                description = "Menu praktis dari bahan yang kamu punya.",
                usedIngredients = listOf("bakso", "nasi", "telur"),
                optionalIngredients = listOf("bawang putih", "kecap", "garam"),
                cookingTimeMinutes = 15,
                difficulty = "Mudah",
                reason = reasonPrefix + "Resep ini cocok karena menggunakan bakso dan nasi yang tersedia.",
                steps = listOf("Potong bakso sesuai selera.", "Tumis bawang putih hingga harum.", "Masukkan telur dan bakso.", "Tambahkan nasi.", "Aduk rata dengan bumbu.", "Sajikan selagi hangat."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            has("mie") && has("bakso") -> RecipeRecommendation(
                title = "Mie Bakso Praktis",
                description = "Sajian mie hangat dengan bakso.",
                usedIngredients = listOf("mie", "bakso"),
                optionalIngredients = listOf("sawi", "bawang goreng"),
                cookingTimeMinutes = 10,
                difficulty = "Sangat Mudah",
                reason = reasonPrefix + "Kombinasi klasik mie dan bakso yang selalu enak.",
                steps = listOf("Rebus mie hingga matang.", "Siapkan bumbu mie.", "Masukkan bakso ke dalam rebusan.", "Sajikan dalam mangkuk."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            has("roti") && has("susu") -> RecipeRecommendation(
                title = "Roti Susu Panggang",
                description = "Sarapan manis dan bergizi.",
                usedIngredients = listOf("roti", "susu"),
                optionalIngredients = listOf("keju", "mentega"),
                cookingTimeMinutes = 10,
                difficulty = "Mudah",
                reason = reasonPrefix + "Menggunakan roti dan susu untuk sarapan cepat.",
                steps = listOf("Olesi roti dengan mentega.", "Panggang roti hingga kecokelatan.", "Tuangkan susu atau sajikan sebagai pendamping."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            has("ayam") && (has("sayur") || has("wortel") || has("bayam")) -> RecipeRecommendation(
                title = "Tumis Ayam Sayur",
                description = "Menu sehat tinggi protein dan serat.",
                usedIngredients = listOf("ayam", "sayur"),
                optionalIngredients = listOf("bawang bombay", "saus tiram"),
                cookingTimeMinutes = 25,
                difficulty = "Sedang",
                reason = reasonPrefix + "Kombinasi ayam dan sayuran sangat baik untuk kesehatan.",
                steps = listOf("Potong ayam kotak-kotak.", "Tumis bumbu.", "Masukkan ayam hingga matang.", "Masukkan sayuran.", "Masak hingga sayur layu."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            has("telur") && (has("sayur") || has("bayam") || has("wortel")) -> RecipeRecommendation(
                title = "Telur Dadar Sayur",
                description = "Telur dadar tebal dengan potongan sayur.",
                usedIngredients = listOf("telur", "sayur"),
                optionalIngredients = listOf("cabai", "lada"),
                cookingTimeMinutes = 10,
                difficulty = "Sangat Mudah",
                reason = reasonPrefix + "Cara mudah makan sayur dengan telur.",
                steps = listOf("Kocok telur.", "Masukkan irisan sayur.", "Tambahkan garam dan lada.", "Goreng hingga matang."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            (has("buah") || has("pisang") || has("mangga")) && has("susu") -> RecipeRecommendation(
                title = "Smoothie Buah Susu",
                description = "Minuman segar penambah energi.",
                usedIngredients = listOf("buah", "susu"),
                optionalIngredients = listOf("madu", "es batu"),
                cookingTimeMinutes = 5,
                difficulty = "Mudah",
                reason = reasonPrefix + "Olahan buah segar dengan susu yang nikmat.",
                steps = listOf("Potong-potong buah.", "Masukkan ke blender bersama susu.", "Blender hingga halus.", "Sajikan dingin."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            (has("tahu") || has("tempe")) && has("sayur") -> RecipeRecommendation(
                title = "Tumis Tahu Tempe Sayur",
                description = "Menu ekonomis dan bergizi.",
                usedIngredients = listOf("tahu", "tempe", "sayur"),
                optionalIngredients = listOf("kecap manis", "lengkuas"),
                cookingTimeMinutes = 20,
                difficulty = "Mudah",
                reason = reasonPrefix + "Protein nabati dari tahu/tempe sangat cocok ditumis dengan sayur.",
                steps = listOf("Goreng tahu/tempe setengah matang.", "Tumis bumbu.", "Masukkan sayuran.", "Masukkan tahu/tempe.", "Beri kecap manis."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            has("kentang") && has("telur") -> RecipeRecommendation(
                title = "Perkedel Kentang Sederhana",
                description = "Lauk pauk yang gurih dan lembut.",
                usedIngredients = listOf("kentang", "telur"),
                optionalIngredients = listOf("seledri", "bawang merah goreng"),
                cookingTimeMinutes = 30,
                difficulty = "Sedang",
                reason = reasonPrefix + "Kentang dan telur bisa diolah menjadi perkedel lezat.",
                steps = listOf("Rebus kentang dan haluskan.", "Campur dengan bumbu.", "Bentuk bulat pipih.", "Celupkan ke telur.", "Goreng hingga keemasan."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            has("pisang") && has("tepung") -> RecipeRecommendation(
                title = "Pisang Goreng",
                description = "Camilan hangat teman minum teh.",
                usedIngredients = listOf("pisang", "tepung"),
                optionalIngredients = listOf("gula", "vanili"),
                cookingTimeMinutes = 15,
                difficulty = "Mudah",
                reason = reasonPrefix + "Cara klasik menikmati pisang.",
                steps = listOf("Buat adonan tepung.", "Celupkan pisang ke adonan.", "Goreng dalam minyak panas.", "Tiriskan dan sajikan."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
            else -> RecipeRecommendation(
                title = "Menu Sederhana dari Bahan Tersedia",
                description = "Kreasi bebas dari stok yang kamu punya.",
                usedIngredients = allIngredientNames,
                optionalIngredients = listOf("minyak goreng", "garam", "bawang putih"),
                cookingTimeMinutes = 15,
                difficulty = "Mudah",
                reason = reasonPrefix + "Memanfaatkan bahan yang ada agar tidak terbuang.",
                steps = listOf("Siapkan semua bahan.", "Tumis bumbu dasar.", "Masukkan bahan secara bertahap.", "Masak hingga matang."),
                warningMessage = if (hasExpired) "Peringatan: Ada bahan dari inventory yang expired!" else null
            )
        }
    }
}
