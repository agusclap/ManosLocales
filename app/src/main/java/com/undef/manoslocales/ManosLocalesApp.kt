package com.undef.manoslocales

import android.app.Application
import com.cloudinary.android.MediaManager

class ManosLocalesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = mutableMapOf(
            "cloud_name" to "dfmoswu5x",
            "api_key" to "675215553211717",
            "api_secret" to "rFDLF8UqwEby5sKRvbLVuDgGdAY"
        )

        MediaManager.init(this, config)
    }
}
