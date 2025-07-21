package com.zmplc.plantminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.zmplc.plantminder.databinding.FragmentPlantListBinding

class PlantListFragment : Fragment() {

    private lateinit var binding: FragmentPlantListBinding
    private lateinit var viewModel: PlantListViewModel
    private lateinit var plantAdapter: PlantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inizializzo il binding
        binding = FragmentPlantListBinding.inflate(inflater, container, false)

        // Inizializzo il ViewModel
        viewModel = ViewModelProvider(this)[PlantListViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Inizializzo il RecyclerView
        binding.plantRecyclerView.layoutManager = LinearLayoutManager(context)

        // Osservo i dati dal ViewModel
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            if (plants.isNotEmpty()) {
                plantAdapter = PlantAdapter(plants) { plant ->
                    val action = PlantListFragmentDirections
                        .actionPlantListFragmentToPlantDetailFragment(plant)
                    findNavController().navigate(action)
                }
                binding.plantRecyclerView.adapter = plantAdapter
            } else {
                Toast.makeText(context, "Nessuna pianta trovata", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestione filtro piante
        val chipGroup = binding.filtroPianteChipGroup
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedType = group.findViewById<Chip>(
                checkedIds.firstOrNull() ?: -1
            )?.text?.toString()?.lowercase()

            viewModel.setFilter(selectedType)
        }

        return binding.root
    }
}