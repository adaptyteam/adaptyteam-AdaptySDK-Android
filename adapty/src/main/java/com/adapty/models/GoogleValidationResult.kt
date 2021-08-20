package com.adapty.models

import com.google.gson.annotations.SerializedName

data class GoogleValidationResult(
    @SerializedName("kind")
    val kind: String?,
    @SerializedName("startTimeMillis")
    val startTimeMillis: String?,
    @SerializedName("expiryTimeMillis")
    val expiryTimeMillis: String?,
    @SerializedName("autoResumeTimeMillis")
    val autoResumeTimeMillis: String?,
    @SerializedName("autoRenewing")
    val autoRenewing: Boolean?,
    @SerializedName("priceCurrencyCode")
    val priceCurrencyCode: String?,
    @SerializedName("priceAmountMicros")
    val priceAmountMicros: String?,
    @SerializedName("introductoryPriceInfo")
    val introductoryPriceInfo: IntroductoryPriceInfo?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("developerPayload")
    val developerPayload: String?,
    @SerializedName("paymentState")
    val paymentState: Int?,
    @SerializedName("cancelReason")
    val cancelReason: Int?,
    @SerializedName("userCancellationTimeMillis")
    val userCancellationTimeMillis: String?,
    @SerializedName("cancelSurveyResult")
    val cancelSurveyResult: SubscriptionCancelSurveyResult?,
    @SerializedName("orderId")
    val orderId: String?,
    @SerializedName("linkedPurchaseToken")
    val linkedPurchaseToken: String?,
    @SerializedName("purchaseType")
    val purchaseType: Int?,
    @SerializedName("priceChange")
    val priceChange: SubscriptionPriceChange?,
    @SerializedName("profileName")
    val profileName: String?,
    @SerializedName("emailAddress")
    val emailAddress: String?,
    @SerializedName("givenName")
    val givenName: String?,
    @SerializedName("familyName")
    val familyName: String?,
    @SerializedName("profileId")
    val profileId: String?,
    @SerializedName("acknowledgementState")
    val acknowledgementState: Int?,
    @SerializedName("externalAccountId")
    val externalAccountId: String?,
    @SerializedName("promotionType")
    val promotionType: Int?,
    @SerializedName("promotionCode")
    val promotionCode: String?,
    @SerializedName("obfuscatedExternalAccountId")
    val obfuscatedExternalAccountId: String?,
    @SerializedName("obfuscatedExternalProfileId")
    val obfuscatedExternalProfileId: String?,
    @SerializedName("purchaseTimeMillis")
    val purchaseTimeMillis: String?,
    @SerializedName("purchaseState")
    val purchaseState: Int?,
    @SerializedName("consumptionState")
    val consumptionState: Int?,
    @SerializedName("purchaseToken")
    val purchaseToken: String?,
    @SerializedName("productId")
    val productId: String?,
    @SerializedName("quantity")
    val quantity: Int?,
    @SerializedName("regionCode")
    val regionCode: String?,
)

data class IntroductoryPriceInfo(
    @SerializedName("introductoryPriceCurrencyCode")
    val introductoryPriceCurrencyCode: String?,
    @SerializedName("introductoryPriceAmountMicros")
    val introductoryPriceAmountMicros: String?,
    @SerializedName("introductoryPricePeriod")
    val introductoryPricePeriod: String?,
    @SerializedName("introductoryPriceCycles")
    val introductoryPriceCycles: Int?,
)

data class SubscriptionCancelSurveyResult(
    @SerializedName("userInputCancelReason")
    val userInputCancelReason: String?,
    @SerializedName("cancelSurveyReason")
    val cancelSurveyReason: Int?,
)

data class SubscriptionPriceChange(
    @SerializedName("newPrice")
    val newPrice: Price?,
    @SerializedName("state")
    val state: Int?,
) {

    data class Price(
        @SerializedName("priceMicros")
        val priceMicros: String?,
        @SerializedName("currency")
        val currency: String?,
    )
}
