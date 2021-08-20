package com.adapty.models

data class PromoModel(
    val promoType: String,
    val variationId: String,
    val expiresAt: String?,
    val paywall: PaywallModel?,
)
