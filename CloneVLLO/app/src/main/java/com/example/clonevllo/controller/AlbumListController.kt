package com.example.clonevllo.controller

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.Controller
import com.example.clonevllo.AlbumListRecyclerAdapter
import com.example.clonevllo.UpdateControllerCallBack
import com.example.clonevllo.databinding.ControllerAlbumListBinding
import com.example.clonevllo.model.AlbumDataProvider

/** 앨범 리스트를 보여주기 위한 Controller
 *
 *  리사이클러뷰를 가지고 있으며,
 *  사용자가 리스트 아이템을 클릭하면
 *  TargetController인 VideoController or GifController로
 *  아이템의 정보(AlbumName)가 전달된다
 * */

class AlbumListController(private var data: Bundle) : Controller(),
    UpdateControllerCallBack {
    private lateinit var viewBinding: ControllerAlbumListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        viewBinding = ControllerAlbumListBinding.inflate(inflater, container, false)
        viewBinding.albumListBtn.text = data.getString("name")!!

        initAlbumBtn()
        initRecyclerView()

        return viewBinding.root
    }


    /** AlbumListRecyclerAdapter를 초기화하기 위한 함수
     *
     *  controller가 가지고 있는 mediaType을 가지고
     *  AlbumDataProvider에게 데이터를 요청한 뒤에
     *
     *  adapter의 데이터를 초기화시킨다.
     * */
    private fun initRecyclerView() {
        viewBinding.albumListRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AlbumListRecyclerAdapter(this@AlbumListController)
                .apply {
                    val mediaType = data.getString("mediaType")!!
                    setData(AlbumDataProvider.getListByMediaType(mediaType))
                }
        }
    }

    /** Album 버튼을 초기화 하기 위한 함수
     *
     *  Album 버튼 클릭 시 현재 컨트롤러가 가지고 있는 데이터를 타겟 컨트롤러에게 전달한다.
     *  (VideoController or GifController)
     * */
    private fun initAlbumBtn() {
        viewBinding.albumListBtn.setOnClickListener {
            (targetController as UpdateControllerCallBack).updateTargetController(data.getString("name")!!)
            router.popCurrentController()
        }
    }


    /** UpdateControllerCallback(인터페이스) 함수
     *
     *  사용자가 앨범 리스트에서 아이템을 클릭 시
     *  해당 아이템의 데이터를 타겟 컨트롤러에게 전달한다.
     *  (VideoController or GifController)
     * */
    override fun updateTargetController(albumName: String) {

        //타겟 컨트롤러에게 albumName 전달
        (targetController as UpdateControllerCallBack).updateTargetController(albumName)
        router.popCurrentController()
    }


}
