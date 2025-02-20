package com.example.invoke.trans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_void.btn_start_trans_void
import kotlinx.android.synthetic.main.activity_void.et_ori_business_order_no
import org.json.JSONException
import org.json.JSONObject

class ReprintActivity  : Activity(), View.OnClickListener {
    private val TAG = "ReprintActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_void)
        btn_start_trans_void.setOnClickListener(this)
        btn_start_trans_void.text = "Reprint"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_trans_void -> startTrans2()
        }
    }

    private fun startTrans2() {
        if (ViewUtil.checkTextIsEmpty(this, et_ori_business_order_no)) return
        val intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION2)
        intent.putExtra("topic", InvokeConstant.ECR_HUB_TOPIC_REPRINT)
        intent.putExtra("app_id", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("merchant_order_no", et_ori_business_order_no.text.toString())
            intent.putExtra("biz_data", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e(TAG, "开始交易 : $jsonObject")
        startActivityForResult(intent, InvokeConstant.REQUEST_VOID)
    }

}