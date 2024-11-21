package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import com.example.invoke.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_completion.*
import kotlinx.android.synthetic.main.activity_completion.btn_start_trans
import kotlinx.android.synthetic.main.activity_completion.cb_not_read_card
import kotlinx.android.synthetic.main.activity_completion.cb_signature
import kotlinx.android.synthetic.main.activity_completion.et_amount
import kotlinx.android.synthetic.main.activity_completion.et_ori_business_order_no
import kotlinx.android.synthetic.main.activity_completion.tv_result
import org.json.JSONException
import org.json.JSONObject


class CompletionActivity : Activity(), View.OnClickListener {

    private val TAG = CompletionActivity::class.java.simpleName
    private lateinit var radioGroup: RadioGroup

    private lateinit var mContext: Context

    private var autoClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        setContentView(R.layout.activity_completion)
        findViewById<Button>(R.id.btn_start_trans).setOnClickListener(this)

        autoClick = intent.getBooleanExtra("autoClick", false)
        LogUtils.setLog(TAG, "$autoClick")
        // 检查是否通过特定方式启动页面
        if (autoClick) {
            // 自动点击银行卡支付按钮
            val handler = Handler()
            handler.postDelayed({
                val amount = "150.87"
                // 设置 CheckBox 为选中状态
                val checkBox = findViewById<CheckBox>(R.id.cb_not_read_card)
                checkBox.isChecked = true
                et_amount.setText(amount)
                btn_start_trans.performClick()
            }, 2000) // 延迟2秒，确保页面加载完成后再点击按钮
        }
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
        intent.putExtra("version", InvokeConstant.VERSION)
        intent.putExtra("transType", InvokeConstant.PRE_AUTH_COMPLETE)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        var jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
//            jsonObject.put("originBusinessOrderNo", et_ori_business_order_no.text.toString())
            if (et_ori_business_order_no.text.toString().isNotEmpty()){
                jsonObject.put("originBusinessOrderNo", et_ori_business_order_no.text.toString())
            } else {
                jsonObject.put("originBusinessOrderNo", sharedPreferences.getString("businessOrderNo","").toString())
                Log.e(TAG,"使用上一笔交易订单号 : ${sharedPreferences.getString("businessOrderNo","").toString()}")
            }
            if (et_tip_amount.text.toString().isNotEmpty()){
                jsonObject.put("tip", et_tip_amount.text.toString())
            }
            jsonObject.put("amt", et_amount.text.toString())
            jsonObject.put("onScreenSignature", cb_signature.isChecked)
            if (cb_not_read_card.isChecked) {
                jsonObject.put("forceNocardAuthCompletion", 1)
            } else {
                jsonObject.put("forceNocardAuthCompletion", 0)
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
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_PREAUTH_COMPLETE)
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

            if (transData != null){
                val jsonData = JSONObject(transData)
                val orderNo = jsonData.getString("businessOrderNo")
                val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("businessOrderNo", orderNo)
                Log.e(TAG,"获取businessOrderNo :  $orderNo")
                editor.apply()
            }

            var showText = "version = $version \ntransType = $transType \nresult = $result \n"
            showText = resultMsg?.let { showText + "resultMsg = $it \n" } ?: showText
            showText = transData?.let { showText + "transData = $it" } ?: showText
            Log.e(TAG, "预授权完成结果：$showText")
            tv_result.text = showText

            // 等待 3 秒
//            Thread.sleep(3000)
            if (autoClick) {
                performVoidOperation()
            }

        } else {
            val version = data?.getStringExtra(InvokeConstant.version)
            val transType = data?.getStringExtra(InvokeConstant.transType)
            val result = data?.getStringExtra(InvokeConstant.result)
            val resultMsg = data?.getStringExtra(InvokeConstant.resultMsg)
            val transData = data?.getStringExtra(InvokeConstant.transData)
            var showText = "version = $version \ntransType = $transType \nresult = $result \n"
            showText = resultMsg?.let { showText + "resultMsg = $it \n" } ?: showText
            showText = transData?.let { showText + "transData = $it" } ?: showText
            Log.e(TAG, "预授权完成结果：$showText")
            tv_result.text = showText
        }
    }

    private fun performVoidOperation() {
        // 执行 Void 操作的逻辑
        val voidIntent = Intent(this, VoidActivity::class.java)
        voidIntent.putExtra("autoClick", true)
        startActivity(voidIntent)
        LogUtils.setLog(TAG, "执行 Void 操作")
    }
}