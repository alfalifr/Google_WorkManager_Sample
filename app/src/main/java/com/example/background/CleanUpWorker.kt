package com.example.background

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.workers.makeStatusNotification
import com.example.background.workers.sleep
import java.io.File

class CleanUpWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

  override fun doWork(): Result {
    makeStatusNotification("Cleaning up temp files", applicationContext)
    sleep()
    return try {
      val outDir = File(applicationContext.filesDir, OUTPUT_PATH)
      if(outDir.exists()) {
        val children = outDir.listFiles()
        if(children?.isNotEmpty() == true) {
          for(child in children) {
            val name = child.name
            if(name.isNotBlank() && name.endsWith(".png", ignoreCase = true)) {
              val isDeleted = child.delete()
              Log.i(TAG_OUTPUT, "$child isDeleted = '$isDeleted'")
            }
          }
        }
      }
      Result.success()
    } catch(t: Throwable) {
      t.printStackTrace()
      Result.failure()
    }
  }
}