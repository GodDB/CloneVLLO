package com.example.clonevllo.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.example.clonevllo.UpdateControllerCallBack
import com.example.clonevllo.databinding.ControllerGifBinding

/**
 * GIF를 생성하기 위한 Controller
 *
 * 하나의 자식 Controller 객체를 보유하여
 * 자식 Controller가 Image 화면을 담당함.
 * */

class GifController(private var data : Bundle) : Controller(), UpdateControllerCallBack {
    private lateinit var viewBinding : ControllerGifBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        viewBinding = ControllerGifBinding.inflate(LayoutInflater.from(container.context), container, false)
        initBackBtn()
        initAlbumBtn()
        setChildController()
        return viewBinding.root
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        viewBinding.gifBtn.text = data.getString("name")!!
    }

    /** UpdateControllerCallBack 구현 함수
     *
     *  AlbumListController에서 전달 받은 albumName으로
     *  전역에 있는 data bundle객체를 초기화한다.
     *
     *  또한 TargetController(자식 컨트롤러)에게
     *  전역에 있는 data를 전달해준다. (즉 albumName, mediaType)
     * */
    override fun updateTargetController(albumName: String) {
        data.apply {
            remove("name")
            putString("name", albumName)
        }

        (targetController as AlbumContentsController).updateAdapterData(data)
    }

    /** 자식 컨트롤러 생성 및
     *  자식 컨트롤러에게 데이터를 전달하기 위해 TargetController로 지정
     * */
    private fun setChildController(){
        val controller = AlbumContentsController(data)
        getChildRouter(viewBinding.gifMain).setRoot(RouterTransaction.with(controller))
        targetController = controller
    }

    /**
     * 뒤로 가기 버튼 클릭 시
     * 현재 컨트롤러 제거
     * */
    private fun initBackBtn(){
        viewBinding.gifBack.setOnClickListener {
            router.popController(this)
        }
    }

    /**
     * 앨범 버튼 클릭 시
     * mediaType, category를 담은 AlbumListController생성
     * */
    private fun initAlbumBtn(){
        viewBinding.gifBtn.setOnClickListener {
            router.pushController(RouterTransaction.with(AlbumListController(data).apply {
                targetController = this@GifController
            }).apply {
                pushChangeHandler(VerticalChangeHandler())
                popChangeHandler(VerticalChangeHandler())
            })
        }
    }
}