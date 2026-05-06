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

    var username by remember {
        mutableStateOf(if (isPreview) "Preview User" else "Loading...")
    }

    val email = currentUser?.email ?: "No Email Found"

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let {

            imageUri = it

            com.sylvia.back2me.data.CloudinaryManager.uploadImage(
                context = context,
                imageUri = it,
                onSuccess = { imageUrl ->

                    val profileUpdates = userProfileChangeRequest {
                        photoUri = Uri.parse(imageUrl)
                    }

                    currentUser?.updateProfile(profileUpdates)

                    currentUser?.uid?.let { uid ->
                        FirebaseDatabase.getInstance()
                            .getReference("Users/$uid/profileImage")
                            .setValue(imageUrl)
                    }

                    imageUri = Uri.parse(imageUrl)
                },
                onError = { error ->
                    android.widget.Toast.makeText(
                        context,
                        error,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    LaunchedEffect(currentUser) {
        if (!isPreview && currentUser != null) {

            val userRef = FirebaseDatabase.getInstance()
                .getReference("Users/${currentUser.uid}/username")

            userRef.get().addOnSuccessListener { snapshot ->
                username = snapshot.value?.toString()
                    ?: currentUser.displayName
                            ?: "User"
            }

            val imgRef = FirebaseDatabase.getInstance()
                .getReference("Users/${currentUser.uid}/profileImage")

            imgRef.get().addOnSuccessListener {
                val url = it.value?.toString()
                if (url != null) {
                    imageUri = Uri.parse(url)
                } else {
                    currentUser.photoUrl?.let { uri ->
                        imageUri = uri
                    }
                }
            }
        }
    }

    val itemViewModel: LostItemViewModel = viewModel()

    // ✅ FIX: make items reactive
    val allItems = itemViewModel.items
    LaunchedEffect(Unit) {
        itemViewModel.fetchItems(context)
    }
    val userPostsCount by remember(allItems, currentUser) {
        derivedStateOf {
            if (isPreview) {
                3
            } else {
                allItems.count { it.userId == currentUser?.uid }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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

            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.BottomEnd
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(newBlue.copy(alpha = 0.1f))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = newBlue,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(newBlue)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(username, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(email, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("My Activity", fontWeight = FontWeight.Bold, color = newBlue)
                    Text("Total Posts: $userPostsCount", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        if (!isPreview) {
                            auth?.signOut()
                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(0)
                            }
                        }
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}