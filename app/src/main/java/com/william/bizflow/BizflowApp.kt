package com.william.bizflow

import android.app.Application
import android.util.Log
import com.cloudinary.android.MediaManager

class BizflowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // IMPORTANT: The 401 error means your cloud_name is incorrect.
        // Go to https://cloudinary.com/console and copy the "Cloud name"
        val cloudName = "dxkqcdqtk" // <--- UPDATE THIS STRING WITH YOUR CLOUD NAME
        
        val config = mapOf(
            "cloud_name" to cloudName,
            "secure" to true
        )
        
        try {
            MediaManager.init(this, config)
            Log.d("BizflowApp", "Cloudinary initialized successfully with cloud: $cloudName")
        } catch (e: IllegalStateException) {
            Log.w("BizflowApp", "Cloudinary already initialized")
        } catch (e: Exception) {
            Log.e("BizflowApp", "Cloudinary init failed: ${e.message}")
        }
    }
}
