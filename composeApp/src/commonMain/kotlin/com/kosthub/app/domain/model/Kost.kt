package com.kosthub.app.domain.model

data class Kost(
    val id: Long,
    val contributorId: Long,
    val namaKos: String,
    val nomorTelepon: String?,
    val daerah: String,
    val jarakKm: Double,
    val hargaTahunan: String,
    val tipeKos: String,
    val kamarMandi: String,
    val wifi: String,
    val furniturKasur: String,
    val furniturLemari: String,
    val furniturMejaBelajar: String,
    val fasilitasPendingin: String,
    val areaLaundry: String,
    val areaDapur: String,
    val keamananCctv: String,
    val isFavorite: Boolean
)
