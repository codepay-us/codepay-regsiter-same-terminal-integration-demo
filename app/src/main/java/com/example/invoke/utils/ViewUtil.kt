package com.example.invoke.utils

import android.content.Context
import android.text.TextUtils
import android.widget.EditText
import com.example.invoke.utils.ToastUtil

object ViewUtil {

    /**
     * 检查输入框字符串是否为空
     */
    fun checkTextIsEmpty(context: Context, editText: EditText): Boolean {
        return if (TextUtils.isEmpty(editText.text)) {
            ToastUtil.showShort(context, editText.hint)
            true
        } else {
            false
        }
    }

    /**
     * 根据输入框的内容获取有效金额
     */
    fun getAmount(editText: EditText): String? {
        return if (!TextUtils.isEmpty(editText.text.toString())) {
            editText.text.toString()
        } else null
    }

}