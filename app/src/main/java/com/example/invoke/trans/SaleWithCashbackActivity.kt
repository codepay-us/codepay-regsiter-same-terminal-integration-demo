package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.invoke.utils.ViewUtil
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import kotlinx.android.synthetic.main.activity_completion.*
import kotlinx.android.synthetic.main.activity_sale_with_cash_back.*
import kotlinx.android.synthetic.main.activity_sale_with_cash_back.cb_signature
import kotlinx.android.synthetic.main.activity_sale_with_cash_back.tv_result
import org.json.JSONException
import org.json.JSONObject

class SaleWithCashbackActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke CashBack"

    private lateinit var radioGroup: RadioGroup

    private var autoClick: Boolean = false

    private lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_with_cash_back)
        mContext = this
        btn_consume.setOnClickListener(this)
        autoClick = intent.getBooleanExtra("autoClick", false)
        LogUtils.setLog(TAG, "$autoClick")
        if (autoClick) {
            // 自动点击银行卡支付按钮
            val handler = Handler()
            handler.postDelayed({
                val checkBox = findViewById<CheckBox>(R.id.cb_signature)
                checkBox.isChecked = true
                val amount = "150.87"
                val cash = "20.00"
                et_amount_consume.setText(amount)
                et_amount_cash.setText(cash)
                btn_consume.performClick()
            }, 2000) // 延迟2秒，确保页面加载完成后再点击按钮
        }
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
        intent.putExtra("transType", InvokeConstant.CASH_BACK)
        intent.putExtra("appId", InvokeConstant.APP_ID)

        var jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("cardType", "2")//credit
            jsonObject.put("amt", et_amount_consume.text.toString())
            jsonObject.put("cashAmount", et_amount_cash.text.toString())
            jsonObject.put("onScreenSignature", cb_signature.isChecked)
            if (operator.text.toString().isNotEmpty()) {
                jsonObject.put("operator",operator.text.toString())
            }
            if (cb_manual.isChecked) {
                jsonObject.put("entryMode", 4)
            }
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
            jsonObject.put("notifyUrl", InvokeConstant.NOTIFY_URL)
            jsonObject.put("OnScreenTip", cb_screen_tip.isChecked)
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("businessOrderNo", jsonObject.getString("businessOrderNo"))
        Log.e(TAG,"获取businessOrderNo :  ${jsonObject.getString("businessOrderNo")}")
        editor.apply()
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_CASH_BACK)
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
            Log.e(TAG, "返现结果：$showText")
            tv_result.text = showText
            // 等待 3 秒
//            Thread.sleep(3000)
            if ("Success" == resultMsg && autoClick){
                val intent = Intent(this, ReprintActivity::class.java)
                intent.putExtra("autoClick", true)
                startActivity(intent)
            }
        }
    }

}