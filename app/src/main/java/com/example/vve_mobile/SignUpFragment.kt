package com.example.vve_mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vve_mobile.databinding.FragmentSignUpBinding
import com.example.vve_mobile.models.Shop
import kotlinx.coroutines.runBlocking

class SignUpFragment : DialogFragment() {

    private val binding: FragmentSignUpBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = ServerDataSource(ApiHelper(RetrofitBuilder.apiService))

        var shops: List<Shop>
        runBlocking {
            shops = db.getShops()
        }

        binding.shopSpinner.adapter = ArrayAdapter(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item, shops.map { it.name }
        )
        binding.signUp.setOnClickListener { _ ->
            val name = binding.nameEnterField.text.toString()
            val surname = binding.surnameEnterField.text.toString()
            val email = binding.emailEnterField.text.toString()
            val shop = shops[binding.shopSpinner.selectedItemId.toInt()].id
            val password = binding.passwordEnterField.text.toString()

            Log.d(
                "",
                (name.length > 1 && surname.length > 1 && email.length > 1 && shop >= 0 && password.length > 3).toString()
            )
            if (name.length > 1 && surname.length > 1 && email.length > 1 && shop >= 0 && password.length > 3) {
                runBlocking {
                    db.registerAdministrator(name, surname, email, password, shop)
                }
                this.dismiss()
            }
        }
        binding.logIn.setOnClickListener { _ ->
            this.dismiss()
        }
    }
}