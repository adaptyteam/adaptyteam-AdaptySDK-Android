package com.adapty.models

data class NonSubscriptionInfoModel(
    val purchaseId: String,
    val vendorProductId: String,
    val vendorTransactionId: String?,
    val store: String,
    val purchasedAt: String?,
    val isOneTime: Boolean,
    val isSandbox: Boolean,
    val isRefund: Boolean
)
