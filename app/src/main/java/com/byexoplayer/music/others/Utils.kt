package com.byexoplayer.music.others

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.byexoplayer.music.db.AlbumItem
import com.byexoplayer.music.db.ArtistItem

import com.byexoplayer.music.db.MusicItem
import com.byexoplayer.music.others.Constants.ALBUM_CONTENT_URI
import com.google.android.exoplayer2.MediaItem
import com.permissionx.guolindev.PermissionX
import java.util.concurrent.TimeUnit

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  11:39
 * @Description : 工具类，获取权限和扫描本地音乐
 */


object Utils {

    fun hasPermission(context: Context)=PermissionX.isGranted(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun getPermission(activity:AppCompatActivity){
        PermissionX.init(activity)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onExplainRequestReason{scope,deniedList->
                scope.showRequestReasonDialog(deniedList,"需要这个权限才能扫描本地音乐","OK","Cancel")
            }
            .request{allGranted,_,deniedList->
                if(allGranted){
                    Toast.makeText(activity.application,"读取权限已获取",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(activity.application,"这些权限获取失败$deniedList",Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getAllAudio(context: Context):ArrayList<MusicItem>{
        val list:ArrayList<MusicItem> = arrayListOf()
        val uri =MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val project= arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Albums.ALBUM_ID
        )
        val cursor=context.contentResolver.query(uri,project,null,null,null)
        cursor?.let {
            while(it.moveToNext()){
//                val album=it.getString(0)
                val title=it.getString(1)
                val duration=it.getLong(2)
                val artist=it.getString(3)
                val musicId=it.getLong(4)
                val size=it.getLong(5)
                val url=ContentUris.withAppendedId(uri,musicId)
                val albumId=it.getLong(6)
                val album_uri= ContentUris.withAppendedId(
                    Uri.parse(ALBUM_CONTENT_URI), albumId)
                if(size>10*100000){
                    list.add(MusicItem(title, artist,musicId.toString(),url,duration,album_uri))
                }
            }
            it.close()
        }
        return list
    }

    fun getAlbumLoader(context: Context):ArrayList<AlbumItem>{
        val list:ArrayList<AlbumItem> = arrayListOf()
        val cUri=MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val project= arrayOf(
            MediaStore.Audio.Albums.ALBUM_ID,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.FIRST_YEAR
        )
        val cursor=context.contentResolver.query(cUri,project,null,null,null)
        cursor?.let {
            while (it.moveToNext()){
                val albumId=it.getLong(0)
                val artistName=it.getString(1)
                val songCount=it.getInt(3)
                val albumTitle=it.getString(4)
                val year=it.getInt(5)
                val albumImg=ContentUris.withAppendedId(
                    Uri.parse(ALBUM_CONTENT_URI), albumId)
                list.add(AlbumItem(albumId.toString(),artistName,songCount,albumTitle,year,albumImg))
            }
            it.close()
        }
        return list
    }

    fun getArtistLoader(context: Context):ArrayList<ArtistItem>{
        val list:ArrayList<ArtistItem> = arrayListOf()
        val cUri=MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
        val project= arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )
        val cursor=context.contentResolver.query(cUri,project,null,null,null)
        cursor?.let {
            while (it.moveToNext()){
                val artistId=it.getLong(0)
                val artistName=it.getString(1)
                val albumCount=it.getInt(2)
                val songCount=it.getInt(3)
                list.add(ArtistItem(albumCount, artistId.toString(), artistName, songCount))
            }
          it.close()
        }
        return list
    }

    fun getAudioByAlbum(context: Context,id:String):ArrayList<MusicItem>{
        val list:ArrayList<MusicItem> = arrayListOf()
        val uri =MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val project= arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Albums.ALBUM_ID
        )
        val cursor=context.contentResolver.query(uri,project,MediaStore.Audio.Albums.ALBUM_ID+"=$id",null,null)
        cursor?.let {
            while(it.moveToNext()){
//                val album=it.getString(0)
                val title=it.getString(1)
                val duration=it.getLong(2)
                val artist=it.getString(3)
                val musicId=it.getLong(4)
                val size=it.getLong(5)
                val url=ContentUris.withAppendedId(uri,musicId)
                val albumId=it.getLong(6)
                val album_uri= ContentUris.withAppendedId(
                    Uri.parse(ALBUM_CONTENT_URI), albumId)
                if(size>10*100000){
                    list.add(MusicItem(title, artist,musicId.toString(),url,duration,album_uri))
                }
            }
            it.close()
        }
        return list
    }
    fun mediaItemToMeta(queueItem: MediaSessionCompat.QueueItem?):MediaMetadataCompat{
        val duration=queueItem?.description?.extras?.getLong("duration")?:0
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, queueItem?.description?.subtitle.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, queueItem?.description?.mediaId.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, queueItem?.description?.title.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, queueItem?.description?.title.toString())
            .putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                queueItem?.description?.iconUri.toString()
            )
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,duration)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, queueItem?.description?.mediaUri.toString())
            .putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                queueItem?.description?.iconUri.toString()
            )
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, queueItem?.description?.subtitle.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, queueItem?.description.toString())
            .build()
    }

    fun getTimeFromDuration(ms: Long, includeMillis:Boolean=false):String{
        var milliseconds=ms
        //不必要担心值会混乱，它会主动的将已计算的值删去，就像计算三位数的个十百位一样。
        val hours= TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds-= TimeUnit.HOURS.toMillis(hours)

        val minutes= TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds-= TimeUnit.MINUTES.toMillis(minutes)

        val seconds= TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if(!includeMillis){
            return "${if(hours<10) "0" else ""}$hours:"+
                    "${if(minutes<10)"0" else ""}$minutes:"+
                    "${if(seconds<10)"0" else "" }$seconds"
        }
        milliseconds-= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds/=10
        return "${if(hours<10) "0" else ""}$hours:"+
                "${if(minutes<10)"0" else ""}$minutes:"+
                "${if(seconds<10)"0" else "" }$seconds:"+
                "${if(milliseconds<10)"0" else ""}$milliseconds"
    }
}