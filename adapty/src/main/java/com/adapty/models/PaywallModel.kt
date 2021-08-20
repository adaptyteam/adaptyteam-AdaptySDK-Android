package com.adapty.models

data class PaywallModel(
    val developerId: String,
    val name: String?,
    val abTestName: String?,
    val revision: Int,
    val isPromo: Boolean,
    val variationId: String,
    val products: List<ProductModel>,
    val customPayloadString: String?,
    val customPayload: Map<String, Any>?,
    val visualPaywall: String?,
)
