package com.byexoplayer.music.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * @ProjectName : 简易音乐
 * @Author :huaweikai
 * @Time : 2021/9/8  23:18
 * @Description : 用于数据库存储不了的数据，进行转换
 */
object Convert {
    @TypeConverter
    fun toUri(url:String): Uri {
        return Uri.parse(url)
    }
    @TypeConverter
    fun fromUri(uri: Uri):String{
        return uri.toString()
    }


    @TypeConverter
    fun toBitmap(bytes:ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap):ByteArray{
        val outputStream= ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }
}