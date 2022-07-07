package com.example.vve_mobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.vve_mobile.databinding.FragmentCheckoutsBinding
import com.example.vve_mobile.databinding.FragmentSettingsBinding
import kotlinx.coroutines.runBlocking

class SettingsFragment : Fragment() {

    private val binding: FragmentSettingsBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = ServerDataSource(ApiHelper(RetrofitBuilder.apiService))
        binding.createBackup.setOnClickListener { _ ->
            runBlocking {
                try {
                    db.createBackup()
                    Toast.makeText(activity, "Created new backup", Toast.LENGTH_SHORT).show()
                }
                catch (exception: Exception){
                    Log.d("Error", exception.message.toString())
                    Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.restoreBackup.setOnClickListener { _ ->
            runBlocking {
                try {
                    db.restoreLastBackup()
                    Toast.makeText(activity, "Restored last backup", Toast.LENGTH_SHORT).show()
                }
                catch (exception: Exception){
                    Log.d("Error", exception.message.toString())
                    Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}