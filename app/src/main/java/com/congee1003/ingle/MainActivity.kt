package com.congee1003.ingle

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var backPressCallback: OnBackPressedCallback

    // 유틸리티 클래스 지연 초기화
    private val networkHelper by lazy { NetworkHelper(this) }
    private val downloadHelper by lazy { DownloadHelper(this) }

    // 파일 업로드 콜백
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    // --- ActivityResult Launcher ---

    // 이미지 선택 (단일)
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        handleFileSelection(uri?.let { listOf(it) })
    }

    // 이미지 선택 (다중)
    private val pickMultipleImagesLauncher = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        handleFileSelection(uris)
    }

    // 권한 요청
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        results.filterValues { !it }.keys.forEach { permission ->
            showToast("$permission 권한 거부됨")
        }
    }

    // --- Lifecycle ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWebView()
        setupBackPressHandler()
        checkAndRequestPermissions()
        loadInitialPage()
    }

    // --- Setup ---

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView = findViewById(R.id.webview)

        // 기본 설정
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            textZoom = 100
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            userAgentString += Constants.USER_AGENT_SUFFIX
        }

        // 컴포넌트 연결
        webView.webViewClient = AppWebViewClient(this) { url ->
            updateBackPressState(url)
        }

        webView.webChromeClient = AppWebChromeClient(this) { callback, _ ->
            handleShowFileChooser(callback)
        }

        webView.addJavascriptInterface(WebAppInterface {
            runOnUiThread { showRefreshDialog() }
        }, "AndroidBridge")

        // 다운로드 핸들러
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            downloadHelper.downloadFile(url, userAgent, contentDisposition, mimeType)
        }
    }

    private fun setupBackPressHandler() {
        backPressCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack()
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    // --- Actions ---

    private fun loadInitialPage() {
        if (networkHelper.isInternetAvailable()) {
            webView.loadUrl(Constants.BASE_URL)
        } else {
            showRetryDialog()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(Manifest.permission.INTERNET)
        permissionLauncher.launch(permissions)
    }

    private fun updateBackPressState(url: String?) {
        val currentPath = url?.replace(Constants.BASE_URL, "") ?: ""
        val isRootPath = currentPath in Constants.RESTRICTED_PATHS || currentPath.isEmpty()
        backPressCallback.isEnabled = webView.canGoBack() && !isRootPath
    }

    // 파일 선택 요청 처리
    private fun handleShowFileChooser(callback: ValueCallback<Array<Uri>>?): Boolean {
        filePathCallback?.onReceiveValue(null) // 이전 콜백 취소
        filePathCallback = callback

        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            pickMultipleImagesLauncher.launch(request)
        } else {
            pickImageLauncher.launch(request)
        }
        return true
    }

    // 파일 선택 결과 처리
    private fun handleFileSelection(uris: List<Uri>?) {
        val result = if (!uris.isNullOrEmpty()) {
            uris.map { FileUtil.copyUriToCache(this, it) }.toTypedArray()
        } else null

        filePathCallback?.onReceiveValue(result)
        filePathCallback = null
    }

    // --- UI Helpers ---

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showRetryDialog() {
        AlertDialog.Builder(this)
            .setTitle("인터넷 연결 없음")
            .setMessage("인터넷 연결 후 다시 시도해주세요.")
            .setPositiveButton("재시도") { _, _ -> loadInitialPage() }
            .setNegativeButton("종료") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showRefreshDialog() {
        AlertDialog.Builder(this)
            .setTitle("화면 업데이트")
            .setMessage("최신 화면으로 업데이트를 진행합니다.")
            .setPositiveButton("확인") { _, _ ->
                webView.clearCache(true)
                webView.reload()
                showToast("업데이트 완료")
            }
            .setNegativeButton("취소", null)
            .show()
    }
}