package com.example.Roomie.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.koin.dsl.module

val supabaseModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = "https://sgxhjpequhgiifmdbnkt.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNneGhqcGVxdWhnaWlmbWRibmt0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzkwMzY4NzYsImV4cCI6MjA5NDYxMjg3Nn0.TOJ24G7Oq3yG7UhEq65xtuNyV005g_Ip5voqvF4VrV4"
        ) {
            install(Storage)
            install(Postgrest)
        }
    }
}
