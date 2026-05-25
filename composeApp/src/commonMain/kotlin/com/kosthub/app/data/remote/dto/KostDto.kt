package com.kosthub.app.data.remote.dto

import kotlinx.serialization.Serializable
import com.kosthub.app.domain.model.Kost

@Serializable
data class KostDto(
    val id: Long,
    val contributorId: Long,
    val namaKos: String,
    val nomorTelepon: String? = null,
    val daerah: String,
    val jarakKm: Double,
    val hargaTahunan: Long,
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
) {
    fun toDomain(): Kost {
        return Kost(
            id = id,
            contributorId = contributorId,
            namaKos = namaKos,
            nomorTelepon = nomorTelepon,
            daerah = daerah,
            jarakKm = jarakKm,
            hargaTahunan = hargaTahunan,
            tipeKos = tipeKos,
            kamarMandi = kamarMandi,
            wifi = wifi,
            furniturKasur = furniturKasur,
            furniturLemari = furniturLemari,
            furniturMejaBelajar = furniturMejaBelajar,
            fasilitasPendingin = fasilitasPendingin,
            areaLaundry = areaLaundry,
            areaDapur = areaDapur,
            keamananCctv = keamananCctv,
            isFavorite = isFavorite
        )
    }
    
    companion object {
        fun fromDomain(kost: Kost): KostDto {
            return KostDto(
                id = kost.id,
                contributorId = kost.contributorId,
                namaKos = kost.namaKos,
                nomorTelepon = kost.nomorTelepon,
                daerah = kost.daerah,
                jarakKm = kost.jarakKm,
                hargaTahunan = kost.hargaTahunan,
                tipeKos = kost.tipeKos,
                kamarMandi = kost.kamarMandi,
                wifi = kost.wifi,
                furniturKasur = kost.furniturKasur,
                furniturLemari = kost.furniturLemari,
                furniturMejaBelajar = kost.furniturMejaBelajar,
                fasilitasPendingin = kost.fasilitasPendingin,
                areaLaundry = kost.areaLaundry,
                areaDapur = kost.areaDapur,
                keamananCctv = kost.keamananCctv,
                isFavorite = kost.isFavorite
            )
        }
    }
}

@Serializable
data class KostListResponse(
    val success: Boolean,
    val count: Int? = null,
    val data: List<KostDto> = emptyList(),
    val error: String? = null
)

@Serializable
data class KostDetailResponse(
    val success: Boolean,
    val data: KostDto? = null,
    val error: String? = null
)
