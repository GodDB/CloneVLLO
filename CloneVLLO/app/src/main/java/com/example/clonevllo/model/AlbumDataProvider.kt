@file:Suppress("DEPRECATION")

package com.example.clonevllo.model

import android.content.Context

import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 *  사용자의 앨범 데이터 (비디오, 이미지, Gif)를 제공하기 위한 Singleton Object
 *
 *  이 객체는 setUp()을 호출한 후에 사용해야 한다.
 *
 *  getDataByMediaTypeAndName()을 통해 앨범 컨텐츠 데이터를 전달 받을 수 있고,
 *  getListByMediaType()를 통해 앨범 리스트 데이터를 전달 받을 수 있다.
 * */
object AlbumDataProvider {
    //mediaType name
    const val all = "all"
    const val image = "image"
    const val video = "video"
    const val gif = "gif"


    //mediaType dataSet
    private lateinit var allDataMap: TreeMap<String, MutableList<AlbumData>>
    private lateinit var imageDataMap: TreeMap<String, MutableList<AlbumData>>
    private lateinit var videoDataMap: TreeMap<String, MutableList<AlbumData>>
    private lateinit var gifDataMap: TreeMap<String, MutableList<AlbumData>>

    // -----------------------------------------

    private lateinit var context: Context


    /** 객체 사용시 최초 1회 호출해야 하는 함수
     *
     *  이 함수를 통해 context를 초기화하고
     *  reload()함수를 통해 dataSet을 초기화 시킨다.
     * */
    fun setUp(context: Context) {
        this.context = context

        CoroutineScope(Dispatchers.Main).launch {
            reload()
        }
    }

    /** 사용자 갤러리로부터 데이터를 가져오기 위한 함수
     *  코루틴으로 호출해야 한다.
     *
     *  setUp()이나 액티비티가 onResume()되었을 때 사용된다.
     * */
    suspend fun reload() {
        withContext(Dispatchers.IO) {
            initAlbumData(context)
        }
    }

    /** mediaType과 앨범 이름에 맞는 데이터를 전달 받기 위한 함수
     *
     *  인자로 mediaType과 name을 전달해
     *  MutableList형태로 데이터를 전달 받는다
     *
     *  만약 해당 name의 데이터가 없는 경우
     *  빈 리스트를 전달 받는다.
     * */
    fun getDataByMediaTypeAndName(
        mediaType: String,
        name: String
    ): MutableList<AlbumData> {
        return when (mediaType) {
            all -> allDataMap[name]?.toMutableList()
                ?: mutableListOf() //원본 데이터를 보장하기 위함. (encapsulation)
            image -> imageDataMap[name]?.toMutableList() ?: mutableListOf()
            video -> videoDataMap[name]?.toMutableList() ?: mutableListOf()
            else -> gifDataMap[name]?.toMutableList() ?: mutableListOf()
        }
    }


    /** mediaType에 맞는 앨범 리스트를 전달 받기 위한 함수
     *
     *  인자로 mediaType을 전달해
     *  MutableList형태로 데이터를 전달 받는다
     *
     * */
    fun getListByMediaType(
        mediaType: String
    ): MutableList<AlbumListData> {
        val dataMap =
            when (mediaType) {
                all -> allDataMap
                image -> imageDataMap
                video -> videoDataMap
                else -> gifDataMap
            }

        return mutableListOf<AlbumListData>().apply {
            dataMap.forEach { (key, value) ->
                //해당 앨범이름의 데이터가 없다면 임의의 데이터를 하나 추가
                if (value.size == 0) this.add(AlbumListData(null))
                //해당 앨범이름의 데이터가 있다면 첫번째 데이터를 담음
                else this.add(AlbumListData(value[0].dataPath, key, value.size))
            }
        }
    }

    /**
     *  사용자의 모든 데이터를 가져오기 위한 함수
     *
     *  이미지, gif, 비디오 데이터를 가져와
     *  필드 맵을 초기화 시킨다.
     * */

    private fun initAlbumData(context: Context) {

        val dataMaps: MutableList<TreeMap<String, MutableList<AlbumData>>> = MutableList(4) {
            TreeMap<String, MutableList<AlbumData>>().apply{
                put("All", mutableListOf())
            }
        }


        val resolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Video.VideoColumns.DURATION,
            MediaStore.Files.FileColumns.MIME_TYPE
        )

        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        val queryUri: Uri = MediaStore.Files.getContentUri("external")

        val cursor = resolver.query(
            queryUri,
            projection,
            selection,
            null,  // Selection args (none).
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        )
        cursor?.use {
            while (it.moveToNext()) {
                val path = it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                val duration =
                    formatDuration(it.getLong(it.getColumnIndex(MediaStore.Video.VideoColumns.DURATION)))
                val category =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val mediaType =
                    mimeTypeConverter(it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)))

                val dataMap = when (mediaType) {
                    image -> dataMaps[1]
                    video -> dataMaps[2]
                    else -> dataMaps[3]
                }
                if (dataMap[category] == null) dataMap[category] = mutableListOf()
                dataMap[category]!!.add(AlbumData(path, duration, mediaType, category))
                dataMap["All"]!!.add(AlbumData(path, duration, mediaType, category))

                if (dataMaps[0][category] == null) dataMaps[0][category] = mutableListOf()
                dataMaps[0][category]!!.add(AlbumData(path, duration, mediaType, category))
                dataMaps[0]["All"]!!.add(AlbumData(path, duration, mediaType, category))
            }
            it.close()
        }

        allDataMap = dataMaps[0]
        imageDataMap = dataMaps[1]
        videoDataMap = dataMaps[2]
        gifDataMap = dataMaps[3]
    }


    /**
     *  mimeType to MediaType
     *  (ex: image/gif  -> gif,  image/jpg  -> image)
     *
     * */
    private fun mimeTypeConverter(mimeType: String): String {
        return when {
            mimeType.contains("video") -> video
            mimeType.contains("image") -> {
                if (mimeType.contains("gif")) gif
                else image
            }
            else -> ""
        }
    }

    /**
     *  miliSeconds to seconds
     *  (ex: 23939  -> 00:23,  239390000 -> 2:39:00)
     *
     * */
    private fun formatDuration(miliSeconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(miliSeconds).toInt() % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds).toInt() % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds).toInt() % 60
        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
            seconds > 0 -> String.format("00:%02d", seconds)
            else -> "00:00"
        }
    }


}
