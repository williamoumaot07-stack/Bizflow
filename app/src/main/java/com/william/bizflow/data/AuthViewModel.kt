package com.william.bizflow.data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.william.bizflow.navigation.Routes

class AuthViewModel(var navController: NavController, var context: Context) {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signup(name: String, email: String, pass: String, confpass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.SIGNUP) { inclusive = true }
                }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logout() {
        mAuth.signOut()
        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.DASHBOARD) { inclusive = true }
        }
    }
}
