package com.william.bizflow.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.william.bizflow.R

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Branding
        Image(
            painter = painterResource(R.drawable.login),
            contentDescription = "",
            modifier = Modifier.size(200.dp),
        )

        Text(
            text = "Log in to manage your business",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password
        TextButton(
            onClick = { /* Handle forgot password */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?", color = Color(0xFF1A73E8))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                // Navigate to Dashboard on successful login
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
        ) {
            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Option
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account?", fontSize = 14.sp)
            TextButton(onClick = { /* Navigate to Sign Up */ }) {
                Text("Sign Up", color = Color(0xFF1A73E8), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginPreview() {
    LoginScreen(rememberNavController())
}

