package com.congee1003.ingle

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.widget.Toast
import java.net.URLDecoder

class DownloadHelper(private val context: Context) {

    fun downloadFile(url: String, userAgent: String, contentDisposition: String?, mimeType: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)

                // 쿠키 및 헤더 설정
                val cookies = CookieManager.getInstance().getCookie(url)
                addRequestHeader("Cookie", cookies)
                addRequestHeader("User-Agent", userAgent)

                // 파일명 추출
                val filename = URLUtil.guessFileName(url, contentDisposition, mimeType)
                val decodedFilename = URLDecoder.decode(filename, "UTF-8")

                setDescription("파일 다운로드 중...")
                setTitle(decodedFilename)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, decodedFilename)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(context, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("Download", "Error: ${e.message}")
            Toast.makeText(context, "다운로드 실패: 관리자에게 문의하세요.", Toast.LENGTH_SHORT).show()
        }
    }
}