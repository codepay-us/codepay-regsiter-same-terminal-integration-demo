package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_tip_adjustment.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

class TipAdjustmentActivity : Activity(), View.OnClickListener {

    private val TAG = "TipAdjustmentActivity"

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_adjustment)
        mContext = this
        btn_start_trans.setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {

            R.id.btn_start_trans -> {
                startTrans2()
            }
        }
    }


    private fun startTrans2() {
        if (ViewUtil.checkTextIsEmpty(this, et_origin_business_no)) return
        val intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        intent.putExtra("version", InvokeConstant.VERSION2)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        intent.putExtra("topic", InvokeConstant.ECR_HUB_TOPIC_TIP_ADJUSTMENT)
        val jsonObject = JSONObject()
        try {
//            if (et_origin_business_no.text.toString().isNotEmpty()){
//                jsonObject.put("orig_merchant_order_no", et_origin_business_no.text.toString())
//            } else {
//                jsonObject.put("orig_merchant_order_no", sharedPreferences.getString("businessOrderNo","").toString())
//                Log.e(TAG,"使用上一笔交易订单号 : ${sharedPreferences.getString("businessOrderNo","").toString()}")
//            }
            jsonObject.put("merchant_order_no", et_origin_business_no.text.toString())
            jsonObject.put("tip_adjustment_amount", ViewUtil.getAmount(et_tip_amount))
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "biz_data: $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_BALANCE_INQUIRY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "三方调用收到信息，resultCode = $resultCode")
        if (resultCode == RESULT_OK) {
            val version = data?.getStringExtra(InvokeConstant.version)
            val transType = data?.getStringExtra(InvokeConstant.transType)
            val result = data?.getStringExtra(InvokeConstant.result)
                ?: data?.getStringExtra(InvokeConstant.BizData)
            val resultMsg = data?.getStringExtra(InvokeConstant.resultMsg)
                ?: data?.getStringExtra(InvokeConstant.ResponseMsg)
            val transData = data?.getStringExtra(InvokeConstant.transData)
                ?: data?.getStringExtra(InvokeConstant.ResponseCode)

            // 生成显示文本
            var showText = "version = $version \n"
            showText += if (transType != null) "transType = $transType \n" else ""
            showText += "result = $result \n"
            showText = resultMsg?.let { "$showText resultMsg = $it \n" } ?: showText
            showText = transData?.let { "$showText transData/responseCode = $it" } ?: showText
            Log.e(TAG, "小费调整结果：$showText")
            tv_result_tip_adjustment.text = showText
        }
    }
}
