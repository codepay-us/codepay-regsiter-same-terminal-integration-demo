package com.example.invoke.trans

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.invoke.utils.ViewUtil.getAmount
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.constant.InvokeConstant.INVOKE_BANKCARD_PAY_TYPE
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import com.example.invoke.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_completion.*
import kotlinx.android.synthetic.main.activity_refund.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat


class RefundActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke--RefundActivity"
    private lateinit var radioGroup: RadioGroup

    private var autoClick: Boolean = false

    private lateinit var mContext: Context
    private var selectDate = DateUtil.StringToDate(DateUtil.getCurDateStr("yyyyMMdd"), "yyyyMMdd")
    private val format = SimpleDateFormat("yyyyMMdd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refund)
//        cb_signature.visibility = View.GONE
        Log.e(TAG, "onCreate")
        mContext = this
        autoClick = intent.getBooleanExtra("autoClick", false)
        LogUtils.setLog(TAG, "$autoClick")
        if (autoClick) {
            // 自动点击银行卡支付按钮
            val handler = Handler()
            handler.postDelayed({
                val amount = "10.87"
                et_amount_refund.setText(amount)
                btn_refund_bankcard.performClick()
            }, 2000) // 延迟2秒，确保页面加载完成后再点击按钮
        }
        btn_refund_bankcard.setOnClickListener(this)
        btn_refund_bankcard2.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_refund_bankcard -> startTrans(paymentScenario = "1", cardType = "2")
            R.id.btn_refund_bankcard2 -> startTrans2(paymentScenario = INVOKE_BANKCARD_PAY_TYPE)
            R.id.tv_time -> {
                val dp = DatePickerDialog(this, { _: DatePicker?, year: Int, month: Int, day: Int ->
                    selectDate = DateUtil.setDate(selectDate, year, month, day)
                    tv_time.text = format.format(selectDate)
                }, selectDate.year + 1900, selectDate.month, selectDate.date) //year是从1900后开始的，所以要加1
                dp.show()
            }
        }
    }


    private fun startTrans(
        paymentScenario: String,
        cardType: String? = null,
    ) {
        if (ViewUtil.checkTextIsEmpty(this, et_amount_refund)) return
        val intent = Intent()
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION)
        intent.putExtra("transType", InvokeConstant.REFUND)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            if (cb_reference.isChecked){
                Log.e(TAG, "进行无参考退款")
            } else {
                if (et_origin_business_no.text.toString().isNotEmpty()){
                    jsonObject.put("originBusinessOrderNo", et_origin_business_no.text.toString())
                } else {
                    jsonObject.put("originBusinessOrderNo", sharedPreferences.getString("businessOrderNo","").toString())
                    Log.e(TAG,"使用上一笔交易订单号 : ${sharedPreferences.getString("businessOrderNo","").toString()}")
                }
            }
            jsonObject.put("notifyUrl", InvokeConstant.NOTIFY_URL)
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
//            if (TextUtils.isEmpty(et_origin_business_no.text.toString())
//                && paymentScenario == "1"
//            ) {
//                jsonObject.put("cardType", "2")
//            }
//            if (cb_signature.isChecked) {
//                jsonObject.put("onScreenSignature", 1)
//            } else {
//                jsonObject.put("onScreenSignature", 0)
//            }
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
            jsonObject.put("amt", getAmount(et_amount_refund))
            jsonObject.put("tip", getAmount(et_tip_amount_refund))
            jsonObject.put("cardType", cardType)
            jsonObject.put("paymentScenario", paymentScenario)
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_REFUND)
    }

    private fun startTrans2(
        paymentScenario: String) {
        if (ViewUtil.checkTextIsEmpty(this, et_amount_refund)) return
        val intent = Intent()
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSIONV2)
        intent.putExtra("topic",InvokeConstant.ECR_HUB_TOPIC_PAY)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            if (cb_reference.isChecked){
                Log.e(TAG, "进行无参考退款")
            } else {
                if (et_origin_business_no.text.toString().isNotEmpty()){
                    jsonObject.put("orig_merchant_order_no", et_origin_business_no.text.toString())
                } else {
                    jsonObject.put("orig_merchant_order_no", sharedPreferences.getString("businessOrderNo","").toString())
                    Log.e(TAG,"使用上一笔交易订单号 : ${sharedPreferences.getString("businessOrderNo","").toString()}")
                }
            }
            jsonObject.put("pay_scenario", paymentScenario)
            jsonObject.put("notify_url", InvokeConstant.NOTIFY_URL)
            jsonObject.put("merchant_order_no", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
//            if (TextUtils.isEmpty(et_origin_business_no.text.toString())
//                && paymentScenario == "1"
//            ) {
//                jsonObject.put("cardType", "2")
//            }
//            if (cb_signature.isChecked) {
//                jsonObject.put("on_screen_signature", 1)
//            } else {
//                jsonObject.put("on_screen_signature", 0)
//            }
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
                    LogUtils.setLog(TAG,"Not select printReceipt")
                }
            }
            jsonObject.put("order_amount", getAmount(et_amount_refund))
            jsonObject.put("tip_amount", getAmount(et_tip_amount_refund))
            jsonObject.put("trans_type", InvokeConstant.REFUND)
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_REFUND)
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

            Log.e(TAG, "退货/退款结果：$showText")
            tv_result_refund.text = showText
            // 等待 3 秒
//            Thread.sleep(3000)
            if (autoClick){
                performAuthOperation()
            }
        }
    }

    private fun performAuthOperation() {
        // 执行 Auth 操作的逻辑
        val authIntent = Intent(this, AuthorizationActivity::class.java)
        authIntent.putExtra("autoClick", true)
        startActivity(authIntent)
        LogUtils.setLog(TAG, "执行 Auth 操作")
        runOnUiThread {
            Toast.makeText(this, "Please swipe card", Toast.LENGTH_LONG).show()
        }
    }

}
