package com.example.clonevllo.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.example.clonevllo.R
import com.example.clonevllo.UpdateControllerCallBack
import com.example.clonevllo.databinding.ControllerVideoBinding
import com.example.clonevllo.model.AlbumDataProvider
import com.google.android.material.tabs.TabLayout


/**
 * 비디오를 생성하기 위한 Controller
 *
 * 탭 레이아웃과 연결되어 1개의 AlbumContentsController 객체를 자식 Controller로 보유
 * 탭 선택마다 자식 Controller의 데이터가 변경됨.
 *
 * */
class VideoController(private var data : Bundle) : Controller(data), UpdateControllerCallBack {

    private lateinit var viewBinding: ControllerVideoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        viewBinding = ControllerVideoBinding.inflate(LayoutInflater.from(container.context), container, false)

        initTabLayout()
        initBackBtn()
        initAlbumBtn()
        setChildController()
        return viewBinding.root
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        viewBinding.videoBtn.text = data.getString("name")
    }


    /**
     *  initialize TabLayout
     *
     *  Tab(전체, 사진, 비디오, GIF) 선택 시 자식 컨트롤러를 새롭게 생성한다.
     * */
    private fun initTabLayout(){
        val tabLayout = viewBinding.videoTab.apply {
            addTab(this.newTab().setText(R.string.all))
            addTab(this.newTab().setText(R.string.photo))
            addTab(this.newTab().setText(R.string.video))
            addTab(this.newTab().setText(R.string.gif))
        }


        //텝레이아웃 리스너 연결
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //탭 선택 시 position값을 mediaType으로 변환
                convertPositionToMediaType(tab!!.position)
                //자식 컨트롤러 데이터 update
                (targetController as AlbumContentsController).updateAdapterData(data)
            }

            //not use
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }

    /**
     * 뒤로 가기 버튼 클릭 시
     * 현재 컨트롤러 제거
     * */
    private fun initBackBtn(){
        viewBinding.videoBack.setOnClickListener {
            router.popCurrentController()
        }
    }

    /**
     * 앨범 버튼 클릭 시
     * 전역에 있는 mediaType, category를 담은 AlbumListController 생성
     * */
    private fun initAlbumBtn(){
        viewBinding.videoBtn.setOnClickListener {
            router.pushController(RouterTransaction.with(AlbumListController(data).apply {
                targetController = this@VideoController
            }).apply {
                pushChangeHandler(VerticalChangeHandler())
                popChangeHandler(VerticalChangeHandler())
            })
        }
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


    /** 자식 컨트롤러 생성 함수
     *
     *  전역에 있는 mediaType과 albumName값을 자식 컨트롤러에게 전달함.
     * */
    private fun setChildController(){
        val controller = AlbumContentsController(data)
        getChildRouter(viewBinding.videoMain).replaceTopController(RouterTransaction.with(controller))

        targetController = controller
    }

    /**
     * 탭 레이아웃의 탭 선택 시
     * 해당 순서에 맞는 mediaType으로 변환
     * */
    private fun convertPositionToMediaType(position: Int){
        when(position){
            0 -> data.apply {
                    remove("mediaType")
                    putString("mediaType", AlbumDataProvider.all)
                }

            1 -> data.apply {
                    remove("mediaType")
                    putString("mediaType", AlbumDataProvider.image)
                }

            2 -> data.apply {
                    remove("mediaType")
                    putString("mediaType", AlbumDataProvider.video)
                }

            3 -> data.apply {
                remove("mediaType")
                putString("mediaType", AlbumDataProvider.gif)
            }
        }
    }




}