package com.example.clonevllo.controller

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.Group
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.example.clonevllo.OnSingleClickListener
import com.example.clonevllo.R
import com.example.clonevllo.databinding.ControllerMainBinding
import com.example.clonevllo.model.AlbumDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 메인 화면을 담당하는 MainController
 *
 *  첫번째 뷰를 클릭하면 -> VideoController
 *  두번째 뷰를 클릭하면 -> GifController로 이동한다.
 *
 * */
class MainController : Controller() {

    private lateinit var controllerMainBinding: ControllerMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        controllerMainBinding = ControllerMainBinding.inflate(inflater, container, false)

        loadVideo()
        loadGif()

        //비디오 그룹을 클릭 시 VideoController가 실행된다.
        controllerMainBinding.mainVideoGroup.setAllOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val bundle = Bundle().apply {
                    putString("mediaType", AlbumDataProvider.all)
                    putString("name", "All")
                }
                router.pushController(RouterTransaction.with(VideoController(bundle).apply {
                    retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                })
                    .apply {
                        pushChangeHandler(HorizontalChangeHandler())
                        popChangeHandler(HorizontalChangeHandler())
                    }
                )
            }
        })

        //GIF 그룹을 클릭 시 VideoController가 실행된다.
        controllerMainBinding.mainGifGroup.setAllOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val bundle = Bundle().apply {
                    putString("mediaType", AlbumDataProvider.image)
                    putString("name", "All")
                }
                router.pushController(RouterTransaction.with(GifController(bundle).apply {
                    retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
                })
                    .apply {
                        pushChangeHandler(HorizontalChangeHandler())
                        popChangeHandler(HorizontalChangeHandler())
                    }
                )
            }
        })


        return controllerMainBinding.root
    }


    /** VideoController를 실행하기 위한
     *  비디오 뷰에 값을 초기화한다.
     * */
    private fun loadVideo() {
        controllerMainBinding.mainVideo.apply {
            setVideoURI(
                Uri.parse(
                    "android.resource://" + activity!!.packageName + "/" +
                            R.raw.great_video_v2
                )
            )
            setOnCompletionListener {
                it.start()
            }
            start()
        }
    }

    /** GifController를 실행하기 위한
     *  비디오 뷰에 값을 초기화한다.
     * */
    private fun loadGif() {
        controllerMainBinding.mainGif.apply {
            setVideoURI(
                Uri.parse(
                    "android.resource://" + activity!!.packageName + "/" +
                            R.raw.motion_photo_v2
                )
            )
            setOnCompletionListener {
                it.start()
            }
            start()
        }
    }

    /** onResume()시 비디오를 재 실행 및
     *  AlbumDataProvider의 데이터를 reload시킨다.
     * */
    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        controllerMainBinding.mainVideo.start()
        controllerMainBinding.mainGif.start()

        CoroutineScope(Dispatchers.Main).launch {
            AlbumDataProvider.reload()
        }
    }
}

/**
 *  Group에 onClickEvent를 넣어주기 위한 확장함수
 *
 *  Group에 속한 view들에게 인자로 전달받은 리스너를 달아줌
 * */
fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
    this.referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}