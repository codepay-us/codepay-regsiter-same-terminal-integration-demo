package com.example.invoke

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.invoke.trans.*
import com.example.invoke.utils.LogUtils

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
        findViewById<Button>(R.id.btn_tip_adjustment).setOnClickListener(this)
        findViewById<Button>(R.id.btn_batch_close).setOnClickListener(this)
        findViewById<Button>(R.id.btn_batch_reprint).setOnClickListener(this)
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

            R.id.btn_batch_reprint -> {
                startActivity(Intent(this, ReprintActivity::class.java))
            }
        }
    }
}