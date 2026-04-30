package com.sylvia.back2me.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sylvia.back2me.R
import com.sylvia.back2me.data.AuthViewModel
import com.sylvia.back2me.navigation.ROUT_HOME
import com.sylvia.back2me.ui.theme.newBlue

@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    // Avoid creating ViewModel in preview
    val authViewModel = if (!isPreview) {
        AuthViewModel(navController, context)
    } else null

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // safer for preview
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = "img",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Welcome back!!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.width(350.dp),
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            label = { Text("Email Address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = newBlue,
                focusedBorderColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.width(350.dp),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = newBlue,
                focusedBorderColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authViewModel?.login(email, password)
            },
            colors = ButtonDefaults.buttonColors(newBlue),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Log in")
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = { }) {
            Text(
                text = "Don't have an account? Register",
                fontSize = 15.sp,
                color = newBlue
            )
        }

        TextButton(
            onClick = {
                if (!isPreview) {
                    navController.navigate(ROUT_HOME)
                }
            }
        ) {
            Text(
                text = "Go to Home",
                fontSize = 15.sp,
                color = newBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}