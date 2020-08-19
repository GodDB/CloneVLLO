package com.example.clonevllo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.clonevllo.databinding.ControllerAlbumContentsItemBinding
import com.example.clonevllo.model.AlbumData
import com.example.clonevllo.model.AlbumDataProvider

/**
 * 앨범 Contents를 위한 리사이클러 뷰 어댑터
 * */

class AlbumRecyclerAdapter(val callback: PushControllerCallback) :
    RecyclerView.Adapter<AlbumRecyclerAdapter.VH>() {

    private lateinit var dataList: MutableList<AlbumData>

    //-------------------------------------------------------

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        context = parent.context
        val viewBinding =
            ControllerAlbumContentsItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return VH(viewBinding)
    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    override fun onBindViewHolder(holder: VH, position: Int) {

        // 아이템뷰에 이미지를 담는다.
        // 만약 해당 dataPath가 null(default view)라면
        // empty이미지를 사용한다.
        Glide
            .with(context)
            .asBitmap()
            .override(200, 200)
            .placeholder(R.drawable.ic_launcher_foreground)
            .load(dataList[position].dataPath ?: R.drawable.empty)
            .into(holder.viewBinding.albumItem)


        //mediaType이 video일때만 durationBar visible
        if (dataList[position].mediaType == AlbumDataProvider.video) {
            holder.viewBinding.albumItemDurationBar.apply {
                text = dataList[position].duration
                visibility = View.VISIBLE
            }
        } else holder.viewBinding.albumItemDurationBar.apply {
            visibility = View.GONE
        }
    }

    /** 어댑터의 데이터를 초기화하기 위한 함수
     *
     *  default item을 하나 추가한다. (빈 장면 넣기)
     *  데이터를 초기화 한 뒤에 변경사항을 알리기 위해 notify를 실시한다.
     * */
    fun setData(dataList: MutableList<AlbumData>) {
        //default item(빈 장면 넣기)
        this.dataList = dataList.apply {
            add(0, AlbumData(null))
        }
        notifyDataSetChanged()
    }

    /** 각 아이템뷰를 소유하고 있는 뷰홀더 클래스
     *  뷰 홀더 객체 생성 시
     *  해당 뷰에 longClickEvent를 달아준다.
     * */
    inner class VH(val viewBinding: ControllerAlbumContentsItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        init {
            viewBinding.albumItem.setOnLongClickListener {
                //default item인 0번만 아니면 longClick 작동
                if(adapterPosition != 0){
                    callback.pushController(
                        dataList[adapterPosition].dataPath!!,
                        dataList[adapterPosition].mediaType
                    )
                    true
                }
                false
            }

        }
    }
}