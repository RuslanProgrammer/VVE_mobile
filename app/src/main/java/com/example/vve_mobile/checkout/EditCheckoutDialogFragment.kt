package com.example.vve_mobile.checkout

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vve_mobile.ApiHelper
import com.example.vve_mobile.R
import com.example.vve_mobile.RetrofitBuilder
import com.example.vve_mobile.ServerDataSource
import com.example.vve_mobile.databinding.CardChangeCheckoutBinding
import com.example.vve_mobile.models.Worker
import kotlinx.coroutines.runBlocking

class EditCheckoutDialogFragment(
    val edit: CheckoutsFragment.editCheckout,
    val getFreeWorkers: () -> Map<String, List<Worker>>
) : DialogFragment() {
    private val binding: CardChangeCheckoutBinding by viewBinding()
    var id: Int? = null
    var title: String? = null
    var description: String? = null
    var worker: Int? = null
    var freeWorkersId: List<Int>? = null
    var freeWorkersName: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.card_change_checkout, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val freeWorkers = getFreeWorkers()

        freeWorkersId = freeWorkers["free"]!!.map { worker ->
            worker.id
        }
        freeWorkersName = freeWorkers["free"]!!.map { worker ->
            worker.name + " " + worker.surname
        }

        val mArgs = arguments
        if (mArgs != null) {
            id = mArgs.getInt("id")
            title = mArgs.getString("title").toString()
            description = mArgs.getString("description").toString()
            worker = mArgs.getInt("worker")
            val curWorker = freeWorkers["all"]!!.first { it.id == worker }
            val workerName = curWorker.name + " " + curWorker.surname
            freeWorkersId = listOf(worker!!) + freeWorkersId!!
            freeWorkersName = listOf(workerName) + freeWorkersName!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleEditText.text = SpannableStringBuilder(title ?: "")
        binding.descriptionEditText.text = SpannableStringBuilder(description ?: "")
        binding.workerSpinner.adapter = ArrayAdapter(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item, freeWorkersName ?: listOf()
        )
        if (id == null) {
            binding.deleteButton.isEnabled = false

        } else {
            runBlocking {
                val db = ServerDataSource(ApiHelper(RetrofitBuilder.apiService))
                binding.peopleText.text = db.getPeopleByCheckout(id!!).toString()
            }
        }

        binding.OKButton.setOnClickListener { _ ->
            title = binding.titleEditText.text.toString()
            description = binding.descriptionEditText.text.toString()
            if (binding.workerSpinner.selectedItemId >= 0) {
                worker = freeWorkersId?.get(binding.workerSpinner.selectedItemId.toInt())
                if (title != null && description != null && worker != null) {
                    if (title!!.length > 2 && description!!.length > 2 && worker!! >= 0) {
                        if (id == null) {
                            edit.createCheckout(title!!, description!!, worker!!)
                            this.dismiss()
                        } else {
                            edit.editCheckout(id!!, title!!, description!!, worker!!)
                            this.dismiss()
                        }
                    }
                }
            }
        }
        binding.cancelButton.setOnClickListener { _ ->
            this.dismiss()
        }

        binding.deleteButton.setOnClickListener { _ ->
            edit.deleteCheckout(id!!)
            this.dismiss()
        }
    }
}