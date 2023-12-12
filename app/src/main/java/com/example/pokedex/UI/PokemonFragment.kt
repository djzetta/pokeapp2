package com.example.pokedex.UI

import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.example.pokedex.Adapters.Concat.PokemonConcactAdapter
import com.example.pokedex.Adapters.PokemonAdapter
import com.example.pokedex.Core.Resource
import com.example.pokedex.Data.Model.Result
import com.example.pokedex.Data.Remote.PokemonDataSource
import com.example.pokedex.Presentation.PokemonVIewModel
import com.example.pokedex.Presentation.PokemonViewModelFactory
import com.example.pokedex.R
import com.example.pokedex.Repository.PokemonRepositoryImple
import com.example.pokedex.Repository.RetrofitClient
import com.example.pokedex.Repository.webService
import com.example.pokedex.databinding.FragmentPokemonBinding

class PokemonFragment : Fragment(R.layout.fragment_pokemon), PokemonAdapter.OnPokemonClickListener {
    private lateinit var binding: FragmentPokemonBinding
    private lateinit var concactAdapter: ConcatAdapter
    private val viewmodel by viewModels<PokemonVIewModel> {
        PokemonViewModelFactory(
            PokemonRepositoryImple(
                PokemonDataSource(RetrofitClient.webservice)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPokemonBinding.bind(view)
        concactAdapter = ConcatAdapter()

        viewmodel.fetchList().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    Log.d("livedata2","${result.data}")
                    binding.progressbar.visibility = View.GONE
                    concactAdapter.apply {
                        addAdapter(0,PokemonConcactAdapter(PokemonAdapter(result.data.results,this@PokemonFragment))) }
                    binding.rvPokemon.adapter = concactAdapter
                }
                is Resource.Failed -> {
                    binding.progressbar.visibility = View.GONE
                }
            }
        })

        //var sp = SoundPool?=null
        //var soundOne=0
        //sp = SoundPool(1, AudioManager.STREAM_MUSIC,1)

        //soundOne=sp.load(this,R.raw.pokeplus, 1)!!


        //fun reproduceSoundPool(view:View){
          //  sp?.play(soundOne,1f,1f,1,1f)}
    }

    override fun onPokemonClick(pokemon: Result) {
        Log.d("Pokemon","onPokemonclick ${pokemon.name}")
        val action= PokemonFragmentDirections.actionPokemonFragmentToPokemonDetailsFragment(pokemon.name)
        findNavController().navigate(action)
    }
}
