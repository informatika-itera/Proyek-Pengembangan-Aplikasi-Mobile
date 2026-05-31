import re

file_path = r'd:\Coolleyah\Semester_6\Pengembangan Aplikasi Mobile\00_Tubes\Pantau_jompo\composeApp\src\commonMain\kotlin\com\example\pantaujompo\presentation\screens\tracking\TrackingScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)', 'userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)\n                            androidx.core.content.ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)?.let { userMarker?.icon = it }')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
