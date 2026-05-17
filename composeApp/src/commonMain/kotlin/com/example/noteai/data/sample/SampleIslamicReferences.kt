package com.example.noteai.data.sample

import com.example.noteai.domain.model.islamic.IslamicReference
import com.example.noteai.domain.model.islamic.SourceType
import com.example.noteai.domain.model.islamic.TopicOption

object SampleIslamicReferences {

    val topics = listOf(
        TopicOption(
            id = "anger",
            title = "Mengendalikan Amarah",
            subtitle = "Dalil saat emosi mulai menguasai diri",
            icon = "🔥"
        ),
        TopicOption(
            id = "calm",
            title = "Ketenangan Hati",
            subtitle = "Pengingat saat hati gelisah dan cemas",
            icon = "🌿"
        ),
        TopicOption(
            id = "sabr",
            title = "Sabar",
            subtitle = "Pegangan ketika menghadapi ujian",
            icon = "🤲"
        ),
        TopicOption(
            id = "taubah",
            title = "Taubat",
            subtitle = "Kembali kepada Allah tanpa putus asa",
            icon = "✨"
        ),
        TopicOption(
            id = "syukur",
            title = "Syukur",
            subtitle = "Mengingat nikmat dan karunia Allah",
            icon = "☀️"
        ),
        TopicOption(
            id = "parents",
            title = "Berbakti Orang Tua",
            subtitle = "Adab dan kewajiban kepada orang tua",
            icon = "🏡"
        )
    )

    val references = listOf(
        IslamicReference(
            id = "quran-ali-imran-134",
            sourceType = SourceType.QURAN,
            title = "Menahan Amarah",
            sourceName = "QS. Ali Imran: 134",
            arabicText = "وَالْكَاظِمِينَ الْغَيْظَ وَالْعَافِينَ عَنِ النَّاسِ",
            translation = "Dan orang-orang yang menahan amarahnya dan memaafkan kesalahan orang lain.",
            explanation = "Ayat ini mengajarkan bahwa menahan amarah dan memaafkan adalah bagian dari akhlak orang bertakwa.",
            topicId = "anger",
            topicTitle = "Mengendalikan Amarah"
        ),
        IslamicReference(
            id = "hadith-dont-be-angry",
            sourceType = SourceType.HADITH,
            title = "Jangan Marah",
            sourceName = "HR. Bukhari",
            arabicText = "لَا تَغْضَبْ",
            translation = "Jangan marah.",
            explanation = "Nasihat singkat ini menekankan pentingnya menguasai emosi sebelum mengambil sikap atau keputusan.",
            topicId = "anger",
            topicTitle = "Mengendalikan Amarah"
        ),
        IslamicReference(
            id = "quran-ar-rad-28",
            sourceType = SourceType.QURAN,
            title = "Hati Menjadi Tenang",
            sourceName = "QS. Ar-Ra'd: 28",
            arabicText = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            translation = "Ingatlah, hanya dengan mengingat Allah hati menjadi tenteram.",
            explanation = "Ketenangan tidak hanya dicari dari keadaan luar, tetapi juga dari kedekatan hati dengan Allah.",
            topicId = "calm",
            topicTitle = "Ketenangan Hati"
        ),
        IslamicReference(
            id = "quran-al-baqarah-153",
            sourceType = SourceType.QURAN,
            title = "Pertolongan dengan Sabar dan Shalat",
            sourceName = "QS. Al-Baqarah: 153",
            arabicText = "اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ",
            translation = "Mohonlah pertolongan dengan sabar dan shalat.",
            explanation = "Saat menghadapi kesulitan, Islam mengarahkan manusia untuk memperkuat kesabaran dan hubungan dengan Allah.",
            topicId = "sabr",
            topicTitle = "Sabar"
        ),
        IslamicReference(
            id = "quran-az-zumar-53",
            sourceType = SourceType.QURAN,
            title = "Jangan Berputus Asa",
            sourceName = "QS. Az-Zumar: 53",
            arabicText = "لَا تَقْنَطُوا مِنْ رَحْمَةِ اللَّهِ",
            translation = "Janganlah kamu berputus asa dari rahmat Allah.",
            explanation = "Ayat ini menguatkan bahwa pintu taubat selalu terbuka bagi hamba yang ingin kembali kepada Allah.",
            topicId = "taubah",
            topicTitle = "Taubat"
        ),
        IslamicReference(
            id = "quran-ibrahim-7",
            sourceType = SourceType.QURAN,
            title = "Syukur Menambah Nikmat",
            sourceName = "QS. Ibrahim: 7",
            arabicText = "لَئِنْ شَكَرْتُمْ لَأَزِيدَنَّكُمْ",
            translation = "Jika kamu bersyukur, niscaya Aku akan menambah nikmat kepadamu.",
            explanation = "Syukur mengajarkan manusia untuk menyadari nikmat Allah dan menggunakannya dengan benar.",
            topicId = "syukur",
            topicTitle = "Syukur"
        ),
        IslamicReference(
            id = "quran-al-isra-23",
            sourceType = SourceType.QURAN,
            title = "Berbuat Baik kepada Orang Tua",
            sourceName = "QS. Al-Isra: 23",
            arabicText = "وَبِالْوَالِدَيْنِ إِحْسَانًا",
            translation = "Dan hendaklah berbuat baik kepada ibu bapak.",
            explanation = "Ayat ini menempatkan bakti kepada orang tua sebagai kewajiban besar setelah ibadah kepada Allah.",
            topicId = "parents",
            topicTitle = "Berbakti Orang Tua"
        )
    )
}
