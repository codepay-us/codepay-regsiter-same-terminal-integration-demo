package com.example.invoke.trans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.invoke.utils.ViewUtil.checkTextIsEmpty
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import kotlinx.android.synthetic.main.activity_completion.*
import kotlinx.android.synthetic.main.activity_void.*
import kotlinx.android.synthetic.main.activity_void.et_ori_business_order_no
import kotlinx.android.synthetic.main.activity_void.tv_result
//import kotlinx.android.synthetic.main.activity_void.cb_signature
import org.json.JSONException
import org.json.JSONObject

class VoidActivity : Activity(), View.OnClickListener {

    private val TAG = "VoidActivity"

    private lateinit var radioGroup: RadioGroup

    private var autoClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_void)
        autoClick = intent.getBooleanExtra("autoClick", false)
        LogUtils.setLog(TAG, "$autoClick")
        if (autoClick) {
            // 自动点击银行卡支付按钮
            val handler = Handler()
            handler.postDelayed({
                btn_start_trans_void.performClick()
            }, 2000) // 延迟2秒，确保页面加载完成后再点击按钮
        }
        btn_start_trans_void.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_trans_void -> startTrans()
        }
    }

    private fun startTrans() {
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION)
        intent.putExtra("transType", InvokeConstant.VOID)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            if (et_ori_business_order_no.text.toString().isNotEmpty()){
                jsonObject.put("originBusinessOrderNo", et_ori_business_order_no.text.toString())
            } else {
                jsonObject.put("originBusinessOrderNo", sharedPreferences.getString("businessOrderNo","").toString())
                Log.e(TAG,"使用上一笔交易订单号 : ${sharedPreferences.getString("businessOrderNo","").toString()}")
            }
            jsonObject.put("notifyUrl", InvokeConstant.NOTIFY_URL)
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
                    jsonObject.put("receiptPrintMode", printReceiptIntValue)
                } else {
                    LogUtils.setLog(TAG,"Not select printReceipt")
                }
            }
//            if (cb_signature.isChecked) {
//                jsonObject.put("onScreenSignature", 1)
//            } else {
//                jsonObject.put("onScreenSignature", 0)
//            }
//            if (cb_not_read_card.isChecked) {
//                jsonObject.put("forceNocardAuthCompletion", 1)
//            } else {
//                jsonObject.put("forceNocardAuthCompletion", 0)
//            }
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_VOID)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "三方调用收到信息，resultCode = $resultCode")
        if (resultCode == RESULT_OK) {
            val version = data?.getStringExtra(InvokeConstant.version)
            val transType = data?.getStringExtra(InvokeConstant.transType)
            val result = data?.getStringExtra(InvokeConstant.result)
            val resultMsg = data?.getStringExtra(InvokeConstant.resultMsg)
            val transData = data?.getStringExtra(InvokeConstant.transData)

            var showText = "version = $version \ntransType = $transType \nresult = $result \n"
            showText = resultMsg?.let { showText + "resultMsg = $it \n" } ?: showText
            showText = transData?.let { showText + "transData = $it" } ?: showText
            Log.e(TAG, "消费撤销结果：$showText")
            tv_result.text = showText
            // 等待 3 秒
//            Thread.sleep(3000)
            if (autoClick) {
                performQueryOperation()
            }
        }
    }

    private fun performQueryOperation() {
        // 执行 Query 操作的逻辑
        val queryIntent = Intent(this, QueryActivity::class.java)
        queryIntent.putExtra("autoClick", true)
        startActivity(queryIntent)
        LogUtils.setLog(TAG, "执行 Query 操作")
    }
}