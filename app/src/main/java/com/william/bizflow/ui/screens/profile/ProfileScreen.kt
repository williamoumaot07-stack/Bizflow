package com.william.bizflow.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
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
import com.william.bizflow.navigation.Routes
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                try {
                    MediaManager.get()
                } catch (_: Exception) {
                    isUploading = false
                    Toast.makeText(context, "Cloudinary not initialized", Toast.LENGTH_LONG).show()
                    return@let
                }

                val inputStream: InputStream? = context.contentResolver.openInputStream(selectedUri)
                if (inputStream == null) {
                    isUploading = false
                    return@let
                }

                val tempFile = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(tempFile)
                inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }

                val uploadPreset = "profile_pcs"
                
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
                                    userRef.setValue(url).addOnCompleteListener {
                                        isUploading = false
                                        tempFile.delete()
                                        if (it.isSuccessful) imageUrl = url
                                    }
                                } else {
                                    isUploading = false
                                    tempFile.delete()
                                }
                            }
                        }
                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Handler(Looper.getMainLooper()).post {
                                isUploading = false
                                tempFile.delete()
                                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    }).dispatch()
            } catch (e: Exception) {
                isUploading = false
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout Confirmation", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to log out of BizFlow?", fontWeight = FontWeight.Medium) },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        navController.navigate(Routes.LOGIN) { popUpTo(0) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Logout", color = Color.White, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF1A237E), fontWeight = FontWeight.Black)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareText = "Business Profile: $name\nContact: $email\nSent via BizFlow"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Profile"))
                    }) {
                        Icon(Icons.Default.Share, "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F3F4))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = localImageUri ?: imageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (imageUrl.isEmpty() && localImageUri == null) {
                    Icon(Icons.Default.CameraAlt, "Add Photo", modifier = Modifier.size(40.dp), tint = Color(0xFF1A237E))
                }
                if (isUploading) CircularProgressIndicator(color = Color(0xFF1A237E), modifier = Modifier.size(50.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Editable Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Business Owner Name", fontWeight = FontWeight.Bold, color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Read-only Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email Address (Permanent)", fontWeight = FontWeight.Bold, color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.LightGray,
                    disabledTextColor = Color.White,
                    disabledLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.trim().isEmpty()) {
                        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSaving = true
                    val userRef = FirebaseDatabase.getInstance().getReference("Users/$userId/name")
                    userRef.setValue(name).addOnCompleteListener {
                        isSaving = false
                        if (it.isSuccessful) Toast.makeText(context, "Changes Saved!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Save, null, Modifier.padding(end = 8.dp), tint = Color.White)
                    Text("Save Profile Changes", fontWeight = FontWeight.Black, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout from BizFlow", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
