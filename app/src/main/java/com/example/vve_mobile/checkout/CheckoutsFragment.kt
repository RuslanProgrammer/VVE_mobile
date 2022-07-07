package com.example.vve_mobile.checkout

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.example.vve_mobile.*
import com.example.vve_mobile.databinding.FragmentCheckoutsBinding
import com.example.vve_mobile.epoxy.simpleController
import com.example.vve_mobile.epoxy.viewholders.checkout.AddCheckoutCardViewHolder
import com.example.vve_mobile.epoxy.viewholders.checkout.CheckoutCardViewHolder
import com.example.vve_mobile.epoxy.viewholders.checkout.addCheckoutCardViewHolder
import com.example.vve_mobile.epoxy.viewholders.checkout.checkoutCardViewHolder
import com.example.vve_mobile.models.Administrator
import com.example.vve_mobile.models.Checkout
import com.example.vve_mobile.models.Worker
import kotlinx.coroutines.runBlocking

class CheckoutsFragment : MvRxBaseFragment(R.layout.fragment_checkouts) {

    private val viewModel: CheckoutViewModel by fragmentViewModel()
    private val binding: FragmentCheckoutsBinding by viewBinding()

    override fun epoxyController() = simpleController(viewModel) { state ->
        val user = state.user.invoke() ?: return@simpleController
        val checkoutList = state.checkoutList
        runBlocking {
            val db = ServerDataSource(ApiHelper(RetrofitBuilder.apiService))
            binding.baseInfo.text = db.getInfo((user as Administrator).shop)
            binding.rebalance.setOnClickListener {
                runBlocking {
                    db.rebalance(user.shop)
                    binding.baseInfo.text = db.getInfo(user.shop)
                    if(binding.baseInfo.text.length < 3){
                        binding.rebalance.isEnabled = false
                    }
                }
            }
            if(binding.baseInfo.text.length < 3){
                binding.rebalance.isEnabled = false
            }
        }
        if (checkoutList is Success) {
            val checkouts = checkoutList.invoke()
            when (user) {
                is Administrator -> {
                    renderAdministratorCheckouts(checkouts) { state.freeWorkerList()!! }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onAsync(CheckoutState::user, onSuccess = { user ->
            when (user) {
                is Administrator -> {

                }
            }
        })
    }

    private fun EpoxyController.renderAdministratorCheckouts(
        checkouts: List<Checkout>,
        getFreeWorkers: () -> Map<String, List<Worker>>
    ) {
        addCheckoutCardViewHolder(getFreeWorkers) {
            id(-1)
            title(getString(R.string.add_new_checkout))
            onClickListeners(buttonsAdd)
        }
        checkouts.forEach { checkout ->
            checkoutCardViewHolder(getFreeWorkers) {
                id(checkout.id)
                checkout(checkout)
                onClickListeners(buttons)
            }
        }
    }

    private val buttons: CheckoutCardViewHolder.cardOnClick =
        object : CheckoutCardViewHolder.cardOnClick {
            override fun checkButtonOnClick(
                checkout: Checkout,
                getFreeWorkers: () -> Map<String, List<Worker>>
            ) {
                val dialog = EditCheckoutDialogFragment(edit, getFreeWorkers)
                val bundle = Bundle()
                if (checkout.id != -1) {
                    bundle.putInt("id", checkout.id)
                    bundle.putString("title", checkout.title)
                    bundle.putString("description", checkout.description)
                    bundle.putInt("worker", checkout.worker)
                    dialog.arguments = bundle
                }

                dialog.show(childFragmentManager, "DialogFragmentWithSetter")
            }
        }

    private val buttonsAdd: AddCheckoutCardViewHolder.cardOnClick =
        object : AddCheckoutCardViewHolder.cardOnClick {
            override fun addButtonOnClick(getFreeWorkers: () -> Map<String, List<Worker>>) {
                val dialog = EditCheckoutDialogFragment(edit, getFreeWorkers)
                dialog.show(childFragmentManager, "DialogFragmentWithSetter")
            }
        }

    interface editCheckout {
        fun editCheckout(id: Int, title: String, description: String, worker: Int)
        fun deleteCheckout(id: Int)
        fun createCheckout(
            title: String,
            description: String,
            worker: Int
        )
    }

    private val edit =
        object : editCheckout {
            override fun editCheckout(
                id: Int,
                title: String,
                description: String,
                worker: Int
            ) {
                viewModel.editCheckout(id, title, description, worker)
            }

            override fun deleteCheckout(id: Int) {
                viewModel.deleteCheckout(id)
            }

            override fun createCheckout(
                title: String,
                description: String,
                worker: Int
            ) {
                viewModel.createCheckout(title, description, worker)
            }
        }
}


