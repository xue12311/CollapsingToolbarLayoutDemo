package com.xue.collapsingtoolbarlayoutdemo.ui.main

import android.content.Context
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.google.android.material.appbar.AppBarLayout
import com.xue.collapsingtoolbarlayoutdemo.R
import com.xue.collapsingtoolbarlayoutdemo.databinding.ActivityMainBinding
import com.xue.collapsingtoolbarlayoutdemo.utils.LinearGradientUtils
import com.zjx.app_common_library.base.viewbinding.BaseVbVmActivity
import com.zjx.app_common_library.utils.BigDecimalUtils
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import java.math.BigDecimal

class MainActivity : BaseVbVmActivity<MainViewModel, ActivityMainBinding>() {
    private val mCollapsingToolbarLayout by lazy { mViewBinding.collapsingToolbarLayout }
    private val mAppBarLayout by lazy { mViewBinding.appBarLayout }
    private val mMagicIndicator by lazy { mViewBinding.magicIndicator }
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
        initMagicIndicator()
    }

    private fun initMagicIndicator() {
        mMagicIndicator.setBackgroundResource(ColorUtils.getColor(android.R.color.transparent))
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = mCommonNavigatorAdapter
        mMagicIndicator.navigator = commonNavigator
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
            val mProgressColor: Int = getTextOrIconColor()
            //更新 返回键 颜色
            onUpdateNavigationIconColor(mProgressColor)
            //更新 Tab
            onUpdateTabTextColor(mProgressColor)
        }
    }
    private val mCommonNavigatorAdapter = object : CommonNavigatorAdapter() {

        override fun getCount(): Int = mDataList.size
        override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
            val simplePagerTitleView = SimplePagerTitleView(context)
            simplePagerTitleView.setText(mDataList[index])
            simplePagerTitleView.normalColor = getTextOrIconColor()
            simplePagerTitleView.selectedColor = simplePagerTitleView.normalColor
            simplePagerTitleView.setTextSize(
                if (mTabSelectedPosition == index) {
                    20f
                } else {
                    14f
                }
            )
            simplePagerTitleView.setOnClickListener {
                mTabSelectedPosition = index
                mMagicIndicator.onPageSelected(index)
                notifyDataSetChanged()
            }
            return simplePagerTitleView
        }

        override fun getIndicator(context: Context?): IPagerIndicator {
            val indicator = LinePagerIndicator(context)
            indicator.mode = LinePagerIndicator.MODE_EXACTLY
            indicator.yOffset = SizeUtils.dp2px(2f).toFloat()
            indicator.setColors(ColorUtils.getColor(R.color.orange))
            return indicator
        }

    }

    /**
     * 更新 Tab 颜色
     */
    private fun onUpdateTabTextColor(textColor: Int = getTextOrIconColor()) {
        for (index in 0 until mCommonNavigatorAdapter.count) {
            mCommonNavigatorAdapter.getTitleView(this, index)?.let { simplePagerTitleView ->
                if (simplePagerTitleView is SimplePagerTitleView) {
                    simplePagerTitleView.normalColor = textColor
                    simplePagerTitleView.selectedColor = simplePagerTitleView.normalColor
                }
            }
        }
    }

    /**
     * 更新返回按钮颜色
     */
    private fun onUpdateNavigationIconColor(iconColor: Int = getTextOrIconColor()) {
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
