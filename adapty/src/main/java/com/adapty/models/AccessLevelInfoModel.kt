package com.adapty.models

data class AccessLevelInfoModel(
    val id: String,
    val isActive: Boolean,
    val vendorProductId: String,
    val vendorTransactionId: String?,
    val vendorOriginalTransactionId: String?,
    val store: String,
    val activatedAt: String?,
    val startsAt: String?,
    val renewedAt: String?,
    val expiresAt: String?,
    val isLifetime: Boolean,
    val cancellationReason: String?,
    val isRefund: Boolean,
    val activeIntroductoryOfferType: String?,
    val activePromotionalOfferType: String?,
    val willRenew: Boolean,
    val isInGracePeriod: Boolean,
    val unsubscribedAt: String?,
    val billingIssueDetectedAt: String?
)
