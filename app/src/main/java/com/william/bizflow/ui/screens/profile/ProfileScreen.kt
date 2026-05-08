package com.william.bizflow.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.william.bizflow.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    DisposableEffect(userId) {
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users/$userId")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        name = user.name
                        email = user.email
                        imageUrl = user.profileImageUrl
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            }
            userRef.addValueEventListener(listener)
            onDispose { userRef.removeEventListener(listener) }
        } else {
            onDispose {}
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            localImageUri = selectedUri
            isUploading = true
            
            try {
                // Ensure MediaManager is initialized
                try {
                    MediaManager.get()
                } catch (e: Exception) {
                    isUploading = false
                    Toast.makeText(context, "Cloudinary not initialized. Check BizflowApp.kt", Toast.LENGTH_LONG).show()
                    return@let
                }

                // Copy the URI content to a temporary file
                val inputStream: InputStream? = context.contentResolver.openInputStream(selectedUri)
                if (inputStream == null) {
                    isUploading = false
                    Toast.makeText(context, "Failed to read selected image", Toast.LENGTH_SHORT).show()
                    return@let
                }

                val tempFile = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(tempFile)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // Upload the temporary file path to Cloudinary
                val uploadPreset = "profile_pcs" // Change this if your preset name is different
                
                MediaManager.get().upload(tempFile.absolutePath)
                    .unsigned(uploadPreset)
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Handler(Looper.getMainLooper()).post { isUploading = true }
                        }

                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                        override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                            val url = resultData?.get("secure_url")?.toString() ?: ""
                            Handler(Looper.getMainLooper()).post {
                                if (userId != null && url.isNotEmpty()) {
                                    val userRef = FirebaseDatabase.getInstance().getReference("Users/$userId/profileImageUrl")
                                    userRef.setValue(url).addOnCompleteListener { task ->
                                        isUploading = false
                                        tempFile.delete() 
                                        if (task.isSuccessful) {
                                            imageUrl = url
                                            localImageUri = null
                                            Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Database Save Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    isUploading = false
                                    tempFile.delete()
                                    Toast.makeText(context, "Upload success but URL missing", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Handler(Looper.getMainLooper()).post {
                                isUploading = false
                                tempFile.delete()
                                val desc = error?.description ?: "Unknown error"
                                val code = error?.code ?: -1
                                
                                val friendlyHint = when(code) {
                                    400 -> "Check if preset 'profile_pcs' is UNSIGNED"
                                    401 -> "Cloud Name is WRONG or account inactive. Update BizflowApp.kt"
                                    else -> ""
                                }
                                
                                android.util.Log.e("CloudinaryError", "Code: $code, Desc: $desc")
                                Toast.makeText(context, "Error $code: $desc $friendlyHint", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            Handler(Looper.getMainLooper()).post { isUploading = false }
                        }
                    }).dispatch()
            } catch (e: Exception) {
                isUploading = false
                Toast.makeText(context, "System Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A73E8)
                )
            )
        }
    )
{ padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (localImageUri != null || imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = localImageUri ?: imageUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Gray
                    )
                }
                
                if (isUploading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = email, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}
