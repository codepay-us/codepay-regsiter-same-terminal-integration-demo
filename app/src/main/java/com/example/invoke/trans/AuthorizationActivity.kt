package com.example.invoke.trans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.invoke.utils.ViewUtil
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.constant.InvokeConstant.INVOKE_BANKCARD_PAY_TYPE
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import kotlinx.android.synthetic.main.activity_authorization.*
import kotlinx.android.synthetic.main.activity_sale.cb_screen_tip
import kotlinx.android.synthetic.main.activity_sale.cb_signature
import kotlinx.android.synthetic.main.activity_sale.et_amount
import kotlinx.android.synthetic.main.activity_sale.et_note
import kotlinx.android.synthetic.main.activity_sale.et_tip
import kotlinx.android.synthetic.main.activity_sale.tv_result
import org.json.JSONException
import org.json.JSONObject

/**
 *
 * @ProjectName: AddPayInvokeDemo
 * @Package: com.example.addpayinvokedemo.trans
 * @ClassName: PreAuthActivity
 * @Author: dongwei
 * @Version: 1.0
 */
class AuthorizationActivity : Activity(), View.OnClickListener {

    private val TAG = AuthorizationActivity::class.java.simpleName
    private lateinit var radioGroup: RadioGroup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        btn_start_trans.setOnClickListener(this)
        btn_start_trans.text = "Auth"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_trans -> startTrans()
        }
    }

    private fun startTrans() {
        if (ViewUtil.checkTextIsEmpty(this, et_amount)) return

        var intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSIONV2)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        var jsonObject = JSONObject()
        try {
            jsonObject.put("merchant_order_no", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("pay_scenario", INVOKE_BANKCARD_PAY_TYPE)
            jsonObject.put("card_network_type", "2")
            jsonObject.put("order_amount", et_amount.text.toString())
            jsonObject.put("on_screen_signature", cb_signature.isChecked)
            jsonObject.put("on_screen_tip", cb_screen_tip.isChecked)
            jsonObject.put("trans_type", InvokeConstant.PRE_AUTH)
            if (et_tip.text.toString().isNotEmpty()) {
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
                    LogUtils.setLog(TAG, "Not select printReceipt")
                }
            }
            jsonObject.put("description", et_note.text.toString())
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        startActivityForResult(intent, InvokeConstant.REQUEST_PREAUTH)
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

    private fun performCompleteOperation() {
        // 执行 Complete 操作的逻辑
        val completeIntent = Intent(this, CompletionActivity::class.java)
        completeIntent.putExtra("autoClick", true)
        startActivity(completeIntent)
        LogUtils.setLog(TAG, "执行 Complete 操作")
    }

}