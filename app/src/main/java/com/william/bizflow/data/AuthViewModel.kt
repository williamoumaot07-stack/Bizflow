package com.william.bizflow.data

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.william.bizflow.models.User
import com.william.bizflow.navigation.Routes
import java.util.concurrent.TimeUnit

class AuthViewModel(var navController: NavController, var context: Context) {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private var verificationId: String? = null
        private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    }

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
                val userId = mAuth.currentUser?.uid ?: ""
                val userRef = FirebaseDatabase.getInstance().getReference("Users/$userId")
                val userData = User(name, email, userId, "") // Explicitly initialize with empty profile image URL

                userRef.setValue(userData).addOnCompleteListener { databaseTask ->
                    if (databaseTask.isSuccessful) {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.SIGNUP) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Database Error: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
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

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email in the email field", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendVerificationCode(phoneNumber: String, activity: Activity?, onResult: (Boolean) -> Unit = {}) {
        if (activity == null) {
            Toast.makeText(context, "Internal Error: Activity not found", Toast.LENGTH_SHORT).show()
            onResult(false)
            return
        }

        // ✅ DEVELOPER BYPASS: Use 0700000000 to test without Firebase Console setup
        if (phoneNumber == "0700000000" || phoneNumber == "700000000") {
            verificationId = "test_id"
            Toast.makeText(context, "DEBUG: Using Test Mode (Code: 123456)", Toast.LENGTH_LONG).show()
            navController.navigate(Routes.VERIFY_CODE + "/$phoneNumber")
            onResult(true)
            return
        }

        val cleaned = phoneNumber.replace("\\s".toRegex(), "").removePrefix("0")
        val formattedPhone = if (cleaned.startsWith("+")) cleaned else "+254$cleaned"
        
        Log.d("AuthViewModel", "Attempting code for: $formattedPhone")

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("AuthViewModel", "Verification completed automatically")
                    signInWithPhoneAuthCredential(credential)
                    onResult(true)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("AuthViewModel", "Verification Failed", e)
                    
                    val message = when {
                        e.message?.contains("not allowed") == true -> 
                            "ERROR: Phone Auth is DISABLED in Firebase Console. (Authentication > Sign-in method)"
                        e.message?.contains("format") == true ->
                            "ERROR: Invalid phone number format. Use E.164 (e.g. +254...)"
                        e.message?.contains("internal") == true ->
                            "INTERNAL ERROR: This is usually caused by a mismatch in your google-services.json or missing SHA keys. Check your Logcat for 'AuthViewModel' tags."
                        e.message?.contains("quota") == true ->
                            "QUOTA EXCEEDED: Too many requests. Try again later or use a test number."
                        else -> "Error [${e.javaClass.simpleName}]: ${e.localizedMessage}"
                    }

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    onResult(false)
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = id
                    resendToken = token
                    Log.d("AuthViewModel", "Code sent, ID: $id")
                    Toast.makeText(context, "Code sent to $formattedPhone", Toast.LENGTH_SHORT).show()
                    navController.navigate(Routes.VERIFY_CODE + "/$phoneNumber")
                    onResult(true)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendVerificationCode(phoneNumber: String, activity: Activity?, onResult: (Boolean) -> Unit = {}) {
        if (activity == null || resendToken == null) {
            Toast.makeText(context, "Cannot resend. Please go back and try again.", Toast.LENGTH_SHORT).show()
            onResult(false)
            return
        }
        val formattedPhone = if (phoneNumber.startsWith("+")) phoneNumber else "+254${phoneNumber.removePrefix("0")}"
        
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential, onResult)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("AuthViewModel", "Resend failed", e)
                    Toast.makeText(context, "Resend failed: ${e.message}", Toast.LENGTH_LONG).show()
                    onResult(false)
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = id
                    resendToken = token
                    Toast.makeText(context, "New code sent!", Toast.LENGTH_SHORT).show()
                    onResult(true)
                }
            })
            .setForceResendingToken(resendToken!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(code: String, onResult: (Boolean) -> Unit = {}) {
        if (verificationId == "test_id") {
            if (code == "123456") {
                Toast.makeText(context, "DEBUG: Test Verification Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.RESET_PASSWORD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
                onResult(true)
            } else {
                Toast.makeText(context, "DEBUG: Invalid Test Code. Use 123456", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
            return
        }

        if (verificationId == null) {
            Toast.makeText(context, "Session expired. Please try again.", Toast.LENGTH_SHORT).show()
            onResult(false)
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential, onResult)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, onResult: (Boolean) -> Unit = {}) {
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Verification Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.RESET_PASSWORD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
                onResult(true)
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        }
    }

    fun updatePassword(newPass: String, onResult: (Boolean) -> Unit) {
        val user = mAuth.currentUser
        if (user != null) {
            user.updatePassword(newPass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.RESET_PASSWORD) { inclusive = true }
                    }
                    onResult(true)
                } else {
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }
        } else {
            Toast.makeText(context, "User session not found", Toast.LENGTH_SHORT).show()
            onResult(false)
        }
    }
}
