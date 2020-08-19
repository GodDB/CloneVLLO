package com.example.clonevllo

import android.os.Bundle


interface UpdateControllerCallBack {
    fun updateTargetController(albumName : String)
}

interface PushControllerCallback{
    fun pushController(albumPath : String, mediaType : String)
}