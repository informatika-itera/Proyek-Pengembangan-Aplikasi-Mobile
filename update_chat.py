import re

file_path = r'd:\Coolleyah\Semester_6\Pengembangan Aplikasi Mobile\00_Tubes\Pantau_jompo\composeApp\src\commonMain\kotlin\com\example\pantaujompo\presentation\screens\ai\AiChatScreen.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('import com.example.pantaujompo.presentation.theme.MeshBackground', 'import com.example.pantaujompo.presentation.theme.MeshBackground\nimport com.example.pantaujompo.presentation.theme.glassCard')

content = content.replace('.background(if (msg.isUser) accentColor else surfaceColor)\n                .border(1.dp, if (msg.isUser) accentColor else MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))', '.glassCard(shape = RoundedCornerShape(\\n                    topStart = 20.dp,\\n                    topEnd = 20.dp,\\n                    bottomStart = if (msg.isUser) 20.dp else 4.dp,\\n                    bottomEnd = if (msg.isUser) 4.dp else 20.dp\\n                ), neonColor = if(msg.isUser) accentColor else Color(0xFF00BCD4))')

content = content.replace('.background(surfaceColor)\n                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))', '.glassCard(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
