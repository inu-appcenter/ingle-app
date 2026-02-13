package com.congee1003.ingle

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.net.URISyntaxException

class AppWebViewClient(
    private val context: Context,
    private val onPageFinishedCallback: (String?) -> Unit
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onPageFinishedCallback(url)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        onPageFinishedCallback(url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url.toString()

        // 1. 내부 도메인 허용
        if (Constants.ALLOWED_DOMAINS.any { url.startsWith(it) }) return false

        // 2. 외부 앱/스키마 처리
        return handleExternalScheme(url, view)
    }

    private fun handleExternalScheme(url: String, view: WebView?): Boolean {
        if (url.startsWith("intent:") || url.startsWith("market:") || url.startsWith("ispmobile:")) {
            return try {
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    true
                } else {
                    handleAppNotInstalled(intent, view)
                }
            } catch (e: URISyntaxException) {
                Log.e("WebView", "Intent Parse Error: ${e.message}")
                false
            } catch (e: Exception) {
                Log.e("WebView", "External App Error: ${e.message}")
                true
            }
        }

        // 3. 일반 외부 링크
        return try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            true
        } catch (e: Exception) {
            Toast.makeText(context, "외부 링크 열기 실패", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun handleAppNotInstalled(intent: Intent, view: WebView?): Boolean {
        val fallbackUrl = intent.getStringExtra("browser_fallback_url")
        if (fallbackUrl != null) {
            view?.loadUrl(fallbackUrl)
            return true
        }

        val packageName = intent.`package`
        if (packageName != null) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            return true
        }

        Toast.makeText(context, "관련 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
        return true
    }
}