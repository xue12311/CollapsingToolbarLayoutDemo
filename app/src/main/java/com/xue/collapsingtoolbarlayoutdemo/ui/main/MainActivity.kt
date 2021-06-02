package com.xue.collapsingtoolbarlayoutdemo.ui.main

import android.os.Build
import android.util.TypedValue
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ResourceUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.xue.collapsingtoolbarlayoutdemo.R
import com.xue.collapsingtoolbarlayoutdemo.databinding.ActivityMainBinding
import com.xue.collapsingtoolbarlayoutdemo.utils.LinearGradientUtils
import com.zjx.app_common_library.base.viewbinding.BaseVbVmActivity
import com.zjx.app_common_library.utils.BigDecimalUtils
import com.zjx.app_common_library.utils.ext.addOnTabSelectedListener
import java.math.BigDecimal


class MainActivity : BaseVbVmActivity<MainViewModel, ActivityMainBinding>() {
    private val mCollapsingToolbarLayout by lazy { mViewBinding.collapsingToolbarLayout }
    private val mAppBarLayout by lazy { mViewBinding.appBarLayout }
    private val mTabLayout by lazy { mViewBinding.tabLayout }
    private val mToolbar by lazy { mViewBinding.toolbar }
    private val mRecyclerView by lazy { mViewBinding.recyclerView }
    private val mAdapter by lazy { MainListAdapter() }
    private val mLinearGradientUtils by lazy {
        LinearGradientUtils(
            ColorUtils.getColor(R.color.toolbar_colors_expanded),
            ColorUtils.getColor(R.color.toolbar_colors_collapsed)
        )
    }
    private var mTabSelectedPosition = 0

    //当前滑动位置
    private var mCurVerticalOffset: Int? = null

    //滑动进度
    private var progress: Float = 0f

    private val mDataList by lazy {
        mutableListOf("周榜", "月榜")
    }

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
        initTabLayout()
    }

    override fun initData() {
        super.initData()
        mAdapter.setList(mutableListOf<String>().apply {
            for (i in 0..100) {
                add(i.toString())
            }
        })
    }

    private fun initTabLayout() {
        mTabLayout.setSelectedTabIndicator(R.drawable.tab_indicator)
        mTabLayout.addOnTabSelectedListener(null, { tab ->
            if (tab != null) {
                onModifyTabTextSize(tab, getTextOrIconColor(), false)
            }
        }, { tab ->
            if (tab != null) {
                onModifyTabTextSize(tab, getTextOrIconColor(), true)
            }
        })
        for (index in 0 until mDataList.size) {
            if (mTabLayout.getTabAt(index) == null) {
                mTabLayout.addTab(mTabLayout.newTab().setText(mDataList[index]))
            } else {
                mTabLayout.getTabAt(index)?.setText(mDataList[index])
            }
        }
    }

    private val mOffsetChangedListener = object : AppBarLayout.OnOffsetChangedListener {
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            // 修改状态栏会触发 AppBarLayout重绘 onOffsetChanged() 方法，这里加个去重
            if (mCurVerticalOffset != null && mCurVerticalOffset == verticalOffset) {
                return
            }
            mCurVerticalOffset = verticalOffset
            //滑动进度
            progress = BigDecimalUtils.div(
                BigDecimal(Math.abs(verticalOffset)),
                BigDecimal(appBarLayout?.getTotalScrollRange() ?: 0)
            ).toFloat()
            val mProgressColor: Int = getTextOrIconColor()
            //更新 返回键 颜色
            onUpdateNavigationIconColor(mProgressColor)
            //更新 Tab
            onUpdateTabTextColor(mProgressColor)
        }
    }


    /**
     * 更新 Tab 颜色
     */
    private fun onUpdateTabTextColor(textColor: Int = getTextOrIconColor()) {
        LogUtils.e("设置Tab颜色")
        mTabLayout.setTabTextColors(textColor, textColor)
        val mPosition = mTabLayout.selectedTabPosition
        for (index in 0 until mTabLayout.tabCount) {
            mTabLayout.getTabAt(index)?.let { tab ->
                onModifyTabTextSize(
                    tab, textColor,
                    if (mPosition == index) true else false
                )
            }
        }
    }

    private fun onModifyTabTextSize(
        tab: TabLayout.Tab,
        textColor: Int = getTextOrIconColor(),
        isSelected: Boolean = false
    ) {
        val textView = if (tab.customView != null && tab.customView is TextView) {
            (tab.customView as TextView)
        } else {
            TextView(this)
        }
        val selectedSize =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                if (isSelected) 20f else 14f,
                resources.displayMetrics
            )
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize)
        textView.setTextColor(textColor)
        textView.setText(tab.text)
        tab.customView = textView
    }

    /**
     * 更新返回按钮颜色
     */
    private fun onUpdateNavigationIconColor(iconColor: Int = getTextOrIconColor()) {
        LogUtils.e("设置 返回 颜色")
        val drawable =
            if (mToolbar.navigationIcon == null) {
                ResourceUtils.getDrawable(R.drawable.ic_baseline_arrow_whilte_ios_24)
            } else {
                mToolbar.navigationIcon!!
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(iconColor)
        }
        mToolbar.setNavigationIcon(drawable)
    }

    private fun getTextOrIconColor(): Int {
        return mLinearGradientUtils.getColor(progress)
    }
}
