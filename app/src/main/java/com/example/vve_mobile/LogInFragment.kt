package com.example.vve_mobile

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vve_mobile.checkout.EditCheckoutDialogFragment
import com.example.vve_mobile.databinding.CardChangeCheckoutBinding
import com.example.vve_mobile.databinding.FragmentLogInBinding
import kotlinx.coroutines.runBlocking

class LogInFragment : Fragment() {

    private val binding: FragmentLogInBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = ServerDataSource(ApiHelper(RetrofitBuilder.apiService))
        binding.logIn.setOnClickListener { _ ->
            val email = binding.emailEnterField.text.toString()
            val password = binding.passwordEnterField.text.toString()
            runBlocking {
                try {
                    val user = db.loginUser(email, password)
                    Toast.makeText(activity, "Logged in", Toast.LENGTH_SHORT).show()
                }
                catch (exception: Exception){
                    Log.d("Correct", exception.message.toString())
                    Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.signUp.setOnClickListener { _ ->
            val dialog = SignUpFragment()
            dialog.show(childFragmentManager, "SignUpFragmentWithSetter")
        }
    }
}