package com.zmplc.plantminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.zmplc.plantminder.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: GardenViewModel
    private lateinit var gardenPlantsAdapter: GardenPlantsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[GardenViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val currentUser = auth.currentUser
        val nome = currentUser?.displayName ?: "Utente"
        binding.nome = nome

        binding.listaPianteRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.gardenPlants.observe(viewLifecycleOwner) { plants ->
            // Controllo se la lista è vuota
            // Gestisco il testo se non ci sono piante nel giardino
            if (plants.isNullOrEmpty()) {
                binding.nessunaPiantaTextView.visibility = View.VISIBLE
                binding.listaPianteRecyclerView.visibility = View.GONE
            } else {
                binding.nessunaPiantaTextView.visibility = View.GONE
                binding.listaPianteRecyclerView.visibility = View.VISIBLE

                // Inizializzo e imposto l'adapter SOLO quando ci sono piante nel giardino
                gardenPlantsAdapter = GardenPlantsAdapter(plants) { plant ->
                    val action = HomeFragmentDirections
                        .actionHomeFragmentToGardenPlantDetailFragment(plant)
                    findNavController().navigate(action)
                }
                binding.listaPianteRecyclerView.adapter = gardenPlantsAdapter
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserGardenPlants()
    }
}