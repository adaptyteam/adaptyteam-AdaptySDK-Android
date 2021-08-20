package com.adapty.models

data class SubscriptionInfoModel(
    val isActive: Boolean,
    val vendorProductId: String,
    val vendorTransactionId: String?,
    val vendorOriginalTransactionId: String?,
    val store: String,
    val activatedAt: String?,
    val renewedAt: String?,
    val expiresAt: String?,
    val startsAt: String?,
    val isLifetime: Boolean,
    val activeIntroductoryOfferType: String?,
    val activePromotionalOfferType: String?,
    val willRenew: Boolean,
    val isInGracePeriod: Boolean,
    val unsubscribedAt: String?,
    val billingIssueDetectedAt: String?,
    val isSandbox: Boolean,
    val isRefund: Boolean,
    val cancellationReason: String?
)
