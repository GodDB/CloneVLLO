package com.example.clonevllo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.example.clonevllo.controller.IntroController

import com.example.clonevllo.model.AlbumDataProvider

/** 메인 액티비티
 *
 *  액티비티 실행 시 IntroController를 router에 push한다.
 * */
class MainActivity : AppCompatActivity() {
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<ViewGroup>(R.id.parent)

        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(IntroController()))
        }
    }


    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }


}