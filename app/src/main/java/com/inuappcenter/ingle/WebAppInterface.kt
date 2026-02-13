package com.inuappcenter.ingle

import android.util.Log
import android.webkit.JavascriptInterface

class WebAppInterface(
    private val onUpdateRequested: () -> Unit
) {
    @JavascriptInterface
    fun onRouteChange(path: String) {
        Log.d("RouteChange", "경로 변경: $path")
    }

    @JavascriptInterface
    fun requestAppUpdate() {
        onUpdateRequested()
    }
}