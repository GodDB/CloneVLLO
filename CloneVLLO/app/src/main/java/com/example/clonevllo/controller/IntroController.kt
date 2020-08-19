package com.example.clonevllo.controller

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.example.clonevllo.R
import com.example.clonevllo.model.AlbumDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 *  인트로 화면을 위한 Controller
 *
 *  이 Controller에서 권한 획득을 실행한다.
 *
 *  권한을 획득하지 못하면 앱 종료(액티비티 종료)
 *  권한을 획득하면 MainController를 router에 setRoot()한다.
 * */
class IntroController : Controller() {

    private val PERMISSION_REQUEST_CODE = 1000

    //-----------------------------------------

    private lateinit var context: Context


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_intro, container, false)
        context = container.context

        //1.5초 대기후 권한 요청
        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            permissionCheck()
        }
        return view
    }

    /**
     * 외부 스토리지를 읽을 권한이 있는지 확인
     *
     * 권한이 있다면, MainController 실행
     * 없다면, 요청
     * */
    private fun permissionCheck() {
        if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            startMainController()
        else
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
    }

    /** 권한 요청 후 결과를 전달 받는 함수
     *
     *  권한을 승인했으면 MainController 실행
     *  거절했다면 앱을 종료한다.
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startMainController()
                else activity?.finish()
            }
        }
    }


    /** MainController 실행을 하는 함수
     *
     *  MainController를 실행하면서 AlbumData들을 가져오기 위해 setUp을 실시한다.
     * */
    private fun startMainController() {
        AlbumDataProvider.setUp(context)

        router.setRoot(
            RouterTransaction.with(MainController()).apply {
                pushChangeHandler(HorizontalChangeHandler())
                popChangeHandler(HorizontalChangeHandler())
            })
    }
}