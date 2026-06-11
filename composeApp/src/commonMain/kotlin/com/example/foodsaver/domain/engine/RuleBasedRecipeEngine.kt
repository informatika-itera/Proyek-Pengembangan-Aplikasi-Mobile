package com.example.foodsaver.domain.engine

import com.example.foodsaver.domain.model.RecipeIngredient
import com.example.foodsaver.domain.model.RecipeRecommendation
import com.example.foodsaver.domain.model.IngredientSource

class RuleBasedRecipeEngine {
    fun generateRecommendations(
        ingredients: List<RecipeIngredient>,
        preference: String,
        prioritizeExpiring: Boolean
    ): RecipeRecommendation {
        val allIngredientNames = ingredients.map { it.name.lowercase() }
        
        val mostUrgent = if (prioritizeExpiring) {
            ingredients.filter { it.source == IngredientSource.INVENTORY && it.daysLeft != null }
                .minByOrNull { it.daysLeft!! }
        } else null

        val reasonPrefix = if (prioritizeExpiring && mostUrgent != null && mostUrgent.daysLeft!! <= 3) {
            "${mostUrgent.name} akan expired dalam ${mostUrgent.daysLeft} hari, jadi sebaiknya digunakan lebih dulu. "
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
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
                warningMessage = "Rekomendasi Lokal"
            )
        }
    }
}
