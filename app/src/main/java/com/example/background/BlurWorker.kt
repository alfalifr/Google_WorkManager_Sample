package com.example.background

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.workers.blurBitmap
import com.example.background.workers.makeStatusNotification
import com.example.background.workers.writeBitmapToFile
import java.lang.IllegalArgumentException

class BlurWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
  override fun doWork(): Result {

    makeStatusNotification("Blurring image", applicationContext)

    return try {
      /*
      val bitmap = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.android_cupcake,
      )
       */
      val imgUriStr = inputData.getString(KEY_IMAGE_URI)

      if(imgUriStr.isNullOrBlank()) {
        throw IllegalArgumentException(
          "Can't blur blank or null `imgUriStr`"
        )
      }

      val contentResolver = applicationContext.contentResolver
      val bitmap = BitmapFactory.decodeStream(
        contentResolver.openInputStream(
          Uri.parse(imgUriStr)
        )
      )

      val blurredBitmap = blurBitmap(bitmap, applicationContext)
      val outUri = writeBitmapToFile(applicationContext, blurredBitmap)

      val outData = workDataOf(
        KEY_IMAGE_URI to outUri.toString()
      )

      makeStatusNotification("Blurred bitmap at $outData", applicationContext)

      Result.success(outData)
    } catch(t: Throwable) {
      Result.failure()
    }
  }
}