package com.adapty.models

data class ProductSubscriptionPeriodModel(
    val unit: PeriodUnit?,
    val numberOfUnits: Int?
)

enum class PeriodUnit(val period: String) {
    D("day"),
    W("week"),
    M("month"),
    Y("year")
}
