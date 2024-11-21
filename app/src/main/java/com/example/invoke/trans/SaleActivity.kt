package com.example.invoke.trans

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.constant.InvokeConstant.INVOKE_BANKCARD_PAY_TYPE
import com.example.invoke.constant.InvokeConstant.INVOKE_CASH
import com.example.invoke.constant.InvokeConstant.INVOKE_QR_B_SCAN_C
import com.example.invoke.constant.InvokeConstant.INVOKE_QR_C_SCAN_B_PAY_TYPE
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import com.example.invoke.utils.ViewUtil.checkTextIsEmpty
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_bankcard
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_bankcard2
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_cash
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_cash2
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_qr
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_qr2
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_scan
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_scan2
import kotlinx.android.synthetic.main.activity_sale.cb_manual
import kotlinx.android.synthetic.main.activity_sale.cb_screen_tip
import kotlinx.android.synthetic.main.activity_sale.cb_signature
import kotlinx.android.synthetic.main.activity_sale.et_amount
import kotlinx.android.synthetic.main.activity_sale.et_note
import kotlinx.android.synthetic.main.activity_sale.et_tip
import kotlinx.android.synthetic.main.activity_sale.operator
import kotlinx.android.synthetic.main.activity_sale.tv_result
import org.json.JSONException
import org.json.JSONObject


class SaleActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke--ConsumeActivity"

    private lateinit var mContext: Context

    private lateinit var radioGroup: RadioGroup

    private var autoClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale)
        mContext = this
        btn_start_trans_bankcard.setOnClickListener(this)
        btn_start_trans_scan.setOnClickListener(this)
        btn_start_trans_qr.setOnClickListener(this)
        btn_start_trans_cash.setOnClickListener(this)

        btn_start_trans_bankcard2.setOnClickListener(this)
        btn_start_trans_scan2.setOnClickListener(this)
        btn_start_trans_qr2.setOnClickListener(this)
        btn_start_trans_cash2.setOnClickListener(this)
        autoClick = intent.getBooleanExtra("autoClick", false)
        LogUtils.setLog(TAG, "$autoClick")
        // 检查是否通过特定方式启动页面
        if (autoClick) {
            // 自动点击银行卡支付按钮
            val handler = Handler()
            handler.postDelayed({
                val amount = "10.87"
                et_amount.setText(amount)
                btn_start_trans_bankcard.performClick()
            }, 2000) // 延迟2秒，确保页面加载完成后再点击按钮
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_trans_bankcard -> startTrans(paymentScenario = "1", cardType = "2")
            R.id.btn_start_trans_scan -> startTrans(paymentScenario = "4", paymentMethod = "WeChatPay")
            R.id.btn_start_trans_qr -> startTrans(paymentScenario = "3", paymentMethod = "WeChatPay")
            R.id.btn_start_trans_cash -> startTrans(paymentScenario = "2")

            R.id.btn_start_trans_bankcard2 -> start2Trans(paymentScenario = INVOKE_BANKCARD_PAY_TYPE)
            R.id.btn_start_trans_scan2 -> start2Trans(paymentScenario = INVOKE_QR_B_SCAN_C, paymentMethod = "WeChatPay")
            R.id.btn_start_trans_qr2 -> start2Trans(paymentScenario = INVOKE_QR_C_SCAN_B_PAY_TYPE, paymentMethod = "WeChatPay")
            R.id.btn_start_trans_cash2 -> start2Trans(paymentScenario = INVOKE_CASH)
        }
    }

    /** Read me First !!!!
     * to start a third call transaction,you need
     * 1. Config the intent, all the data transaction need will get from Intent
     * such as: startTrans()
     * 2. StartActivityForResult, in this method, you can process the transaction result
     * such as :onActivityResult()
     * Note : Please don't change all the parameter labeled Fixed
     */
    private fun startTrans(
        paymentScenario: String,
        paymentMethod: String? = null,
        cardType: String? = null,
    ) {
        if (checkTextIsEmpty(mContext, et_amount)) return
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val intent = Intent()

        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION)
        intent.putExtra("transType", InvokeConstant.PURCHASE)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("paymentScenario", paymentScenario)
            jsonObject.put("cardType", cardType)
            jsonObject.put("paymentMethod", paymentMethod)
            jsonObject.put("amt", et_amount.text.toString())
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
                    LogUtils.setLog(TAG, "Not select printReceipt")
                }
            }
            jsonObject.put("onScreenSignature", cb_signature.isChecked)
            jsonObject.put("onScreenTip", cb_screen_tip.isChecked)
            if (operator.text.toString().isNotEmpty()) {
                jsonObject.put("operator", operator.text.toString())
            }
            if (cb_manual.isChecked) {
                jsonObject.put("entryMode", 4)
            }
            if (et_tip.text.toString().isNotEmpty() && paymentScenario != "2") {
                jsonObject.put("tip", et_tip.text.toString())
            }
            jsonObject.put("note", et_note.text.toString())
            jsonObject.put("notifyUrl", InvokeConstant.NOTIFY_URL)
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(mContext, "JSON 解析异常发生", Toast.LENGTH_SHORT).show()
        }
        val editor = sharedPreferences.edit()
        editor.putString("businessOrderNo", jsonObject.getString("businessOrderNo"))
        Log.e(TAG, "获取businessOrderNo :  ${jsonObject.getString("businessOrderNo")}")
        Log.e(TAG, "开始交易 : $jsonObject")
        editor.apply()
        startActivityForResult(intent, InvokeConstant.REQUEST_CONSUME)
    }

    private fun start2Trans(
        paymentScenario: String,
        paymentMethod: String? = null,
        cardType: String? = null,
    ) {
        if (checkTextIsEmpty(mContext, et_amount)) return
        val intent = Intent()
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSIONV2)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        intent.putExtra("topic", InvokeConstant.ECR_HUB_TOPIC_PAY)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("merchant_order_no", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("pay_scenario", paymentScenario)
            jsonObject.put("card_type", cardType)
            jsonObject.put("pay_method_id", paymentMethod)
            jsonObject.put("order_amount", et_amount.text.toString())
            jsonObject.put("on_screen_signature", cb_signature.isChecked)
            jsonObject.put("on_screen_tip", cb_screen_tip.isChecked)
            jsonObject.put("trans_type", InvokeConstant.PURCHASE)
            if (et_tip.text.toString().isNotEmpty() && paymentScenario != "2") {
                jsonObject.put("tip_amount", et_tip.text.toString())
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
                    jsonObject.put("receipt_print_mode", printReceiptIntValue)
                } else {
                    LogUtils.setLog(TAG,"Not select printReceipt")
                }
            }
            jsonObject.put("description", et_note.text.toString())
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val editor = sharedPreferences.edit()
        editor.putString("businessOrderNo", jsonObject.getString("merchant_order_no"))
        Log.e(TAG, "获取merchant_order_no :  ${jsonObject.getString("merchant_order_no")}")
        Log.e(TAG, "开始交易 : $jsonObject")
        editor.apply()
        startActivityForResult(intent, InvokeConstant.REQUEST_CONSUME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "三方调用收到信息，resultCode = $resultCode")

        if (resultCode == RESULT_OK && data != null) {
            val version = data.getStringExtra(InvokeConstant.version)
            val transType = data.getStringExtra(InvokeConstant.transType)
            val result = data.getStringExtra(InvokeConstant.result)
                ?: data.getStringExtra(InvokeConstant.BizData)
            val resultMsg = data.getStringExtra(InvokeConstant.resultMsg)
                ?: data.getStringExtra(InvokeConstant.ResponseMsg)
            val transData = data.getStringExtra(InvokeConstant.transData)
                ?: data.getStringExtra(InvokeConstant.ResponseCode)

            // 生成显示文本
            var showText = "version = $version \n"
            showText += if (transType != null) "transType = $transType \n" else ""
            showText += "result = $result \n"
            showText = resultMsg?.let { "$showText resultMsg = $it \n" } ?: showText
            showText = transData?.let { "$showText transData/responseCode = $it" } ?: showText

            Log.e(TAG, "消费结果：$showText")
            tv_result.text = showText

            // 处理成功和失败的逻辑
            if (resultMsg == "Success" ||transData == "000") {
                handleSuccessScenario()
            } else {
                handleFailureScenario()
            }
        }
    }

    // 成功时的操作逻辑
    private fun handleSuccessScenario() {
        if (autoClick) {
            runOnUiThread {
                Toast.makeText(this, "Please swipe card", Toast.LENGTH_LONG).show()
            }
            val refundIntent = Intent(this, RefundActivity::class.java)
            refundIntent.putExtra("autoClick", true)
            startActivity(refundIntent)
            LogUtils.setLog(TAG, "执行 Refund 操作")
        }
    }

    // 失败时的操作逻辑
    private fun handleFailureScenario() {
        runOnUiThread {
            Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showOperationDialog() {
        val options = arrayOf("Void", "Refund", "Query", "Reprint", "Cancel")

        AlertDialog.Builder(this)
            .setTitle("Transaction completed. Do you want to proceed?")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // 用户选择 Void 操作
                        performVoidOperation()
                    }

                    1 -> {
                        // 用户选择 Refund 操作
                        performRefundOperation()
                    }

                    2 -> {
                        // 用户选择 Query 操作
                        performQueryOperation()
                    }

                    3 -> {
                        // 用户选择 Print 操作
                        performPrintOperation()
                    }

                    4 -> {
                        // 用户选择 Cancel 操作
                    }
                }
            }
            .setCancelable(true) // 允许点击对话框外部取消
            .show()
    }

    private fun performVoidOperation() {
        // 执行 Void 操作的逻辑
        val voidIntent = Intent(this, VoidActivity::class.java)
        startActivity(voidIntent)
        LogUtils.setLog(TAG, "执行 Void 操作")
    }

    private fun performRefundOperation() {
        // 执行 Refund 操作的逻辑
        val refundIntent = Intent(this, RefundActivity::class.java)
        startActivity(refundIntent)
        LogUtils.setLog(TAG, "执行 Refund 操作")
    }

    private fun performQueryOperation() {
        // 执行 Query 操作的逻辑
        val queryIntent = Intent(this, QueryActivity::class.java)
        startActivity(queryIntent)
        LogUtils.setLog(TAG, "执行 Query 操作")
    }

    private fun performPrintOperation() {
        // 执行 Reprint 操作的逻辑
        val printIntent = Intent(this, ReprintActivity::class.java)
        startActivity(printIntent)
        LogUtils.setLog(TAG, "执行 Reprint 操作")
    }

}
