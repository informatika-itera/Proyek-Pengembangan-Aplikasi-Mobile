$body = @{
    contents = @(
        @{
            parts = @(
                @{
                    text = "Kamu adalah ahli gizi virtual asisten 'Pantau Jompo' yang gaul, suportif, dan asyik. Profil Pengguna: Pria 25 tahun, aktif. Identifikasi kandungan gizi makanan berikut secara akurat: Nasi Goreng. Balas HANYA dengan JSON valid, tanpa markdown (seperti ```json) atau teks pengantar apa pun. Gunakan struktur persis seperti ini: { 'nama': 'Nama Makanan Spesifik', 'protein': 12, 'karbo': 45, 'lemak': 8, 'info': 'Insight gaul singkat (maks 15 kata) buat sobat jompo terkait makanan ini.' }"
                }
            )
        }
    )
    generationConfig = @{
        temperature = 0.1
        maxOutputTokens = 500
        responseMimeType = "application/json"
    }
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-WebRequest -Uri "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=AIzaSyAN7BWR__JSC6guxhnLlKOmQRCanwYDr1U" -Method Post -ContentType "application/json" -Body $body -ErrorAction Stop
    Write-Output "Success: $($response.Content)"
} catch {
    $res = $_.Exception.Response
    if ($res) {
        $reader = New-Object System.IO.StreamReader($res.GetResponseStream())
        $body = $reader.ReadToEnd()
        Write-Output "Error Body: $body"
    } else {
        Write-Output "Error: $($_.Exception.Message)"
    }
}
