package com.example.inventra.core.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    /** Client utama dengan anon key — untuk operasi user biasa */
    val client = createSupabaseClient(
        supabaseUrl = ApiConfig.supabaseUrl,
        supabaseKey = ApiConfig.supabaseAnonKey
    ) {
        install(Auth) {
            alwaysAutoRefresh = true
            autoLoadFromStorage = true
        }
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }

    /**
     * Admin client dengan service role key.
     * Digunakan HANYA untuk:
     * - Membuat user baru (auth.admin.createUser) tanpa logout admin
     * - Insert/delete profile dengan bypass RLS
     * - Hapus semua data untuk keperluan demo
     *
     * JANGAN gunakan untuk operasi user biasa.
     */
    val adminClient by lazy {
        createSupabaseClient(
            supabaseUrl = ApiConfig.supabaseUrl,
            supabaseKey = ApiConfig.supabaseServiceRoleKey
        ) {
            install(Auth) {
                alwaysAutoRefresh = false
                autoLoadFromStorage = false
            }
            install(Postgrest)
            install(Storage)
        }
    }
}