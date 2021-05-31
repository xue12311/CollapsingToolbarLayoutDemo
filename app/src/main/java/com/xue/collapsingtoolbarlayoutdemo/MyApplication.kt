package com.xue.collapsingtoolbarlayoutdemo

import com.zjx.app_common_library.App

/**
 * 自定义 Application
 */
class MyApplication : App() {
    companion object {
        val instance: MyApplication by lazy { MyApplication() }
    }

    override fun onCreate() {
        super.onCreate()
        initSPUtils()
        initLogUtils(BuildConfig.DEBUG,BuildConfig.DEBUG)
    }
}