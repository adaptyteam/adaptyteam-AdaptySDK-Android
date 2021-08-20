package com.adapty.models

import java.math.BigDecimal

data class ProductDiscountModel(
    val price: BigDecimal,
    val numberOfPeriods: Int,
    val localizedPrice: String,
    val subscriptionPeriod: ProductSubscriptionPeriodModel
)
