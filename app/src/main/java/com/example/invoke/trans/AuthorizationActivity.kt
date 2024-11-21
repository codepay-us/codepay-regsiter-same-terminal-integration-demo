package com.example.invoke.trans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.invoke.utils.ViewUtil
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import kotlinx.android.synthetic.main.activity_authorization.*
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

    private var autoClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        autoClick = intent.getBooleanExtra("autoClick", false)
        LogUtils.setLog(TAG, "$autoClick")
        // 检查是否通过特定方式启动页面
        if (autoClick) {
            // 自动点击银行卡支付按钮
            val handler = Handler()
            handler.postDelayed({
                val amount = "150.87"
                et_amount.setText(amount)
                btn_start_trans.performClick()
            }, 2000) // 延迟2秒，确保页面加载完成后再点击按钮
        }
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
        intent.putExtra("version", InvokeConstant.VERSION)
        intent.putExtra("transType", InvokeConstant.PRE_AUTH)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        var jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("cardType", "2")//Credit
            jsonObject.put("amt", et_amount.text.toString())
            jsonObject.put("notifyUrl", InvokeConstant.NOTIFY_URL)
            jsonObject.put("onScreenSignature", cb_signature.isChecked)
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
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("businessOrderNo", jsonObject.getString("businessOrderNo"))
        Log.e(TAG,"获取businessOrderNo :  ${jsonObject.getString("businessOrderNo")}")
        Log.e(TAG, "开始交易 : $jsonObject")
        editor.apply()
        startActivityForResult(intent, InvokeConstant.REQUEST_PREAUTH)
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
            Log.e(TAG, "预授权结果：$showText")
            tv_result.text = showText
            // 等待 3 秒
//            Thread.sleep(3000)
            if (resultMsg == "Success" && autoClick) {
                performCompleteOperation()
            }
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