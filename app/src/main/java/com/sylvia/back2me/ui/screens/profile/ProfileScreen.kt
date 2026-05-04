package com.sylvia.back2me.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.sylvia.back2me.data.LostItemViewModel
import com.sylvia.back2me.navigation.ROUTE_LOGIN
import com.sylvia.back2me.ui.theme.newBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val auth = if (!isPreview) FirebaseAuth.getInstance() else null
    val currentUser = auth?.currentUser

    var username by remember { mutableStateOf(if (isPreview) "Preview User" else "Loading...") }
    val email = if (isPreview) "preview@back2me.com" else (currentUser?.email ?: "No Email Found")

    // 🔥 1. State for the selected Image URI
    var imageUri by remember { mutableStateOf<Uri?>(currentUser?.photoUrl) }

    // 🔥 2. Image Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            // Optional: Save this URI to Firebase Auth profile
            val profileUpdates = userProfileChangeRequest { photoUri = it }
            currentUser?.updateProfile(profileUpdates)
        }
    }

    LaunchedEffect(currentUser) {
        if (!isPreview && currentUser != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users/${currentUser.uid}/username")
            userRef.get().addOnSuccessListener { snapshot ->
                username = if (snapshot.exists()) snapshot.value.toString() else currentUser.displayName ?: "User"
            }
        }
    }

    val itemViewModel: LostItemViewModel = viewModel()
    val allItems = itemViewModel.items
    val userPostsCount = if (isPreview) 3 else {
        remember(allItems) { allItems.count { it.userId == currentUser?.uid } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = newBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 3. Clickable Profile Picture with "Edit" Icon
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clickable { launcher.launch("image/*") }, // Opens Gallery
                contentAlignment = Alignment.BottomEnd
            ) {
                // The actual Image/Icon Circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(newBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = newBlue,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                // Small "Edit" Camera badge
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(newBlue)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Picture",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = username, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = email, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(30.dp))

            // ... (Rest of your Card and Logout button code)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("My Activity", fontWeight = FontWeight.Bold, color = newBlue)
                    Text("Total Posts: $userPostsCount", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { /* Logout logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}