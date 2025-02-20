package com.example.invoke.constant

object InvokeConstant {

    const val ECR_HUB_TOPIC_PAY = "ecrhub.pay.order"

    const val ECR_HUB_TOPIC_TIP_ADJUSTMENT = "ecrhub.pay.tip.adjustment"

    const val ECR_HUB_TOPIC_BATCH_CLOSE = "ecrhub.pay.batch.close"

    const val ECR_HUB_TOPIC_QUERY = "ecrhub.pay.query"

    const val ECR_HUB_TOPIC_REPRINT = "ecrhub.pay.reprint"

    /**
     * 银行卡支付方式
     */
    const val INVOKE_BANKCARD_PAY_TYPE = "SWIPE_CARD"

    /**
     * 扫码主扫
     */
    const val INVOKE_QR_C_SCAN_B_PAY_TYPE = "SCANQR_PAY"

    /**
     * 扫码被扫
     */
    const val INVOKE_QR_B_SCAN_C = "BSCANQR_PAY"

    /**
     * 扫码被扫
     */
    const val INVOKE_CASH = "CASH_PAY"
    val ACTION_NONE = 0;
    val ACTION_CONSUME = 2;
    val ACTION_REFUND = 9;
    val ACTION_QUERY = 5;
    val ACTION_SETTLE = 4;
    val ACTION_PRINT = 16;
    val RESULT_SUCCESS = 0;
    val RESULT_FAIL_COMMON = -1;
    val RESULT_FAIL_NO_NEED_BREAK = 1001;
    val PAY_TYPE_BANK_CARD = "1001";
    val PAY_TYPE_WX_SCAN = "1002";
    val PAY_TYPE_WX_CODE = "1003";
    val PAY_TYPE_ALI_SCAN = "1004";
    val PAY_TYPE_ALI_CODE = "1005";


    //RequestCode
    val REQUEST_CONSUME = 100;
    val REQUEST_VOID = 101;
    val REQUEST_REFUND = 102;
    val REQUEST_BALANCE_INQUIRY = 103;
    val REQUEST_CASH_BACK = 104;
    val REQUEST_CASH_ADVANCE = 105;
    val REQUEST_PREAUTH = 106;
    val REQUEST_PREAUTH_COMPLETE = 107;
    val REQUEST_PREAUTH_CANCEL = 108;
    val REQUEST_PREAUTH_COMPLETE_CANCEL = 109;
    val REQUEST_SETTLEMENT = 110;

    //FIXED
    //Please don't change these parameter,otherwise the transaction will be failed.
    const val PURCHASE = "1"
    const val VOID = "2"
    const val REFUND = "3"
    const val PRE_AUTH = "4"
    const val PRE_AUTH_COMPLETE = "6"
    const val BALANCE = "BALANCE"
    const val CASH_BACK = "11"
    const val BATCH_CLOSE = "23"
    const val TIP_ADJUSTMENT = "24"
    const val QUERY = "21"
    const val REPRINT = "22"

    //params
    const val CASHIER_PACKAGE = "com.codepay.register"
    const val CASHIER_ACTION = "com.codepay.transaction.call"
    const val VERSION = "1.1"
    const val VERSION1 = "1.1"
    const val VERSION2 = "2.0"
    const val NOTIFY_URL = "Your NOTIFY_URL"
    const val APP_ID = "wz2b6cef2f18008ee7"
    /**
     * result data key
     */
    const val version: String = "version"
    const val transType = "transType"
    const val transData = "transData"
    const val result = "result"
    const val resultMsg = "resultMsg"

    const val BizData = "biz_data"
    const val ResponseCode = "response_code"
    const val ResponseMsg = "response_msg"
}