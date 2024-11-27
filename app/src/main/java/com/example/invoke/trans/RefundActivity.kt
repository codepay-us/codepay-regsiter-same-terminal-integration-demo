package com.example.invoke.trans

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.invoke.utils.ViewUtil.getAmount
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.constant.InvokeConstant.INVOKE_BANKCARD_PAY_TYPE
import com.example.invoke.utils.DateUtil
import com.example.invoke.utils.LogUtils
import com.example.invoke.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_refund.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat


class RefundActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke--RefundActivity"
    private lateinit var radioGroup: RadioGroup

    private lateinit var mContext: Context
    private var selectDate = DateUtil.StringToDate(DateUtil.getCurDateStr("yyyyMMdd"), "yyyyMMdd")
    private val format = SimpleDateFormat("yyyyMMdd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refund)
        Log.e(TAG, "onCreate")
        mContext = this
        btn_refund_bankcard2.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
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
                if (ViewUtil.checkTextIsEmpty(this, et_origin_business_no)) return
                jsonObject.put("orig_merchant_order_no", et_origin_business_no.text.toString())
            }
            jsonObject.put("pay_scenario", paymentScenario)
            jsonObject.put("notify_url", InvokeConstant.NOTIFY_URL)
            jsonObject.put("merchant_order_no", DateUtil.getCurDateStr("yyyyMMddHHmmss"))

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
        }
    }

}
