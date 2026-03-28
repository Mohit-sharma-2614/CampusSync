//package com.example.campussync.persentation.notice
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Refresh
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.RadioButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import dagger.hilt.android.lifecycle.HiltViewModel
//
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NoticeScreen(
//    onBackClick: () -> Unit = {},
//    viewModel: NoticeViewModel = hiltViewModel()
//) {
//    val isTeacher = viewModel.isTeacher.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Notices") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { innerPadding->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            if (isTeacher.value) {
//                TeacherCreateNoticeScreen(viewModel)
//            } else {
//                StudentNoticeFeedScreen(viewModel)
//            }
//        }
//    }
//}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TeacherCreateNoticeScreen(viewModel: NoticeViewModel) {
//    var title by remember { mutableStateOf("") }
//    var content by remember { mutableStateOf("") }
//    var selectedScope by remember { mutableStateOf(NoticeScope.COLLEGE) }
//    var targetDept by remember { mutableStateOf("CS") }
//
//    val sendStatus by viewModel.sendingStatus.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Create New Notice", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = title,
//            onValueChange = { title = it },
//            label = { Text("Title") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = content,
//            onValueChange = { content = it },
//            label = { Text("Message Content") },
//            modifier = Modifier.fillMaxWidth().height(120.dp),
//            maxLines = 5
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Scope Selection
//        Text("Target Audience:", style = MaterialTheme.typography.labelLarge)
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            RadioButton(
//                selected = selectedScope == NoticeScope.COLLEGE,
//                onClick = { selectedScope = NoticeScope.COLLEGE }
//            )
//            Text("Entire College")
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            RadioButton(
//                selected = selectedScope == NoticeScope.DEPARTMENT,
//                onClick = { selectedScope = NoticeScope.DEPARTMENT }
//            )
//            Text("Department Only")
//        }
//
//        if (selectedScope == NoticeScope.DEPARTMENT) {
//            OutlinedTextField(
//                value = targetDept,
//                onValueChange = { targetDept = it },
//                label = { Text("Dept Code (e.g., CS, MECH)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = { viewModel.sendNotice(title, content, selectedScope, if(selectedScope == NoticeScope.DEPARTMENT) targetDept else null) },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = title.isNotBlank() && content.isNotBlank()
//        ) {
//            Icon(Icons.Default.Add, contentDescription = null)
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Publish Notice")
//        }
//
//        if (sendStatus != null) {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = sendStatus!!,
//                color = if (sendStatus!!.contains("Error")) Color.Red else Color(0xFF007A33)
//            )
//        }
//    }
//}
//
//@Composable
//fun StudentNoticeFeedScreen(viewModel: NoticeViewModel) {
//    val notices by viewModel.notices.collectAsState()
//
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(onClick = { viewModel.syncNotices() }) {
//                Icon(Icons.Default.Refresh, "Sync")
//            }
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .background(Color(0xFFF5F5F5))
//        ) {
//            Text(
//                text = "Notice Board",
//                style = MaterialTheme.typography.headlineLarge,
//                modifier = Modifier.padding(16.dp)
//            )
//
//            if (notices.isEmpty()) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Text("No notices yet. Connect to WiFi!", color = Color.Gray)
//                }
//            } else {
//                LazyColumn(
//                    contentPadding = PaddingValues(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    items(notices) { notice ->
//                        NoticeCard(notice)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun NoticeCard(notice: Notice) {
//    Card(
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                // Category Badge
//                Surface(
//                    color = if (notice.scope == NoticeScope.COLLEGE) Color(0xFFE3F2FD) else Color(0xFFFFF3E0),
//                    shape = RoundedCornerShape(8.dp),
//                    modifier = Modifier.padding(bottom = 8.dp)
//                ) {
//                    Text(
//                        text = if (notice.scope == NoticeScope.COLLEGE) "COLLEGE" else "DEPT: ${notice.targetDepartment}",
//                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//                        style = MaterialTheme.typography.labelSmall,
//                        color = if (notice.scope == NoticeScope.COLLEGE) Color(0xFF1565C0) else Color(0xFFE65100),
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                Text(
//                    text = formatTime(notice.timestamp), // Helper function needed
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray
//                )
//            }
//
//            Text(
//                text = notice.title,
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 4.dp)
//            )
//
//            Text(
//                text = notice.content,
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.DarkGray
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "- ${notice.authorName}",
//                style = MaterialTheme.typography.labelMedium,
//                color = Color.Gray,
//                modifier = Modifier.align(Alignment.End)
//            )
//        }
//    }
//}
//
//fun formatTime(isoString: String): String {
//    // Basic formatting - replace with your preferred DateTimeFormatter
//    return try {
//        isoString.substring(0, 10) + " " + isoString.substring(11, 16)
//    } catch (e: Exception) {
//        "Just now"
//    }
//}