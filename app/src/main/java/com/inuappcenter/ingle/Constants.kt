package com.inuappcenter.ingle

object Constants {
    const val BASE_URL = "https://inu-ingle-web.pages.dev"
    const val USER_AGENT_SUFFIX = " IngleApp/1.0.0"

    // 뒤로가기 제한 경로
    val RESTRICTED_PATHS = setOf("/", "/tutorial", "/map", "/stamp", "/profile")

    // 허용 도메인
    val ALLOWED_DOMAINS = listOf(BASE_URL)
}