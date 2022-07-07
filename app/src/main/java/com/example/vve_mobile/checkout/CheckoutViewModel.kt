package com.example.vve_mobile.checkout

import com.airbnb.mvrx.*
import com.example.vve_mobile.ApiHelper
import com.example.vve_mobile.RetrofitBuilder
import com.example.vve_mobile.ServerDataSource
import com.example.vve_mobile.models.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


data class CheckoutState(
    val user: Async<User> = Uninitialized,
    val checkoutList: Async<List<Checkout>> = Uninitialized,
    val freeWorkerList: Async<Map<String, List<Worker>>> = Uninitialized,
) : MavericksState

class CheckoutViewModel(
    initialState: CheckoutState,
    private val serverDataSource: ServerDataSource
) : MavericksViewModel<CheckoutState>(initialState) {

    init {
        setState {
            copy(user = Loading(), checkoutList = Loading(), freeWorkerList = Loading())
        }
        runBlocking {
            val user = serverDataSource.getUser()
            val checkoutList: List<Checkout> = serverDataSource.getCheckoutListByShop((user as Administrator).shop)
            val freeWorkerList = mapOf(
                "free" to serverDataSource.getAllAvailableWorkersByShopId(1),
                "all" to serverDataSource.getWorkers()
            )
            setState {
                copy(
                    user = Success(user),
                    checkoutList = Success(checkoutList),
                    freeWorkerList = Success(freeWorkerList)
                )
            }
        }
//        viewModelScope.launch {
//            val user = serverDataSource.getUser()
//            val checkoutList: List<Checkout> = serverDataSource.getCheckoutListByShop((user as Administrator).shop)
////            when (user) {
////                is Administrator -> {
////                    checkoutList = serverDataSource.getCheckoutListByShop(user.shop)
////                }
////                is Worker -> {
////                    checkoutList = serverDataSource.getCheckoutListByShop(user.shop)
////                }
////                is Customer -> {
////                    checkoutList = serverDataSource.getCheckoutList()
////                }
////
////                else -> {
////                    setState { copy(user = Fail(NullPointerException())) }
////                    return@launch
////                }
////            }
//
////            when (user) {
////                is Administrator -> {
//                    val freeWorkerList = mapOf(
//                        "free" to serverDataSource.getAllAvailableWorkersByShopId(user.shop),
//                        "all" to serverDataSource.getWorkers()
//                    )
//                    serverDataSource.getAllAvailableWorkersByShopId(user.shop)
//                    setState {
//                        copy(
//                            user = Success(user),
//                            checkoutList = Success(checkoutList),
//                            freeWorkerList = Success(freeWorkerList)
//                        )
//                    }
//                }
////                is Customer -> {
////
////                }
    }

    companion object : MavericksViewModelFactory<CheckoutViewModel, CheckoutState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: CheckoutState
        ): CheckoutViewModel {
            return CheckoutViewModel(state, ServerDataSource(ApiHelper(RetrofitBuilder.apiService)))
        }
    }

    fun editCheckout(
        id: Int,
        title: String,
        description: String,
        worker: Int
    ) {
        withState { state ->
            viewModelScope.launch {
                serverDataSource.updateCheckout(
                    id,
                    title,
                    description,
                    (state.user.invoke()!! as Administrator).shop,
                    worker
                )
                val newCheckoutList =
                    serverDataSource.getCheckoutListByShop((state.user.invoke() as Administrator).shop)

                setState { copy(checkoutList = Success(newCheckoutList)) }
            }
        }
    }

    fun deleteCheckout(id: Int) {
        withState { state ->
            viewModelScope.launch {
                val newCheckoutList = state.checkoutList.invoke()!!.filter {
                    it.id != id
                }
                serverDataSource.deleteCheckout(id)

                setState { copy(checkoutList = Success(newCheckoutList)) }
            }
        }
    }

    fun createCheckout(
        title: String,
        description: String,
        worker: Int
    ) {

        withState { state ->
            viewModelScope.launch {
                serverDataSource.createCheckout(
                    title,
                    description,
                    (state.user.invoke()!! as Administrator).shop,
                    worker
                )
                val newCheckoutList =
                    serverDataSource.getCheckoutListByShop((state.user.invoke() as Administrator).shop)

                setState { copy(checkoutList = Success(newCheckoutList)) }
            }
        }
    }
}