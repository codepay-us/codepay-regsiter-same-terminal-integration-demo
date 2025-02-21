package com.example.invoke.trans

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.constant.InvokeConstant.ECR_HUB_TOPIC_BATCH_CLOSE
import kotlinx.android.synthetic.main.activity_sale.et_amount
import kotlinx.android.synthetic.main.activity_sale.tv_result
import kotlinx.android.synthetic.main.amount_layout.btn_start_trans


class BatchCloseActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke Balance"

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bach_close)
        mContext = this
        btn_start_trans.setOnClickListener(this)
        et_amount.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_trans -> start2Trans()
        }
    }

    private fun start2Trans() {

        val intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION2)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        intent.putExtra("topic", ECR_HUB_TOPIC_BATCH_CLOSE)
        startActivityForResult(intent, InvokeConstant.REQUEST_BALANCE_INQUIRY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "resultCode = $resultCode")

        if (resultCode == RESULT_OK && data != null) {
            val version = data.getStringExtra(InvokeConstant.version)
            val transType = data.getStringExtra(InvokeConstant.transType)
            val result = data.getStringExtra(InvokeConstant.result)
                ?: data.getStringExtra(InvokeConstant.BizData)
            val resultMsg = data.getStringExtra(InvokeConstant.resultMsg)
                ?: data.getStringExtra(InvokeConstant.ResponseMsg)
            val transData = data.getStringExtra(InvokeConstant.transData)
                ?: data.getStringExtra(InvokeConstant.ResponseCode)

            var showText = "version = $version \n"
            showText += if (transType != null) "transType = $transType \n" else ""
            showText += "result = $result \n"
            showText = resultMsg?.let { "$showText resultMsg = $it \n" } ?: showText
            showText = transData?.let { "$showText transData/responseCode = $it" } ?: showText

            Log.e(TAG, "Result：$showText")
            tv_result.text = showText
        }
    }
}