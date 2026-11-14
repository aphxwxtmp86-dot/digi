package com.example.digitalanswersheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF242638)
private val Muted = Color(0xFF777B8A)
private val Purple = Color(0xFF6254A5)
private val Canvas = Color(0xFFF7F7FA)
private val Line = Color(0xFFE6E6ED)

data class Exam(
    val id: Int, val name: String, val count: Int, val options: Int,
    val multi: Boolean, val columns: Int, val timed: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    var exams by remember { mutableStateOf(listOf<Exam>()) }
    var screen by remember { mutableStateOf("home") }
    var selected by remember { mutableStateOf<Exam?>(null) }
    var nextId by remember { mutableIntStateOf(1) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Purple, onPrimary = Color.White, background = Canvas,
            surface = Color.White, onSurface = Ink, secondary = Muted
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = Canvas) {
            when (screen) {
                "create" -> CreateScreen(
                    onBack = { screen = "home" },
                    onCreate = { name, count, opts, multi, cols, timed ->
                        val exam = Exam(nextId++, name, count, opts, multi, cols, timed)
                        exams = exams + exam
                        selected = exam
                        screen = "answer"
                    }
                )
                "answer" -> selected?.let { exam ->
                    AnswerScreen(exam, onBack = { screen = "home" })
                } ?: HomeScreen(exams, { screen = "create" }, { selected = it; screen = "answer" })
                else -> HomeScreen(exams, { screen = "create" }, { selected = it; screen = "answer" })
            }
        }
    }
}

@Composable
fun Header(title: String, subtitle: String? = null, back: (() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp)) {
        if (back != null) Text("‹  กลับ", color = Purple, modifier = Modifier.clickable { back() }.padding(bottom = 12.dp))
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Ink)
        if (subtitle != null) Text(subtitle, color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
fun HomeScreen(exams: List<Exam>, create: () -> Unit, open: (Exam) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Header("Digital Answer Sheet", "ระบบกระดาษคำตอบดิจิทัล")
        Column(Modifier.padding(horizontal = 20.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("จัดการชุดข้อสอบ", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    Text("สร้างกระดาษคำตอบ ตั้งค่าเฉลย และตรวจผลคะแนน", color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp))
                    Button(onClick = create, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("＋  สร้างชุดข้อสอบ")
                    }
                }
            }
            Text("ชุดข้อสอบของฉัน", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, modifier = Modifier.padding(top = 24.dp, bottom = 10.dp))
            if (exams.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Box(Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
                        Text("ยังไม่มีชุดข้อสอบ\nเริ่มต้นด้วยการสร้างชุดแรกของคุณ", color = Muted, lineHeight = 22.sp)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(exams) { exam ->
                        Card(Modifier.fillMaxWidth().clickable { open(exam) }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(15.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text(exam.name, fontWeight = FontWeight.SemiBold)
                                Text("${exam.count} ข้อ  •  ${exam.options} ตัวเลือก  •  ${if (exam.multi) "หลายคำตอบ" else "คำตอบเดียว"}", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateScreen(onBack: () -> Unit, onCreate: (String, Int, Int, Boolean, Int, Boolean) -> Unit) {
    var name by remember { mutableStateOf("ชุดข้อสอบใหม่") }
    var count by remember { mutableStateOf("100") }
    var optionCount by remember { mutableIntStateOf(5) }
    var multi by remember { mutableStateOf(false) }
    var columns by remember { mutableIntStateOf(1) }
    var timed by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Header("สร้างชุดข้อสอบ", "กำหนดรูปแบบกระดาษคำตอบ", onBack)
        Column(Modifier.weight(1f).padding(horizontal = 20.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("ชื่อชุดข้อสอบ") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(count, { count = it.filter(Char::isDigit).take(4) }, label = { Text("จำนวนข้อ") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true)
            SettingLabel("จำนวนตัวเลือก")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (2..5).forEach { n -> ChoiceChip("$n ตัวเลือก", optionCount == n) { optionCount = n } }
            }
            SettingLabel("รูปแบบคำตอบ")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip("คำตอบเดียว", !multi) { multi = false }
                ChoiceChip("หลายคำตอบ", multi) { multi = true }
            }
            SettingLabel("รูปแบบกระดาษ")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip("1 คอลัมน์", columns == 1) { columns = 1 }
                ChoiceChip("2 คอลัมน์", columns == 2) { columns = 2 }
                ChoiceChip("3 คอลัมน์", columns == 3) { columns = 3 }
            }
            SettingLabel("จับเวลา")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("เปิดใช้งานตัวจับเวลา", modifier = Modifier.weight(1f), color = Ink)
                Switch(checked = timed, onCheckedChange = { timed = it })
            }
            if (error.isNotBlank()) Text(error, color = Color(0xFFB3261E), fontSize = 12.sp)
        }
        Button(onClick = {
            val c = count.toIntOrNull()
            if (name.isBlank() || c == null || c !in 1..1000) error = "กรุณาระบุชื่อและจำนวนข้อระหว่าง 1–1,000 ข้อ"
            else onCreate(name.trim(), c, optionCount, multi, columns, timed)
        }, modifier = Modifier.fillMaxWidth().padding(20.dp).height(50.dp), shape = RoundedCornerShape(12.dp)) {
            Text("สร้างกระดาษคำตอบ")
        }
    }
}

@Composable
fun SettingLabel(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))
}

@Composable
fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(10.dp),
        color = if (selected) Purple.copy(alpha = .10f) else Color.White,
        border = BorderStroke(1.dp, if (selected) Purple else Line)) {
        Text(label, color = if (selected) Purple else Ink, fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp))
    }
}

@Composable
fun AnswerScreen(exam: Exam, onBack: () -> Unit) {
    val letters = listOf("ก", "ข", "ค", "ง", "จ").take(exam.options)
    var answers by remember(exam.id) { mutableStateOf(mapOf<Int, Set<String>>()) }
    var showSubmit by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Header(exam.name, "${exam.count} ข้อ  •  เลือกได้${if (exam.multi) "หลายคำตอบ" else "คำตอบเดียว"}", onBack)
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("กระดาษคำตอบ", fontWeight = FontWeight.SemiBold)
            Text("${answers.size}/${exam.count} ข้อ", color = Purple, fontSize = 13.sp)
        }
        LazyColumn(Modifier.weight(1f).padding(horizontal = 14.dp), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            items((1..exam.count).toList()) { q ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 8.dp)) {
                    Text(q.toString().padStart(2, '0'), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.width(34.dp))
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        letters.forEach { letter ->
                            val checked = answers[q]?.contains(letter) == true
                            Surface(
                                modifier = Modifier.size(34.dp).clickable(enabled = !submitted) {
                                    val current = answers[q].orEmpty()
                                    val updated = if (checked) current - letter else if (exam.multi) current + letter else setOf(letter)
                                    answers = if (updated.isEmpty()) answers - q else answers + (q to updated)
                                },
                                shape = CircleShape,
                                color = if (checked) Purple else Color.White,
                                border = BorderStroke(1.3.dp, if (checked) Purple else Color(0xFFB8BBC6))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(letter, color = if (checked) Color.White else Ink, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
        Button(onClick = { showSubmit = true }, enabled = !submitted, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).height(48.dp), shape = RoundedCornerShape(12.dp)) {
            Text(if (submitted) "ส่งคำตอบแล้ว" else "ส่งคำตอบ")
        }
    }
    if (showSubmit) {
        AlertDialog(
            onDismissRequest = { showSubmit = false },
            title = { Text("ยืนยันการส่งคำตอบ") },
            text = { Text("ตอบแล้ว ${answers.size} จาก ${exam.count} ข้อ ต้องการส่งคำตอบหรือไม่?") },
            confirmButton = { TextButton(onClick = { submitted = true; showSubmit = false }) { Text("ยืนยัน") } },
            dismissButton = { TextButton(onClick = { showSubmit = false }) { Text("กลับไปตรวจ") } }
        )
    }
}
