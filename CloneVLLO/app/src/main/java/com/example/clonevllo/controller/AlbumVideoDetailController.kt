package com.example.clonevllo.controller

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.example.clonevllo.databinding.ControllerAlbumVideoDetailBinding


/** AlbumContents LongClick 시  Video를 보여주기 위한 Controller*/

class AlbumVideoDetailController(private val bundle: Bundle) : Controller() {


    private lateinit var viewBinding: ControllerAlbumVideoDetailBinding
    private lateinit var context: Context


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        context = container.context
        viewBinding = ControllerAlbumVideoDetailBinding.inflate(inflater, container,false)

        loadVideo(bundle.getString("dataPath")!!)
        initCloseBtn()

        return viewBinding.root
    }




    /** 닫기 버튼을 초기화하기 위한 함수
     *
     *  버튼 클릭 시 해당 컨트롤러는 제거된다.
     * */
    private fun initCloseBtn(){
        viewBinding.detailCancelBtn.setOnClickListener {
            router.popCurrentController()
        }
    }

    /** video를 실행 시키기 위한 함수
     *
     * Controller가 가지고 있는 data를 통해
     * 비디오를 실행시킨다.
     */
    private fun loadVideo(dataPath: String) {
        viewBinding.detailVideo.apply {
            keepScreenOn = true
            setVideoURI(Uri.parse(dataPath))
            setOnCompletionListener {
                it.start()
            }
            start()
        }

    }

}