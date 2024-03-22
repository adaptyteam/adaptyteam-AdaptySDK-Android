package com.adapty.internal.di

import android.content.Context
import androidx.annotation.RestrictTo
import com.adapty.internal.AdaptyInternal
import com.adapty.internal.data.cache.CacheEntityTypeAdapterFactory
import com.adapty.internal.data.cache.CacheRepository
import com.adapty.internal.data.cache.PreferenceManager
import com.adapty.internal.data.cache.ResponseCacheKeyProvider
import com.adapty.internal.data.cloud.*
import com.adapty.internal.data.models.*
import com.adapty.internal.data.models.requests.SendEventRequest
import com.adapty.internal.domain.AuthInteractor
import com.adapty.internal.domain.ProductsInteractor
import com.adapty.internal.domain.ProfileInteractor
import com.adapty.internal.domain.PurchasesInteractor
import com.adapty.internal.utils.*
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.sync.Semaphore
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Format
import java.util.*
import kotlin.LazyThreadSafetyMode.NONE

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal object Dependencies {
    internal inline fun <reified T> inject(named: String? = null) = lazy(NONE) {
        injectInternal<T>(named)
    }

    private inline fun <reified T> injectInternal(named: String? = null) =
        (map[T::class.java]!![named] as DIObject<T>).provide()

    @get:JvmSynthetic
    internal val map = hashMapOf<Class<*>, Map<String?, DIObject<*>>>()

    private const val BASE = "base"
    private const val ANALYTICS = "analytics"
    private const val RECORD_ONLY = "record_only"
    private const val LOCAL = "local"
    private const val REMOTE = "remote"

    private fun <T> singleVariantDiObject(
        initializer: () -> T,
        initType: DIObject.InitType = DIObject.InitType.SINGLETON
    ): Map<String?, DIObject<T>> = mapOf(null to DIObject(initializer, initType))

    @JvmSynthetic
    internal fun init(appContext: Context, apiKey: String, observerMode: Boolean) {
        map.putAll(
            listOf(
                Gson::class.java to mapOf(
                    BASE to DIObject({
                        val dataKey = "data"
                        val attributesKey = "attributes"
                        val metaKey = "meta"
                        val paywallsKey = "paywalls"
                        val productsKey = "products"
                        val versionKey = "version"
                        val profileKey = "profile"
                        val errorsKey = "errors"

                        val attributesObjectExtractor = ResponseDataExtractor { jsonElement ->
                            ((jsonElement as? JsonObject)?.get(dataKey) as? JsonObject)
                                ?.get(attributesKey) as? JsonObject
                        }
                        val dataArrayExtractor = ResponseDataExtractor { jsonElement ->
                            (jsonElement as? JsonObject)?.get(dataKey) as? JsonArray
                        }
                        val dataObjectExtractor = ResponseDataExtractor { jsonElement ->
                            (jsonElement as? JsonObject)?.get(dataKey) as? JsonObject
                        }
                        val fallbackPaywallsExtractor = ResponseDataExtractor { jsonElement ->
                            val paywalls = JsonArray()

                            ((jsonElement as? JsonObject)?.get(dataKey) as? JsonArray)
                                ?.forEach { element ->
                                    ((element as? JsonObject)?.get(attributesKey) as? JsonObject)
                                        ?.let(paywalls::add)
                                }

                            val meta = (jsonElement as? JsonObject)?.get(metaKey) as? JsonObject

                            val products = (meta?.get(productsKey) as? JsonArray) ?: JsonArray()

                            val version = (meta?.get(versionKey) as? JsonPrimitive) ?: JsonPrimitive(0)

                            JsonObject().apply {
                                add(paywallsKey, paywalls)
                                add(productsKey, products)
                                add(versionKey, version)
                            }
                        }
                        val validationResultExtractor = ResponseDataExtractor { jsonElement ->
                            (((jsonElement as? JsonObject)?.get(dataKey) as? JsonObject)
                                ?.get(attributesKey) as? JsonObject)?.let { result ->

                                val errors = (result.remove(errorsKey) as? JsonArray) ?: JsonArray()

                                JsonObject().apply {
                                    add(profileKey, result)
                                    add(errorsKey, errors)
                                }
                            }
                        }

                        GsonBuilder()
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    TypeToken.get(PaywallDto::class.java),
                                    attributesObjectExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    TypeToken.get(ViewConfigurationDto::class.java),
                                    dataObjectExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    TypeToken.get(AnalyticsConfig::class.java),
                                    dataObjectExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    TypeToken.get(ProfileDto::class.java),
                                    attributesObjectExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    object : TypeToken<ArrayList<ProductDto>>() {},
                                    dataArrayExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    object : TypeToken<ArrayList<String>>() {},
                                    dataArrayExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    TypeToken.get(FallbackPaywalls::class.java),
                                    fallbackPaywallsExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                AdaptyResponseTypeAdapterFactory(
                                    TypeToken.get(ValidationResult::class.java),
                                    validationResultExtractor,
                                )
                            )
                            .registerTypeAdapterFactory(
                                CacheEntityTypeAdapterFactory()
                            )
                            .registerTypeAdapterFactory(
                                CreateOrUpdateProfileRequestTypeAdapterFactory()
                            )
                            .registerTypeAdapter(
                                SendEventRequest::class.java,
                                SendEventRequestSerializer()
                            )
                            .registerTypeAdapter(
                                AnalyticsEvent::class.java,
                                AnalyticsEventTypeAdapter()
                            )
                            .registerTypeAdapter(
                                AnalyticsData::class.java,
                                AnalyticsDataTypeAdapter()
                            )
                            .registerTypeAdapter(
                                BigDecimal::class.java,
                                BigDecimalDeserializer()
                            )
                            .create()
                    }),
                    ANALYTICS to DIObject({
                        GsonBuilder()
                            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                            .create()
                    })
                ),

                Format::class.java to singleVariantDiObject({
                    DecimalFormat("0.00", DecimalFormatSymbols(Locale.US))
                }),

                PreferenceManager::class.java to singleVariantDiObject({
                    PreferenceManager(appContext, injectInternal(named = BASE))
                }),

                CloudRepository::class.java to singleVariantDiObject({
                    CloudRepository(
                        injectInternal(named = BASE),
                        injectInternal()
                    )
                }),

                CacheRepository::class.java to singleVariantDiObject({
                    CacheRepository(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(named = BASE),
                    )
                }),

                HttpClient::class.java to mapOf(
                    BASE to DIObject({
                        BaseHttpClient(
                            injectInternal(),
                            injectInternal(named = BASE),
                            injectInternal(named = BASE),
                        )
                    }),
                    ANALYTICS to DIObject({
                        BaseHttpClient(
                            injectInternal(),
                            injectInternal(named = ANALYTICS),
                            injectInternal(named = RECORD_ONLY),
                        )
                    }),
                ),

                Semaphore::class.java to mapOf(
                    LOCAL to DIObject({
                        Semaphore(1)
                    }),
                    REMOTE to DIObject({
                        Semaphore(1)
                    }),
                ),

                AnalyticsEventQueueDispatcher::class.java to singleVariantDiObject({
                    AnalyticsEventQueueDispatcher(
                        injectInternal(),
                        injectInternal(named = ANALYTICS),
                        injectInternal(),
                        injectInternal(named = LOCAL),
                        injectInternal(named = REMOTE),
                    )
                }),

                AnalyticsTracker::class.java to mapOf(
                    BASE to DIObject({
                        AnalyticsManager(
                            injectInternal(named = RECORD_ONLY),
                            injectInternal(),
                        )
                    }),
                    RECORD_ONLY to DIObject({
                        AnalyticsEventRecorder(
                            injectInternal(),
                            injectInternal(named = ANALYTICS),
                            injectInternal(named = LOCAL),
                        )
                    }),
                ),

                NetworkConnectionCreator::class.java to singleVariantDiObject({
                    DefaultConnectionCreator()
                }),

                HttpResponseManager::class.java to mapOf(
                    BASE to DIObject({
                        DefaultHttpResponseManager(
                            injectInternal(),
                            injectInternal(),
                            injectInternal(named = BASE),
                        )
                    }),
                    ANALYTICS to DIObject({
                        DefaultHttpResponseManager(
                            injectInternal(),
                            injectInternal(),
                            injectInternal(named = RECORD_ONLY),
                        )
                    }),
                ),

                ResponseBodyConverter::class.java to singleVariantDiObject({
                    DefaultResponseBodyConverter(injectInternal(named = BASE))
                }),

                ResponseCacheKeyProvider::class.java to singleVariantDiObject({
                    ResponseCacheKeyProvider()
                }),

                PayloadProvider::class.java to singleVariantDiObject({
                    PayloadProvider(injectInternal(), injectInternal())
                }),

                RequestFactory::class.java to singleVariantDiObject({
                    RequestFactory(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(named = BASE),
                        apiKey,
                        observerMode,
                    )
                }),

                InstallationMetaCreator::class.java to singleVariantDiObject({
                    InstallationMetaCreator(injectInternal())
                }),

                MetaInfoRetriever::class.java to singleVariantDiObject({
                    MetaInfoRetriever(
                        appContext,
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                    )
                }),

                CrossplatformMetaRetriever::class.java to singleVariantDiObject({
                    CrossplatformMetaRetriever()
                }),

                AdaptyUiMetaRetriever::class.java to singleVariantDiObject({
                    AdaptyUiMetaRetriever()
                }),

                AdIdRetriever::class.java to singleVariantDiObject({
                    AdIdRetriever(appContext, injectInternal())
                }),

                AppSetIdRetriever::class.java to singleVariantDiObject({
                    AppSetIdRetriever(appContext)
                }),

                StoreCountryRetriever::class.java to singleVariantDiObject({
                    StoreCountryRetriever(injectInternal())
                }),

                UserAgentRetriever::class.java to singleVariantDiObject({
                    UserAgentRetriever(appContext)
                }),

                IPv4Retriever::class.java to singleVariantDiObject({
                    IPv4Retriever(injectInternal())
                }),

                CustomAttributeValidator::class.java to singleVariantDiObject({
                    CustomAttributeValidator()
                }),

                PaywallPicker::class.java to singleVariantDiObject({ PaywallPicker() }),

                ProductPicker::class.java to singleVariantDiObject({ ProductPicker() }),

                AttributionHelper::class.java to singleVariantDiObject({ AttributionHelper() }),

                CurrencyHelper::class.java to singleVariantDiObject({ CurrencyHelper() }),

                HashingHelper::class.java to singleVariantDiObject({ HashingHelper() }),

                PaywallMapper::class.java to singleVariantDiObject({
                    PaywallMapper(injectInternal(named = BASE))
                }),

                ProductMapper::class.java to singleVariantDiObject({
                    ProductMapper(
                        appContext,
                        injectInternal(),
                    )
                }),

                ReplacementModeMapper::class.java to singleVariantDiObject({ ReplacementModeMapper() }),

                ProfileMapper::class.java to singleVariantDiObject({ ProfileMapper() }),

                ViewConfigurationMapper::class.java to singleVariantDiObject({
                    ViewConfigurationMapper()
                }),

                StoreManager::class.java to singleVariantDiObject({
                    StoreManager(
                        appContext,
                        injectInternal(),
                        injectInternal(named = BASE),
                    )
                }),

                LifecycleAwareRequestRunner::class.java to singleVariantDiObject({
                    LifecycleAwareRequestRunner(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(named = BASE),
                        injectInternal(),
                    )
                }),

                LifecycleManager::class.java to singleVariantDiObject({
                    LifecycleManager(injectInternal())
                }),

                ProductsInteractor::class.java to singleVariantDiObject({
                    ProductsInteractor(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal()
                    )
                }),

                ProfileInteractor::class.java to singleVariantDiObject({
                    ProfileInteractor(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                    )
                }),

                PurchasesInteractor::class.java to singleVariantDiObject({
                    PurchasesInteractor(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal()
                    )
                }),

                AuthInteractor::class.java to singleVariantDiObject({
                    AuthInteractor(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal()
                    )
                }),

                AdaptyInternal::class.java to singleVariantDiObject({
                    AdaptyInternal(
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(),
                        injectInternal(named = BASE),
                        injectInternal(),
                        injectInternal(),
                        observerMode,
                    )
                }),
            )
        )
    }
}