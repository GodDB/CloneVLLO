package com.example.clonevllo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clonevllo.databinding.ControllerAlbumListItemsBinding
import com.example.clonevllo.model.AlbumListData


/**
 * 앨범 리스트를 위한 리사이클러 뷰 어댑터
 * */
class AlbumListRecyclerAdapter(val callback: UpdateControllerCallBack) :
    RecyclerView.Adapter<AlbumListRecyclerAdapter.VH>() {

    private lateinit var dataList: MutableList<AlbumListData>

    //-----------------------------------------------------------------
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        context = parent.context
        val viewBinding = ControllerAlbumListItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return VH(viewBinding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        // 아이템뷰에 이미지를 담는다.
        // 만약 해당 dataPath가 null(default view)라면
        // placeHolder 이미지를 사용한다.
        Glide
            .with(context)
            .asBitmap()
            .centerInside()
            .placeholder(R.drawable.ic_launcher_foreground)
            .load(dataList[position].dataPath ?: R.drawable.ic_launcher_foreground)
            .into(holder.viewBinding.albumListImg)

        holder.viewBinding.albumListTxt.text = dataList[position].albumName
        holder.viewBinding.albumListCount.text =
            dataList[position].albumCount.toString()

    }

    /** 어댑터의 데이터를 초기화하기 위한 함수
     *  데이터를 초기화 한 뒤에 변경사항을 알리기 위해 notify를 실시한다.
     * */
    fun setData(dataList: MutableList<AlbumListData>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    /** 각 아이템뷰를 소유하고 있는 뷰홀더 클래스
     *
     *  뷰 홀더 객체 생성 시
     *  해당 뷰에 onClickEvent를 달아준다.
     * */
    inner class VH(val viewBinding : ControllerAlbumListItemsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        init {
            viewBinding.albumListParent.setOnClickListener {
                callback.updateTargetController(dataList[adapterPosition].albumName)
            }
        }
    }
}