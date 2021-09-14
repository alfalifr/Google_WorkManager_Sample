package com.example.background

import android.content.Context
import android.graphics.BitmapFactory
import android.icu.text.CaseMap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.workers.makeStatusNotification
import java.text.SimpleDateFormat
import java.util.*

class SaveToFileWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

  private val title = "Blurred Image"
  private val dateFormatter = SimpleDateFormat(
    "yyyy.MM.dd 'at' HH:mm:ss z",
    Locale.getDefault()
  )

  override fun doWork(): Result {
    makeStatusNotification("Saving image", applicationContext)
    return try {
      val resolver = applicationContext.contentResolver

      val inputBlurImgUriStr = inputData.getString(KEY_IMAGE_URI)
      val bitmap = BitmapFactory.decodeStream(
        resolver.openInputStream(
          Uri.parse(inputBlurImgUriStr)
        )
      )

      val savedImgUrl = MediaStore.Images.Media.insertImage(
        resolver, bitmap, title, dateFormatter.format(Date())
      )

      if(!savedImgUrl.isNullOrBlank()) {
        val outData = workDataOf(
          KEY_IMAGE_URI to savedImgUrl
        )
        makeStatusNotification("Blur image saved at $savedImgUrl", applicationContext)
        Result.success(outData)
      } else {
        Log.e(TAG_OUTPUT, "Fail to write to MediaStore")
        Result.failure()
      }

    } catch(t: Throwable) {
      t.printStackTrace()
      Result.failure()
    }
  }
}