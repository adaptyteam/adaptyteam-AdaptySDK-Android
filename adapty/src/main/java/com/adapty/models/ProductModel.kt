package com.adapty.models

import com.android.billingclient.api.SkuDetails
import java.math.BigDecimal

data class ProductModel(
    val vendorProductId: String,
    val localizedTitle: String,
    val localizedDescription: String,
    val paywallName: String?,
    val paywallABTestName: String?,
    val variationId: String?,
    val price: BigDecimal,
    val localizedPrice: String?,
    val currencyCode: String?,
    val currencySymbol: String?,
    val subscriptionPeriod: ProductSubscriptionPeriodModel?,
    val introductoryOfferEligibility: Boolean,
    val introductoryDiscount: ProductDiscountModel?,
    val freeTrialPeriod: ProductSubscriptionPeriodModel?,
    val skuDetails: SkuDetails?
)
