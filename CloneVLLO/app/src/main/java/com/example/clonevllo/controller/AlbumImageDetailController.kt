package com.example.clonevllo.controller

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.clonevllo.databinding.ControllerAlbumImageDetailBinding
import com.example.clonevllo.model.AlbumDataProvider

/** AlbumContents LongClick 시
 * image, gif를 보여주기 위한 Controller
 * */
class AlbumImageDetailController(private val bundle: Bundle) : Controller() {

    private lateinit var viewBinding : ControllerAlbumImageDetailBinding
    private lateinit var context : Context


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        context = container.context
        viewBinding = ControllerAlbumImageDetailBinding.inflate(LayoutInflater.from(context), container, false)

        initCloseBtn()
        classifyImageAndGIF(bundle.getString("dataPath")!!, bundle.getString("mediaType")!!)
        return viewBinding.root
    }


    /** 닫기 버튼을 초기화하기 위한 함수
     *
     *  버튼 클릭 시 해당 컨트롤러가 제거된다.
     * */
    private fun initCloseBtn(){
        viewBinding.detailCancelBtn.setOnClickListener {
            router.popCurrentController()
        }

    }

    /** 전달 받은 데이터가 Image인지, Gif인지 분류하기 위한 함수
     *
     *  분류에 따라 적합한 glide 객체를 선택한다.
     * */
    private fun classifyImageAndGIF(dataPath : String, mediaType : String){
        when(mediaType){
            //이미지
            AlbumDataProvider.image -> {
                Glide
                    .with(context)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .load(dataPath)
                    .into(viewBinding.detailIv)
            }
            //GIF
            AlbumDataProvider.gif -> {
                Glide
                    .with(context)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .load(dataPath)
                    .into(viewBinding.detailIv)
            }
        }
    }
}