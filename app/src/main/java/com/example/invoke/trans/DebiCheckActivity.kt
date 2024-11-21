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
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import com.example.invoke.utils.ViewUtil.checkTextIsEmpty
import kotlinx.android.synthetic.main.activity_debicheck.*
import org.json.JSONException
import org.json.JSONObject


class DebiCheckActivity : Activity(), View.OnClickListener {

    private val TAG = "DebiCheckActivity"

    private lateinit var mContext: Context

    private lateinit var radioGroup: RadioGroup

    private var autoClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debicheck)
        mContext = this
        btn_start_trans.setOnClickListener(this)


    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_trans_bankcard -> startTrans(paymentScenario = "1", cardType = "2")
            R.id.btn_start_trans_scan -> startTrans(
                paymentScenario = "4",
                paymentMethod = "WeChatPay"
            )

            R.id.btn_start_trans_qr -> startTrans(
                paymentScenario = "3",
                paymentMethod = "WeChatPay"
            )

            R.id.btn_start_trans_cash -> startTrans(paymentScenario = "2")
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
//            jsonObject.put("OnScreenTip", cb_screen_tip.isChecked)
//            if (et_tip.text.toString().isNotEmpty() && paymentScenario != "2") {
//                jsonObject.put("tip", et_tip.text.toString())
//            }
            jsonObject.put("note", et_note.text.toString())
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
            Log.e(TAG, "消费结果：$showText")
            tv_result.text = showText

            // 等待 3 秒
//            Thread.sleep(3000)

            if (resultMsg == "Success") {
                if (autoClick) {
                    runOnUiThread {
                        Toast.makeText(this, "Please swipe card", Toast.LENGTH_LONG).show()
                    }
                    val refundIntent = Intent(this, RefundActivity::class.java)
                    refundIntent.putExtra("autoClick", true)
                    startActivity(refundIntent)
                    LogUtils.setLog(TAG, "执行 Refund 操作")
                }
            } else {
                showOperationDialog()
                runOnUiThread {
                    Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show()
                }
            }
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
