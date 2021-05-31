package com.xue.collapsingtoolbarlayoutdemo.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.google.android.material.appbar.AppBarLayout
import com.xue.collapsingtoolbarlayoutdemo.R
import com.xue.collapsingtoolbarlayoutdemo.databinding.ActivityMainBinding
import com.xue.collapsingtoolbarlayoutdemo.utils.LinearGradientUtils
import com.zjx.app_common_library.App
import com.zjx.app_common_library.base.viewbinding.BaseVbVmActivity
import com.zjx.app_common_library.utils.BigDecimalUtils
import java.math.BigDecimal

class MainActivity : BaseVbVmActivity<MainViewModel, ActivityMainBinding>() {
    private val mCollapsingToolbarLayout by lazy { mViewBinding.collapsingToolbarLayout }
    private val mAppBarLayout by lazy { mViewBinding.appBarLayout }
    private val mTablayout by lazy { mViewBinding.tablayout }
    private val mToolbar by lazy { mViewBinding.toolbar }
    private val mRecyclerView by lazy { mViewBinding.recyclerView }
    private val mAdapter by lazy { MainListAdapter() }
    private val mLinearGradientUtils by lazy {
        LinearGradientUtils(
            ColorUtils.getColor(R.color.toolbar_colors_expanded),
            ColorUtils.getColor(R.color.toolbar_colors_collapsed)
        )
    }

    //滑动进度
    private var progress: Float = 0f

    override fun initWindowParam() {
        super.initWindowParam()
        BarUtils.transparentStatusBar(this)
    }

    override fun initView() {
        super.initView()
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_whilte_ios_24)
        mAppBarLayout.addOnOffsetChangedListener(mOffsetChangedListener)
    }

    override fun initData() {
        super.initData()
        mAdapter.setList(mutableListOf<String>().apply {
            for (i in 0..100) {
                add(i.toString())
            }
        })
    }

    private val mOffsetChangedListener = object : AppBarLayout.OnOffsetChangedListener {
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            //滑动进度
            progress = BigDecimalUtils.div(
                BigDecimal(Math.abs(verticalOffset)),
                BigDecimal(appBarLayout?.getTotalScrollRange() ?: 0)
            ).toFloat()
            //更新 返回键 颜色
            onUpdateNavigationIconColor()
        }
    }

    /**
     * 更新返回按钮颜色
     */
    private fun onUpdateNavigationIconColor() {
        val drawable =
            if (mToolbar.navigationIcon == null) {
                ResourceUtils.getDrawable(R.drawable.ic_baseline_arrow_whilte_ios_24)
            } else {
                mToolbar.navigationIcon!!
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(getToolbarColor())
        }
        mToolbar.setNavigationIcon(drawable)
    }

    private fun getToolbarColor() = mLinearGradientUtils.getColor(progress)
}
