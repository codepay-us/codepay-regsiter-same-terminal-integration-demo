package com.example.invoke

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.invoke.trans.*
import com.example.invoke.utils.LogUtils
import kotlinx.android.synthetic.main.activity_main.tv_result

class MainActivity : Activity(), View.OnClickListener {

    val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.setLog(TAG, "onCreate")
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        LogUtils.setLog(TAG, "initView")
        findViewById<Button>(R.id.btn_sale_trans).setOnClickListener(this)
        findViewById<Button>(R.id.btn_void).setOnClickListener(this)
        findViewById<Button>(R.id.btn_refund_trans).setOnClickListener(this)
        findViewById<Button>(R.id.btn_cash_back).setOnClickListener(this)
        findViewById<Button>(R.id.btn_authorization_trans).setOnClickListener(this)
        findViewById<Button>(R.id.btn_completion_trans).setOnClickListener(this)
        findViewById<Button>(R.id.btn_query).setOnClickListener(this)
        findViewById<Button>(R.id.btn_test).setOnClickListener(this)
        findViewById<Button>(R.id.btn_get).setOnClickListener(this)
        findViewById<Button>(R.id.btn_tip_adjustment).setOnClickListener(this)
        findViewById<Button>(R.id.btn_batch_close).setOnClickListener(this)
    }

    // start third call test
    // To enter every trans type test activity, you can click the button
    override fun onClick(view: View) {
        LogUtils.setLog(TAG, "onClick")
        when (view.id) {
            //PURCHASE
            // there are all parameter detail descriptions in "purchase" transaction
            // other transaction type also like this one
            R.id.btn_sale_trans -> {
                startActivity(Intent(this, SaleActivity::class.java))
            }
            //VOID
            R.id.btn_void -> {
                startActivity(Intent(this, VoidActivity::class.java))
            }
            //REFUND
            R.id.btn_refund_trans -> {
                startActivity(Intent(this, RefundActivity::class.java))
            }
            //CASH BACK
            R.id.btn_cash_back -> {
                startActivity(Intent(this, SaleWithCashbackActivity::class.java))
            }
            //PRE AUTH
            R.id.btn_authorization_trans -> {
                startActivity(Intent(this, AuthorizationActivity::class.java))
            }
            //PRE AUTH COMPLETE
            R.id.btn_completion_trans -> {
                startActivity(Intent(this, CompletionActivity::class.java))
            }

            R.id.btn_query -> {
                startActivity(Intent(this, QueryActivity::class.java))
            }

            R.id.btn_tip_adjustment -> {
                startActivity(Intent(this, TipAdjustmentActivity::class.java))
            }

            R.id.btn_batch_close -> {
                startActivity(Intent(this, BatchCloseActivity::class.java))
            }

            R.id.btn_test -> {
                runOnUiThread {
                    Toast.makeText(this, "Please prepare your card", Toast.LENGTH_LONG).show()
                }
                val intent = Intent(this, SaleActivity::class.java)
                intent.putExtra("autoClick", true) // 设置标志位
                startActivity(intent)
            }

            R.id.btn_get -> {
                val availableMemory = getAvailableMemory(this)
                val totalMemory = getTotalMemory(this)
                val availableStorage = getAvailableStorage()
                val totalStorage = getTotalStorage()

                val memoryInfo = "Memory: Available ${formatSize(availableMemory)}, Total ${
                    formatSize(totalMemory)
                }"
                val storageInfo = "Storage: Available ${formatSize(availableStorage)}, Total ${
                    formatSize(totalStorage)
                }"
                Log.e(TAG, "$memoryInfo\n$storageInfo")

                runOnUiThread {
                    Toast.makeText(this, "$memoryInfo\n$storageInfo", Toast.LENGTH_LONG).show()
                    tv_result.text = memoryInfo + storageInfo
                }
            }
        }
    }

    fun getAvailableMemory(context: Context): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem
    }

    fun getTotalMemory(context: Context): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem
    }

    fun getAvailableStorage(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.availableBytes
    }

    fun getTotalStorage(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.totalBytes
    }

    fun formatSize(size: Long): String {
        val sizeInGb = size / (1024 * 1024 * 1024).toFloat()
        return String.format("%.2f GB", sizeInGb)
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
//                tv_result_consume.text = "requestCode : $requestCode\n " +
//                        "resultCode : $resultCode\n " +
//                        "transType : ${data.getStringExtra("transType")}\n" +
//                        "result :  ${data.getStringExtra("result")}\n" +
//                        "transData : ${data.getStringExtra("transData")}"
            } else {
                Log.e(TAG, "-1")
//                tv_result_consume.text = "requestCode : $requestCode\n" +
//                        "resultCode : $resultCode\n " +
//                        "transType:${data?.getStringExtra("transType")}" +
//                        "result : ${data?.getStringExtra("result")}\n" +
//                        "resultMsg : ${data?.getStringExtra("resultMsg")}\n"
            }
        }
    }
}