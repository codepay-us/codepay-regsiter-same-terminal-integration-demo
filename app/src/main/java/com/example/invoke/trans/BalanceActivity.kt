package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.utils.DateUtil
import kotlinx.android.synthetic.main.activity_sale.et_amount
import kotlinx.android.synthetic.main.activity_sale.tv_result
import kotlinx.android.synthetic.main.amount_layout.*
import org.json.JSONException
import org.json.JSONObject

class BalanceActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke Balance"

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance)
        mContext = this
        btn_start_trans.setOnClickListener(this)
        et_amount.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_trans -> startTrans()
        }
    }

    private fun startTrans() {

        var intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", "A01")
        intent.putExtra("transType", InvokeConstant.BALANCE)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        var jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            jsonObject.put("paymentScenario", "CARD")
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        startActivityForResult(intent, InvokeConstant.REQUEST_BALANCE_INQUIRY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(
            TAG, "requestCode:$requestCode," +
                    "resultCode:$resultCode ," +
                    "result:${data?.getStringExtra("result")}," +
                    "resultMsg:${data?.getStringExtra("resultMsg")}," +
                    "transType:${data?.getStringExtra("transType")}"
        )

        Log.e(TAG, "transData:${data?.getStringExtra("transData")}")
        if (resultCode == RESULT_OK) {
            if ("0" == data?.getStringExtra("result")) {
                Log.e(TAG, "0")
                tv_result.text = "requestCode : $requestCode\n " +
                        "resultCode : $resultCode\n" +
                        "transType : ${data.getStringExtra("transType")}\n" +
                        "result :  ${data.getStringExtra("result")}\n" +
                        "transData : ${data.getStringExtra("transData")}"
            } else {
                Log.e(TAG, "-1")
                tv_result.text = "requestCode : $requestCode\n" +
                        "resultCode : $resultCode\n " +
                        "transType : ${data?.getStringExtra("transType")}\n" +
                        "result : ${data?.getStringExtra("result")}\n" +
                        "resultMsg : ${data?.getStringExtra("resultMsg")}\n"
            }
        }
    }
}