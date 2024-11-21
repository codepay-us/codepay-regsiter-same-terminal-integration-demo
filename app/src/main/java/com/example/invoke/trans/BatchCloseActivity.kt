package com.example.invoke.trans

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.invoke.R
import com.example.invoke.constant.InvokeConstant
import com.example.invoke.constant.InvokeConstant.ECR_HUB_TOPIC_BATCH_CLOSE
import com.example.invoke.utils.DateUtil
import kotlinx.android.synthetic.main.activity_sale.et_amount
import kotlinx.android.synthetic.main.activity_sale.tv_result
import kotlinx.android.synthetic.main.amount_layout.btn_start_trans
import org.json.JSONException
import org.json.JSONObject

class BatchCloseActivity : Activity(), View.OnClickListener {

    private val TAG = "invoke Balance"

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bach_close)
        mContext = this
        btn_start_trans.setOnClickListener(this)
        et_amount.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_trans -> start2Trans()
        }
    }

    private fun startTrans() {

        val intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION1)
        intent.putExtra("transType", InvokeConstant.BATCH_CLOSE)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            intent.putExtra("transData", jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        startActivityForResult(intent, InvokeConstant.REQUEST_BALANCE_INQUIRY)
    }

    private fun start2Trans() {

        val intent = Intent()
        intent.action = InvokeConstant.CASHIER_ACTION
        intent.putExtra("version", InvokeConstant.VERSION1)
        intent.putExtra("transType", InvokeConstant.BATCH_CLOSE)
        intent.putExtra("appId", InvokeConstant.APP_ID)
        intent.putExtra("topic", ECR_HUB_TOPIC_BATCH_CLOSE)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("businessOrderNo", DateUtil.getCurDateStr("yyyyMMddHHmmss"))
            intent.putExtra("biz_data", jsonObject.toString())
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
            if ("00" == data?.getStringExtra("result")) {
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