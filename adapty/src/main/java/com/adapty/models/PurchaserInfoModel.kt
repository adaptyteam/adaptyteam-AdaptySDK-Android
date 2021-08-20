package com.adapty.models

data class PurchaserInfoModel(
    val customerUserId: String?,
    val accessLevels: Map<String, AccessLevelInfoModel>,
    val subscriptions: Map<String, SubscriptionInfoModel>,
    val nonSubscriptions: Map<String, List<NonSubscriptionInfoModel>>,
)
