package com.example.vve_mobile.epoxy.viewholders.checkout

import android.graphics.Color
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.example.vve_mobile.R
import com.example.vve_mobile.ViewBindingEpoxyModelWithHolder
import com.example.vve_mobile.databinding.CheckoutItemBinding
import com.example.vve_mobile.models.Checkout
import com.example.vve_mobile.models.Worker

@EpoxyModelClass(layout = R.layout.checkout_item)
abstract class CheckoutCardViewHolder(val getFreeWorkers: () -> Map<String, List<Worker>>) :
    ViewBindingEpoxyModelWithHolder<CheckoutItemBinding>() {

    @EpoxyAttribute
    lateinit var checkout: Checkout

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    open lateinit var onClickListeners: cardOnClick

    override fun CheckoutItemBinding.bind() {
        checkoutTitleText.text = checkout.title
        checkoutCard.setOnClickListener {
            onClickListeners.checkButtonOnClick(checkout, getFreeWorkers)
        }
    }

    interface cardOnClick {
        fun checkButtonOnClick(checkout: Checkout, getFreeWorkers: () -> Map<String, List<Worker>>)
    }
}


@EpoxyModelClass(layout = R.layout.checkout_item)
abstract class AddCheckoutCardViewHolder(val getFreeWorkers: () -> Map<String, List<Worker>>) :
    ViewBindingEpoxyModelWithHolder<CheckoutItemBinding>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    open lateinit var onClickListeners: cardOnClick

    override fun CheckoutItemBinding.bind() {
        checkoutCard.setCardBackgroundColor(Color.CYAN)
        checkoutTitleText.text = title
        checkoutCard.setOnClickListener {
            onClickListeners.addButtonOnClick(getFreeWorkers)
        }
    }

    interface cardOnClick {
        fun addButtonOnClick(getFreeWorkers: () -> Map<String, List<Worker>>)
    }
}