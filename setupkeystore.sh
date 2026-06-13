#!/usr/bin/env bash
# setup_keystore.sh — Generate release keystore untuk MasakuY
# Jalankan sekali dari root proyek: bash setup_keystore.sh

set -e

KEYSTORE_FILE="masakuy-release.jks"
KEY_ALIAS="masakuy"
PROPERTIES_FILE="local.properties"

echo "================================================"
echo "  MasakuY — Setup Release Keystore"
echo "================================================"
echo ""

if [ -f "$KEYSTORE_FILE" ]; then
    echo "⚠️  $KEYSTORE_FILE sudah ada."
    read -p "   Buat ulang? (y/N): " confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo "Dibatalkan."
        exit 0
    fi
fi

read -s -p "Store password (min 6 karakter): " STORE_PASS; echo ""
read -s -p "Konfirmasi store password: " STORE_PASS2; echo ""
[ "$STORE_PASS" != "$STORE_PASS2" ] && echo "❌ Password tidak cocok." && exit 1

read -s -p "Key password (Enter = sama dengan store): " KEY_PASS; echo ""
[ -z "$KEY_PASS" ] && KEY_PASS="$STORE_PASS"

read -p "Nama [MasakuY Developer]: " CN;  CN="${CN:-MasakuY Developer}"
read -p "Kota [Jakarta]: "           L;   L="${L:-Jakarta}"
read -p "Kode negara [ID]: "         C;   C="${C:-ID}"

echo ""
echo "🔑  Membuat keystore..."
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass "$STORE_PASS" \
    -keypass "$KEY_PASS" \
    -dname "CN=$CN, L=$L, C=$C" \
    -noprompt

echo "✅  $KEYSTORE_FILE berhasil dibuat"

# Tulis / update local.properties
touch "$PROPERTIES_FILE"
update_prop() {
    local key=$1 val=$2
    if grep -q "^$key=" "$PROPERTIES_FILE" 2>/dev/null; then
        sed -i "s|^$key=.*|$key=$val|" "$PROPERTIES_FILE"
    else
        echo "$key=$val" >> "$PROPERTIES_FILE"
    fi
}

update_prop "SIGNING_STORE_FILE"     "$KEYSTORE_FILE"
update_prop "SIGNING_STORE_PASSWORD" "$STORE_PASS"
update_prop "SIGNING_KEY_ALIAS"      "$KEY_ALIAS"
update_prop "SIGNING_KEY_PASSWORD"   "$KEY_PASS"

echo "✅  local.properties diperbarui (SIGNING_* entries)"

# Pastikan .jks masuk .gitignore
for entry in "*.jks" "local.properties"; do
    if ! grep -qF "$entry" .gitignore 2>/dev/null; then
        echo "$entry" >> .gitignore
        echo "   → $entry ditambahkan ke .gitignore"
    fi
done

echo ""
echo "================================================"
echo "  Langkah selanjutnya:"
echo "  ./gradlew :app:assembleRelease"
echo "  ./gradlew :app:bundleRelease   ← Play Store"
echo ""
echo "  ⚠️  JANGAN commit *.jks dan local.properties!"
echo "================================================"