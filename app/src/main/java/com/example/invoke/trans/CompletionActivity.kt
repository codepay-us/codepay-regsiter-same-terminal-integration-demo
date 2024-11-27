package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import com.example.invoke.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_completion.et_amount
import kotlinx.android.synthetic.main.activity_completion.tv_result
import kotlinx.android.synthetic.main.activity_refund.et_amount_refund
import kotlinx.android.synthetic.main.activity_refund.et_origin_business_no
import kotlinx.android.synthetic.main.activity_refund.et_tip_amount_refund
import org.json.JSONException
import org.json.JSONObject


class CompletionActivity : Activity(), View.OnClickListener {

    private val TAG = CompletionActivity::class.java.simpleName
    private lateinit var radioGroup: RadioGroup

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        setContentView(R.layout.activity_completion)
        findViewById<Button>(R.id.btn_start_trans).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_trans -> startTrans()
        }
    }

    private fun startTrans() {
        if (ViewUtil.checkTextIsEmpty(mContext, et_amount)) return
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        var intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSIONV2)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        var jsonObject = JSONObject()
        try {
            if (et_origin_business_no.text.toString().isNotEmpty()) {
                jsonObject.put("orig_merchant_order_no", et_origin_business_no.text.toString())
            } else {
                jsonObject.put(
                    "orig_merchant_order_no",
                    sharedPreferences.getString("businessOrderNo", "").toString()
                )
                Log.e(
                    TAG,
                    "使用上一笔交易订单号 : ${
                        sharedPreferences.getString("businessOrderNo", "").toString()
                    }"
                )
            }
            jsonObject.put("notify_url", InvokeConstant.NOTIFY_URL)
            jsonObject.put("merchant_order_no", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            radioGroup = findViewById(R.id.radioGroup)
            val checkedRadioButtonId = radioGroup.checkedRadioButtonId
            if (checkedRadioButtonId != -1) {
                val radioButton = findViewById<RadioButton>(checkedRadioButtonId)
                val printReceiptIntValue = when (radioButton.text.toString()) {
                    "No print" -> 0
                    "Merchant" -> 1
                    "CardHolder" -> 2
                    "All" -> 3
                    else -> -1
                }
                if (printReceiptIntValue != -1) {
                    jsonObject.put("receipt_print_mode", printReceiptIntValue)
                } else {
                    LogUtils.setLog(TAG, "Not select printReceipt")
                }
            }
            jsonObject.put("order_amount", ViewUtil.getAmount(et_amount_refund))
            jsonObject.put("tip_amount", ViewUtil.getAmount(et_tip_amount_refund))
            jsonObject.put("trans_type", InvokeConstant.PRE_AUTH_COMPLETE)
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_PREAUTH_COMPLETE)
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