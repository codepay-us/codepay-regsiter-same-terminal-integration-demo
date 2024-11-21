package com.example.invoke.trans

//import kotlinx.android.synthetic.main.activity_void.cb_signature
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.LogUtils
import kotlinx.android.synthetic.main.activity_void.*
import org.json.JSONException
import org.json.JSONObject

class ReprintActivity : Activity(), View.OnClickListener {

    private val TAG = "ReprintActivity"

    private var autoClick: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_void)
//        cb_signature.visibility = View.GONE
//        cb_not_read_card.visibility = View.GONE
        radioGroup.visibility = View.GONE
        autoClick = intent.getBooleanExtra("autoClick", false)
        LogUtils.setLog(TAG, "$autoClick")
        if (autoClick) {
            // 自动点击银行卡支付按钮
            val handler = Handler()
            handler.postDelayed({
                btn_start_trans_void.performClick()
            }, 1000) // 延迟1秒，确保页面加载完成后再点击按钮
        }
        btn_start_trans_void.setOnClickListener(this)
        btn_start_trans_void.text = "Reprint"
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
        intent.putExtra("transType", InvokeConstant.REPRINT)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            if (et_ori_business_order_no.text.toString().isNotEmpty()){
                jsonObject.put("businessOrderNo", et_ori_business_order_no.text.toString())
            } else {
                jsonObject.put("businessOrderNo", sharedPreferences.getString("businessOrderNo","").toString())
                Log.e(TAG,"使用上一笔交易订单号 : ${sharedPreferences.getString("businessOrderNo","").toString()}")
            }
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
            Log.e(TAG, "打印结果：$showText")
            tv_result.text = showText
        }
    }
}