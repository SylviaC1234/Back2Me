package com.sylvia.back2me.ui.screens.add

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sylvia.back2me.data.LostItemViewModel
import com.sylvia.back2me.models.LostItem
import com.sylvia.back2me.navigation.ROUT_HOME
import com.sylvia.back2me.ui.theme.newBlue

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(navController: NavController) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    // ✅ SHARED VIEWMODEL: Same scoping as HomeScreen
    val viewModel: LostItemViewModel = if (!isPreview) {
        val backStackEntry = remember(navController) { navController.getBackStackEntry(ROUT_HOME) }
        viewModel(backStackEntry)
    } else {
        viewModel()
    }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Lost") }
    var location by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Lost/Found Item") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = newBlue, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Card(modifier = Modifier.fillMaxWidth().height(200.dp).clickable { launcher.launch("image/*") }) {
                Box(Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Text("Tap to upload image", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Spacer(Modifier.height(16.dp))
            Text("Select Type:", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold)
            Row {
                Button(onClick = { type = "Lost" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "Lost") Color.Red else Color.LightGray)) { Text("Lost") }
                Spacer(Modifier.width(12.dp))
                Button(onClick = { type = "Found" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "Found") Color(0xFF2E7D32) else Color.LightGray)) { Text("Found") }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Info") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(24.dp))

            if (isUploading) {
                CircularProgressIndicator(color = newBlue)
            } else {
                Button(
                    onClick = {
                        if (title.isBlank() || location.isBlank()) return@Button
                        isUploading = true

                        val newItem = LostItem(
                            id = System.currentTimeMillis().toString(),
                            title = title, type = type, location = location,
                            description = description, contact = contact,
                            imageUrl = imageUri?.toString() ?: ""
                        )

                        // ✅ IMMEDIATE UPDATE
                        viewModel.addItem(newItem)

                        // ✅ BACKEND UPLOAD
                        viewModel.uploadItem(imageUri, title, type, location, description, contact, context) {
                            isUploading = false
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = newBlue)
                ) { Text("Post Item", color = Color.White) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() {
    AddItemScreen(rememberNavController())
}