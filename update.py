import re

file_path = r'd:\Coolleyah\Semester_6\Pengembangan Aplikasi Mobile\00_Tubes\Pantau_jompo\composeApp\src\commonMain\kotlin\com\example\pantaujompo\presentation\screens\tracking\TrackingScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Add imports
imports = '''
import com.example.pantaujompo.presentation.theme.MeshBackground
import com.example.pantaujompo.presentation.theme.glassCard
import com.example.pantaujompo.core.util.AppStrings
import org.koin.compose.koinInject
'''
content = content.replace('import java.util.Locale\n', 'import java.util.Locale\n' + imports)

# Add userPreferences to TrackingScreen signature/body
content = content.replace('''
    val context = LocalContext.current
''', '''
    val context = LocalContext.current
    val userPreferences: com.example.pantaujompo.data.local.datastore.UserPreferences = org.koin.compose.koinInject()
    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String) = AppStrings.get(key, language)
''')

# Change root Box to MeshBackground
content = content.replace('Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D))) {', '''
    MeshBackground(modifier = Modifier.fillMaxSize()) {
''')

# Wrap Map in a glassCard look (rounded, some padding)
content = content.replace('modifier = Modifier.fillMaxSize(),', '''modifier = Modifier.fillMaxSize().padding(16.dp).clip(RoundedCornerShape(32.dp)),''')

# Update strings with str(...)
content = content.replace('"GPS Aktif"', 'str("gps_aktif")')
content = content.replace('"GPS Terputus"', 'str("gps_terputus")')
content = content.replace('"JARAK"', 'str("jarak_tempuh").uppercase()')
content = content.replace('"PACE"', 'str("pace").uppercase()')
content = content.replace('"KALORI"', 'str("kalori").uppercase()')
content = content.replace('"SELESAI"', 'str("selesai").uppercase()')

# Change bottom box to glassCard
content = content.replace('''        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .background(Color(0xFF0A0A0A).copy(alpha = 0.97f))
                .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .padding(top = 12.dp, start = 28.dp, end = 28.dp, bottom = 24.dp)
                .navigationBarsPadding()
        ) {''', '''        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .glassCard()
                .padding(top = 12.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                .navigationBarsPadding()
        ) {''')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("TrackingScreen updated.")
