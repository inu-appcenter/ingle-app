package com.inuappcenter.ingle

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class AppWebChromeClient(
    private val context: Context,
    private val onShowFileChooserCallback: (ValueCallback<Array<Uri>>?, FileChooserParams?) -> Boolean
) : WebChromeClient() {

    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> result?.confirm() }
            .setCancelable(false)
            .show()
        return true
    }

    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> result?.confirm() }
            .setNegativeButton("취소") { _, _ -> result?.cancel() }
            .setCancelable(false)
            .show()
        return true
    }

    override fun onShowFileChooser(
        view: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        return onShowFileChooserCallback(filePathCallback, fileChooserParams)
    }
}