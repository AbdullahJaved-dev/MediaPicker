package com.sdsol.mediapicker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.sdsol.mediapicker.util.PaymentsUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    data class State(
        val googlePayAvailable: Boolean? = false,
        val googlePayButtonClickable: Boolean = true,
        val checkoutSuccess: Boolean = false,
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val paymentsClient: PaymentsClient = PaymentsUtil.createPaymentsClient(application)

    init {
        fetchCanUseGooglePay()
    }

    private fun fetchCanUseGooglePay() {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest()
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task = paymentsClient.isReadyToPay(request)

        task.addOnCompleteListener { completedTask ->
            try {
                _state.update { currentState ->
                    currentState.copy(googlePayAvailable = completedTask.getResult(ApiException::class.java))
                }
            } catch (exception: ApiException) {
                Log.w("isReadyToPay failed", exception)
            }
        }
    }


    fun getLoadPaymentDataTask(priceCents: Long): Task<PaymentData> {
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    fun setGooglePayButtonClickable(clickable:Boolean) {
        _state.update { currentState ->
            currentState.copy(googlePayButtonClickable = clickable)
        }
    }

    fun checkoutSuccess() {
        _state.update { currentState ->
            currentState.copy(checkoutSuccess = true)
        }
    }

}