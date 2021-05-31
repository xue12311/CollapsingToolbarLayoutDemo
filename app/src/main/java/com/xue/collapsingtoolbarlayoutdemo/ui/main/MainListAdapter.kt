package com.xue.collapsingtoolbarlayoutdemo.ui.main

import com.xue.collapsingtoolbarlayoutdemo.databinding.ItemMainListBinding
import com.zjx.app_common_library.utils.viewbinding.BaseBindingAdapter
import com.zjx.app_common_library.utils.viewbinding.BaseViewBindingHolder

class MainListAdapter : BaseBindingAdapter<String, ItemMainListBinding>() {
    override fun convert(holder: BaseViewBindingHolder<ItemMainListBinding>, item: String) {
        holder.binding.tvPosition.setText("当前位置 : ${item}")
    }
}