package com.example.clonevllo.controller

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.example.clonevllo.AlbumRecyclerAdapter
import com.example.clonevllo.PushControllerCallback
import com.example.clonevllo.RecyclerDecoration
import com.example.clonevllo.databinding.ControllerAlbumContetnsBinding
import com.example.clonevllo.model.AlbumDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  GifController와 VideoController의 자식 Controller로,
 *  사용자의 앨범 데이터들을 RecyclerView에 나타낸다.
 * */
class AlbumContentsController(private var data: Bundle) : Controller(data), PushControllerCallback {

    private lateinit var viewBinding: ControllerAlbumContetnsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        viewBinding = ControllerAlbumContetnsBinding.inflate(inflater, container, false)
        initRecyclerView()
        return viewBinding.root
    }

    /** 부모 컨트롤러로 부터 데이터를 전달 받기 위한 함수(GifController or VideoController)
     *
     *  전달 받은 데이터로 전역 데이터를 갱신한 후
     *  AlbumDataProvider에게 데이터를 전달 받아
     *  RecyclerView Adapter의 데이터를 갱신한다.
     * */
    fun updateAdapterData(data: Bundle) {
        this.data = data

        (viewBinding.contentsRecyclerView.adapter as AlbumRecyclerAdapter).apply {
            setData(
                AlbumDataProvider.getDataByMediaTypeAndName(
                    data.getString("mediaType")!!,
                    data.getString("name")!!
                )
            )
        }
    }

    /** AlbumRecyclerAdapter 객체를 초기화하기 위한 함수
     *
     *  현재 controller에서 지니고 있는 mediaType, AlbumName값을 통해
     *  AlbumDataProvider에게 데이터를 전달받은 뒤
     *
     *  recyclerAdapter에게 데이터를 전달한다.
     * */
    private fun initRecyclerView() {
        viewBinding.contentsRecyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = AlbumRecyclerAdapter(this@AlbumContentsController)
                .apply {
                    setData(
                        AlbumDataProvider.getDataByMediaTypeAndName(
                            data.getString("mediaType")!!,
                            data.getString("name")!!
                        )
                    )
                }

            val dpi: Float = resources.displayMetrics.density
            addItemDecoration(RecyclerDecoration(10 * dpi.toInt()))
        }
    }

    /** PushControllerCallBack(인터페이스)의 함수
     *
     *  RecyclerView Adapter에게 전달되어
     *  Item LongClick시 호출된다.
     *
     *  전달 받은 앨범 데이터 경로와 mediaType값을 담아
     *  DetailController를 호출한다.
     * */
    override fun pushController(dataPath: String, mediaType: String) {
        val bundle = Bundle().apply {
            putString("dataPath", dataPath)
            putString("mediaType", mediaType)
        }

        //mediaType이 video면 AlbumVideoDetailController 호출
        //mediaType이 video가 아니면 AlbumImageDetailController 호출
        val controller =
            if (mediaType == AlbumDataProvider.video) AlbumVideoDetailController(bundle)
            else AlbumImageDetailController(bundle)

        parentController?.router?.pushController(RouterTransaction.with(controller).apply {
            pushChangeHandler(FadeChangeHandler())
            popChangeHandler(FadeChangeHandler())
        })
    }

    /** 액티비티 onResume()시 호출되는 함수
     *
     *  onResume()시 사용자의 갤러리 데이터 변화를 동기화 시키기 위해
     *  reLoad()호출
     * */
    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        reLoad()
    }

    /** 사용자의 갤러리 데이터 변화를 동기화 시키기 위한 함수
     *
     *  AlbumDataProvider 객체를 reLoad 시킨 뒤
     *  다시 mediaType 데이터를 가져온다.
     * */
    private fun reLoad() {
        val adapter =
            viewBinding.contentsRecyclerView.adapter as AlbumRecyclerAdapter

        val mediaType = data.getString("mediaType")!!
        val name = data.getString("name")!!

        CoroutineScope(Dispatchers.Main).launch {
            AlbumDataProvider.reload()
            adapter.setData(AlbumDataProvider.getDataByMediaTypeAndName(mediaType, name))
        }

    }


}