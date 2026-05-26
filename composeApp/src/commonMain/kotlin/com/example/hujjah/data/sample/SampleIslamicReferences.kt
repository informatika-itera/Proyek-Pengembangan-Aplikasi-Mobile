package com.example.hujjah.data.sample

import com.example.hujjah.domain.model.islamic.IslamicReference
import com.example.hujjah.domain.model.islamic.SourceType
import com.example.hujjah.domain.model.islamic.TopicOption

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
            id = "shalat",
            title = "Shalat",
            subtitle = "Mengingat kewajiban tiang agama",
            icon = "🕌"
        ),
        TopicOption(
            id = "tawakkal_cemas",
            title = "Tawakkal / Cemas",
            subtitle = "Pasrah dan percaya pada rencana Allah",
            icon = "🛡️"
        ),
        TopicOption(
            id = "ilmu",
            title = "Ilmu",
            subtitle = "Keutamaan menuntut ilmu dan belajar",
            icon = "📚"
        ),
        TopicOption(
            id = "parents",
            title = "Berbakti Orang Tua",
            subtitle = "Adab dan kewajiban kepada orang tua",
            icon = "🏡"
        ),
        TopicOption(
            id = "rezeki",
            title = "Rezeki",
            subtitle = "Jaminan rezeki dan usaha yang berkah",
            icon = "💰"
        )
    )

    val references = listOf(
        // === ANGER ===
        IslamicReference(
            id = "quran-ali-imran-134",
            sourceType = SourceType.QURAN,
            title = "Menahan Amarah",
            sourceName = "QS. Ali 'Imran: 134",
            arabicText = "الَّذِينَ يُنْفِقُونَ فِي السَّRَّاءِ وَالضَّRَّاءِ وَالْكَاظِمِينَ الْغَيْظَ وَالْعَافِينَ عَنِ النَّاسِ ۗ وَاللَّهُ يُحِبُّ الْمُحْسِنِينَ",
            translation = "(yaitu) orang-orang yang berinfak, baik di waktu lapang maupun sempit, dan orang-orang yang menahan amarahnya dan memaafkan (kesalahan) orang lain. Dan Allah mencintai orang-orang yang berbuat kebaikan.",
            explanation = "Menahan amarah dan memaafkan kesalahan orang lain merupakan ciri utama orang yang bertakwa dan dicintai Allah.",
            topicId = "anger",
            topicTitle = "Mengendalikan Amarah"
        ),
        IslamicReference(
            id = "hadith-dont-be-angry",
            sourceType = SourceType.HADITH,
            title = "Jangan Marah",
            sourceName = "HR. Bukhari",
            arabicText = "لَا تَغْضَبْ وَلَكَ الْجَنَّةُ",
            translation = "Janganlah engkau marah, maka bagimu surga.",
            explanation = "Nasihat Rasulullah SAW yang diulang-ulang agar kita dapat mengendalikan nafsu amarah demi mendapatkan rida Allah.",
            topicId = "anger",
            topicTitle = "Mengendalikan Amarah"
        ),

        // === CALM ===
        IslamicReference(
            id = "quran-ar-rad-28",
            sourceType = SourceType.QURAN,
            title = "Hati Menjadi Tenang",
            sourceName = "QS. Ar-Ra'd: 28",
            arabicText = "الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُمْ بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            translation = "(yaitu) orang-orang yang beriman dan hati mereka menjadi tenteram dengan mengingat Allah. Ingatlah, hanya dengan mengingat Allah hati menjadi tenteram.",
            explanation = "Ketenangan hati yang sejati tidak ditemukan dalam materi, melainkan dengan memperbanyak mengingat Allah (zikir).",
            topicId = "calm",
            topicTitle = "Ketenangan Hati"
        ),
        IslamicReference(
            id = "quran-al-insyirah-5-6",
            sourceType = SourceType.QURAN,
            title = "Kemudahan Bersama Kesulitan",
            sourceName = "QS. Al-Insyirah: 5-6",
            arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا • إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            translation = "Maka sesungguhnya beserta kesulitan ada kemudahan. Sesungguhnya beserta kesulitan itu ada kemudahan.",
            explanation = "Allah mengulangi kalimat ini dua kali sebagai jaminan pasti bahwa setiap ujian hidup pasti disertai dengan jalan keluarnya.",
            topicId = "calm",
            topicTitle = "Ketenangan Hati"
        ),

        // === SABR ===
        IslamicReference(
            id = "quran-al-baqarah-153",
            sourceType = SourceType.QURAN,
            title = "Pertolongan dengan Sabar dan Shalat",
            sourceName = "QS. Al-Baqarah: 153",
            arabicText = "يَا أَيُّهَا الَّذِينَ آمَنُوا اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ ۚ إِنَّ اللَّهَ مَعَ الصَّابِرِينَ",
            translation = "Wahai orang-orang yang beriman! Mohonlah pertolongan (kepada Allah) dengan sabar dan shalat. Sungguh, Allah beserta orang-orang yang sabar.",
            explanation = "Sabar and shalat merupakan benteng spiritual terbaik bagi seorang mukmin dalam menghadapi badai ujian kehidupan.",
            topicId = "sabr",
            topicTitle = "Sabar"
        ),
        IslamicReference(
            id = "quran-az-zumar-10",
            sourceType = SourceType.QURAN,
            title = "Pahala Tanpa Batas bagi Orang Sabar",
            sourceName = "QS. Az-Zumar: 10",
            arabicText = "إِنَّمَا يُوَفَّى الصَّابِرُونَ أَجْرَهُمْ بِغَيْرِ حِسَابٍ",
            translation = "Hanya orang-orang yang bersabarlah yang disempurnakan pahalanya tanpa batas.",
            explanation = "Besarnya ganjaran kesabaran ditunjukkan dengan janji Allah yang akan melipatgandakan pahala mereka tanpa ada batasan ukuran.",
            topicId = "sabr",
            topicTitle = "Sabar"
        ),

        // === TAUBAH ===
        IslamicReference(
            id = "quran-az-zumar-53",
            sourceType = SourceType.QURAN,
            title = "Jangan Berputus Asa",
            sourceName = "QS. Az-Zumar: 53",
            arabicText = "قُلْ يَا عِبَادِيَ الَّذِينَ أَسْرَفُوا عَلَىٰ أَنْفُسِهِمْ لَا تَقْنَطُوا مِنْ رَحْمَةِ اللَّهِ ۚ إِنَّ اللَّهَ يَغْفِرُ الذُّنُوبَ جَمِيعًا ۚ إِنَّهُ هُوَ الْغَفُورُ الرَّحِيمُ",
            translation = "Katakanlah (Muhammad), 'Wahai hamba-hamba-Ku yang melampaui batas terhadap diri mereka sendiri! Janganlah kamu berputus asa dari rahmat Allah. Sesungguhnya Allah mengampuni dosa-dosa semuanya. Sungguh, Dialah Yang Maha Pengampun, Maha Penyayang.'",
            explanation = "Kasih sayang Allah teramat luas, pintu ampunan-Nya selalu terbuka lebar bagi siapa pun yang ingin kembali bertaubat.",
            topicId = "taubah",
            topicTitle = "Taubat"
        ),
        IslamicReference(
            id = "quran-at-tahrim-8",
            sourceType = SourceType.QURAN,
            title = "Taubat Nasuha",
            sourceName = "QS. At-Tahrim: 8",
            arabicText = "يَا أَيُّهَا الَّذِينَ آمَنُوا تُوبُوا إِلَى اللَّهِ تَوْبَةً نَصُوحًا",
            translation = "Wahai orang-orang yang beriman! Bertobatlah kepada Allah dengan tobat yang semurni-murninya (nasuha).",
            explanation = "Perintah untuk bertaubat dengan sungguh-sungguh, bertekad bulat meninggalkan maksiat, dan memperbaiki arah hidup ke jalan kebaikan.",
            topicId = "taubah",
            topicTitle = "Taubat"
        ),

        // === SYUKUR ===
        IslamicReference(
            id = "quran-ibrahim-7",
            sourceType = SourceType.QURAN,
            title = "Syukur Menambah Nikmat",
            sourceName = "QS. Ibrahim: 7",
            arabicText = "وَإِذْ تَأَذَّنَ رَبُّكُمْ لَئِنْ شَكَرْتُمْ لَأَزِيدَنَّكُمْ ۖ وَلَئِن... كَفَرْتُمْ إِنَّ عَذَابِي لَشَدِيدٌ",
            translation = "Dan (ingatlah) ketika Tuhanmu memaklumkan, 'Sesungguhnya jika kamu bersyukur, niscaya Aku akan menambah (nikmat) kepadamu, tetapi jika kamu mengingkari (nikmat-Ku), maka pasti azab-Ku sangat berat.'",
            explanation = "Bersyukur mendatangkan keberkahan dan kelipatan nikmat, sebaliknya kekufuran menutup pintu berkah dan mengundang murka Allah.",
            topicId = "syukur",
            topicTitle = "Syukur"
        ),

        // === SHALAT ===
        IslamicReference(
            id = "quran-al-maun-4-5",
            sourceType = SourceType.QURAN,
            title = "Lalai dalam Shalat",
            sourceName = "QS. Al-Ma'un: 4-5",
            arabicText = "فَوَيْلٌ لِلْمُصَلِّينَ • الَّذِينَ هُمْ عَنْ صَلَاتِهِمْ سَاهُونَ",
            translation = "Maka celakalah orang-orang yang shalat, (yaitu) orang-orang yang lalai terhadap shalatnya.",
            explanation = "Peringatan keras bagi orang yang menunda-nunda shalat, meremehkan rukunnya, atau shalat tanpa kekhusyukan dan kesungguhan.",
            topicId = "shalat",
            topicTitle = "Shalat"
        ),
        IslamicReference(
            id = "quran-al-baqarah-45",
            sourceType = SourceType.QURAN,
            title = "Shalat Menenangkan Jiwa",
            sourceName = "QS. Al-Baqarah: 45",
            arabicText = "وَاسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ ۚ وَإِنَّهَا لَكَبِيرَةٌ إِلَّا عَلَى الْخَاشِعِينَ",
            translation = "Dan mohonlah pertolongan (kepada Allah) dengan sabar dan shalat. Dan (shalat) itu sungguh berat, kecuali bagi orang-orang yang khusyuk.",
            explanation = "Mendirikan shalat dengan khusyuk mendatangkan pertolongan Allah dan menjadi sarana istirahat jiwa dari hiruk-pikuk dunia.",
            topicId = "shalat",
            topicTitle = "Shalat"
        ),

        // === TAWAKKAL_CEMAS ===
        IslamicReference(
            id = "quran-ath-thalaq-3",
            sourceType = SourceType.QURAN,
            title = "Allah Cukup bagi yang Bertawakkal",
            sourceName = "QS. Ath-Thalaq: 3",
            arabicText = "وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ ۚ وَمَنْ يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ",
            translation = "Dan Dia memberinya rezeki dari arah yang tidak disangka-sangkanya. Dan barangsiapa bertawakal kepada Allah, niscaya Allah akan mencukupkan (keperluan)nya.",
            explanation = "Tawakkal adalah menyerahkan segala urusan kepada Allah setelah berusaha. Keyakinan ini menghilangkan cemas dan kekhawatiran masa depan.",
            topicId = "tawakkal_cemas",
            topicTitle = "Tawakkal / Cemas"
        ),

        // === ILMU ===
        IslamicReference(
            id = "quran-al-mujadilah-11",
            sourceType = SourceType.QURAN,
            title = "Derajat Penuntut Ilmu",
            sourceName = "QS. Al-Mujadilah: 11",
            arabicText = "يَرْفَعِ اللَّهُ الَّذِينَ آمَنُوا مِنْكُمْ وَالَّذِينَ أُوتُوا الْعِلْمَ دَرَجَاتٍ",
            translation = "Niscaya Allah akan mengangkat (derajat) orang-orang yang beriman di antaramu dan orang-orang yang diberi ilmu pengetahuan beberapa derajat.",
            explanation = "Menuntut ilmu adalah kewajiban mulia yang mengangkat martabat dan derajat seorang manusia baik di dunia maupun di akhirat.",
            topicId = "ilmu",
            topicTitle = "Ilmu"
        ),

        // === PARENTS ===
        IslamicReference(
            id = "quran-al-isra-23",
            sourceType = SourceType.QURAN,
            title = "Berbakti kepada Orang Tua",
            sourceName = "QS. Al-Isra: 23",
            arabicText = "وَبِالْوَالِدَيْنِ إِحْسَانًا ۚ إِمَّا يَبْلُغَنَّ عِنْدَكَ الْكِبَرَ أَحَدُهُمَا أَوْ كِلَاهُمَا فَلَا تَقُلْ لَهُمَا أُفٍّ وَلَا تَنْهَرْهُمَا وَقُلْ لَهُمَا قَوْلًا كَرِيمًا",
            translation = "Dan hendaklah berbuat baik kepada ibu bapak. Jika salah seorang di antara keduanya atau kedua-duanya sampai berusia lanjut dalam pemeliharaanmu, maka sekali-kali janganlah engkau mengatakan kepada keduanya perkataan 'ah' dan janganlah engkau membentak keduanya, dan ucapkanlah kepada keduanya perkataan yang baik.",
            explanation = "Berbakti kepada orang tua adalah amal ibadah yang sangat tinggi tingkatannya, bahkan mendekati kewajiban menyembah Allah semata.",
            topicId = "parents",
            topicTitle = "Berbakti Orang Tua"
        ),

        // === REZEKI ===
        IslamicReference(
            id = "quran-hud-6",
            sourceType = SourceType.QURAN,
            title = "Jaminan Rezeki dari Allah",
            sourceName = "QS. Hud: 6",
            arabicText = "وَمَا مِنْ دَابَّةٍ فِي الْأَرْضِ إِلَّا عَلَى اللَّهِ رِزْقُهَا",
            translation = "Dan tidak satu pun makhluk bergerak (bernyawa) di bumi melainkan dijamin Allah rezekinya.",
            explanation = "Setiap makhluk hidup telah dijamin rezekinya oleh Allah SWT. Tugas manusia hanyalah berusaha menjemputnya dengan cara yang halal.",
            topicId = "rezeki",
            topicTitle = "Rezeki"
        ),
        IslamicReference(
            id = "quran-ath-thalaq-2-3",
            sourceType = SourceType.QURAN,
            title = "Jalan Keluar dan Rezeki Tak Disangka",
            sourceName = "QS. Ath-Thalaq: 2-3",
            arabicText = "وَمَنْ يَتَّقِ اللَّهَ يَجْعَلْ لَهُ مَخْرَجًا • وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ",
            translation = "Barangsiapa bertakwa kepada Allah niscaya Dia akan membukakan jalan keluar baginya, dan memberinya rezeki dari arah yang tidak disangka-sangkanya.",
            explanation = "Ketakwaan kepada Allah mendatangkan berkah berupa solusi atas segala kesulitan hidup dan rezeki yang tidak pernah diduga.",
            topicId = "rezeki",
            topicTitle = "Rezeki"
        )
    )
}
