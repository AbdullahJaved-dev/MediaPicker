package com.sdsol.mediapicker.util


import com.google.android.gms.wallet.WalletConstants

/**
 * This file contains several constants you must edit before proceeding.
 * Please take a look at PaymentsUtil.java to see where the constants are used and to potentially
 * remove ones not relevant to your integration.
 *
 *
 * Required changes:
 *
 *  1.  Update SUPPORTED_NETWORKS and SUPPORTED_METHODS if required (consult your processor if
 * unsure)
 *  1.  Update CURRENCY_CODE to the currency you use.
 *  1.  Update SHIPPING_SUPPORTED_COUNTRIES to list the countries where you currently ship. If this
 * is not applicable to your app, remove the relevant bits from PaymentsUtil.java.
 *  1.  If you're integrating with your `PAYMENT_GATEWAY`, update
 * PAYMENT_GATEWAY_TOKENIZATION_NAME and PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS per the
 * instructions they provided. You don't need to update DIRECT_TOKENIZATION_PUBLIC_KEY.
 *  1.  If you're using `DIRECT` integration, please edit protocol version and public key as
 * per the instructions.
 */
object Constants {
    const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

    val SUPPORTED_NETWORKS = listOf(
        "AMEX",
        "DISCOVER",
        "JCB",
        "MASTERCARD",
        "VISA")

    val SUPPORTED_METHODS = listOf(
        "PAN_ONLY",
        "CRYPTOGRAM_3DS")

    const val COUNTRY_CODE = "US"

    const val CURRENCY_CODE = "USD"

    private const val PAYMENT_GATEWAY_TOKENIZATION_NAME = "example"

    /**
     * Custom parameters required by the processor/gateway.
     * In many cases, your processor / gateway will only require a gatewayMerchantId.
     * Please refer to your processor's documentation for more information. The number of parameters
     * required and their names vary depending on the processor.
     *
     * @value #PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS
     */
    val PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = mapOf(
        "gateway" to PAYMENT_GATEWAY_TOKENIZATION_NAME,
        "gatewayMerchantId" to "exampleGatewayMerchantId"
    )
}