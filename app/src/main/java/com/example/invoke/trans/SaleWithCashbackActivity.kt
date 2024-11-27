package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import com.example.invoke.utils.ViewUtil
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import kotlinx.android.synthetic.main.activity_sale_with_cash_back.*
import kotlinx.android.synthetic.main.activity_sale_with_cash_back.cb_signature
import kotlinx.android.synthetic.main.activity_sale_with_cash_back.tv_result
import org.json.JSONException
import org.json.JSONObject

class SaleWithCashbackActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke CashBack"

    private lateinit var radioGroup: RadioGroup

    private lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_with_cash_back)
        mContext = this
        btn_consume.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_consume -> startTrans()
        }
    }

    private fun startTrans() {
        if (ViewUtil.checkTextIsEmpty(mContext, et_amount_consume)) return

        var intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION)
        intent.putExtra("app_id", InvokeConstant.APP_ID)

        var jsonObject = JSONObject()
        try {
            jsonObject.put("merchant_order_no", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("card_network", "2")//credit
            jsonObject.put("order_amount", et_amount_consume.text.toString())
            jsonObject.put("cash_amount", et_amount_cash.text.toString())
            jsonObject.put("on_screen_signature", cb_signature.isChecked)
            radioGroup = findViewById(R.id.radioGroup)
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        startActivityForResult(intent, InvokeConstant.REQUEST_CASH_BACK)
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