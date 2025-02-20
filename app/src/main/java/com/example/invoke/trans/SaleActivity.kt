package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_bankcard2
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_cash2
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_qr2
import kotlinx.android.synthetic.main.activity_sale.btn_start_trans_scan2
import kotlinx.android.synthetic.main.activity_sale.cb_screen_tip
import kotlinx.android.synthetic.main.activity_sale.cb_signature
import kotlinx.android.synthetic.main.activity_sale.et_amount
import kotlinx.android.synthetic.main.activity_sale.et_note
import kotlinx.android.synthetic.main.activity_sale.et_tip
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

        btn_start_trans_bankcard2.setOnClickListener(this)
        btn_start_trans_scan2.setOnClickListener(this)
        btn_start_trans_qr2.setOnClickListener(this)
        btn_start_trans_cash2.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_trans_bankcard2 -> start2Trans(paymentScenario = INVOKE_BANKCARD_PAY_TYPE)
            R.id.btn_start_trans_scan2 -> start2Trans(
                paymentScenario = INVOKE_QR_B_SCAN_C,
                paymentMethod = "WeChatPay"
            )

            R.id.btn_start_trans_qr2 -> start2Trans(
                paymentScenario = INVOKE_QR_C_SCAN_B_PAY_TYPE,
                paymentMethod = "WeChatPay"
            )

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
    private fun start2Trans(
        paymentScenario: String,
        paymentMethod: String? = null,
    ) {
        if (checkTextIsEmpty(mContext, et_amount)) return
        val intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION2)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        intent.putExtra("topic", InvokeConstant.ECR_HUB_TOPIC_PAY)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("trans_type", InvokeConstant.PURCHASE)
            jsonObject.put("merchant_order_no", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("order_amount", et_amount.text.toString())
            if (et_tip.text.toString().isNotEmpty() && paymentScenario != "2") {
                jsonObject.put("tip_amount", et_tip.text.toString())
            }
            jsonObject.put("on_screen_tip", cb_screen_tip.isChecked)
            jsonObject.put("on_screen_signature", cb_signature.isChecked)
            jsonObject.put("pay_method_id", paymentMethod)
            jsonObject.put("pay_scenario", paymentScenario)
            jsonObject.put("card_network_type", "2")
            jsonObject.put("description", et_note.text.toString())
            jsonObject.put("notify_url", "your notify url")
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        startActivityForResult(intent, InvokeConstant.REQUEST_CONSUME)
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

}
