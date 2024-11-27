package com.example.invoke.trans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.invoke.utils.ViewUtil.checkTextIsEmpty
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.LogUtils
import kotlinx.android.synthetic.main.activity_sale.tv_result
import kotlinx.android.synthetic.main.activity_void.*
//import kotlinx.android.synthetic.main.activity_void.cb_signature
import org.json.JSONException
import org.json.JSONObject

class QueryActivity : Activity(), View.OnClickListener {

    private val TAG = "QueryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_void)
        radioGroup.visibility = View.GONE
        btn_start_trans_void.setOnClickListener(this)
        btn_start_trans_void.text = "Query"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_trans_void -> startTrans2()
        }
    }

    private fun startTrans2() {
        val intent = Intent()
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSIONV2)
        intent.putExtra("topic", InvokeConstant.ECR_HUB_TOPIC_QUERY)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            if (et_ori_business_order_no.text.toString().isNotEmpty()){
                jsonObject.put("orig_merchant_order_no", et_ori_business_order_no.text.toString())
            } else {
                jsonObject.put("orig_merchant_order_no", sharedPreferences.getString("businessOrderNo","").toString())
                Log.e(TAG,"使用上一笔交易订单号 : ${sharedPreferences.getString("businessOrderNo","").toString()}")
            }
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_VOID)
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