package com.zmplc.plantminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zmplc.plantminder.databinding.FragmentAnnaffiatureBinding

class AnnaffiatureFragment : Fragment() {

    private lateinit var binding: FragmentAnnaffiatureBinding
    private lateinit var viewModel: GardenViewModel
    private lateinit var adapter: AnnaffiatureAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnnaffiatureBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[GardenViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.annaffiatureRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.gardenPlants.observe(viewLifecycleOwner) { plants ->
            if (plants.isNullOrEmpty()) {
                binding.nessunaPiantaTextView.visibility = View.VISIBLE
                binding.annaffiatureRecyclerView.visibility = View.GONE
            } else {
                binding.nessunaPiantaTextView.visibility = View.GONE
                binding.annaffiatureRecyclerView.visibility = View.VISIBLE

                adapter = AnnaffiatureAdapter(plants) { plant ->
                    val action = AnnaffiatureFragmentDirections
                        .actionAnnaffiatureFragmentToGardenPlantDetailFragment(plant)
                    findNavController().navigate(action)
                }
                binding.annaffiatureRecyclerView.adapter = adapter
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserGardenPlants()
    }
}