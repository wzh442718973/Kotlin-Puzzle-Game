package com.thana.simplegame.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.thana.simplegame.R
import com.thana.simplegame.data.common.SharedPreferences
import com.thana.simplegame.databinding.FragmentLevelTwoBinding
import com.thana.simplegame.ui.common.BaseFragment
import com.thana.simplegame.ui.common.viewBinding

class LevelTwoFragment : BaseFragment(R.layout.fragment_level_two) {

    private val binding by viewBinding(FragmentLevelTwoBinding::bind)

    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPreferences(requireContext())

        binding.next.setOnClickListener {
            nextLevel()
        }

        showHint()
        validateAnswer()

    }

    private fun showHint() {
        binding.expand.setOnClickListener {
            binding.hint.visibility = View.VISIBLE
            binding.collapse.visibility = View.VISIBLE
            binding.expand.visibility = View.INVISIBLE
        }
        binding.collapse.setOnClickListener {
            binding.hint.visibility = View.GONE
            binding.collapse.visibility = View.INVISIBLE
            binding.expand.visibility = View.VISIBLE
        }
    }

    private fun nextLevel() {
        val action = LevelTwoFragmentDirections.actionLevelTwoFragmentToLevelThreeFragment()
        findNavController().navigate(action)
    }

    private fun validateAnswer() {

        val loseAudio = ExoPlayer.Builder(requireContext()).build()

        val loseUri = RawResourceDataSource.buildRawResourceUri(R.raw.lose)

        val winAudio = ExoPlayer.Builder(requireContext()).build()

        val winUri = RawResourceDataSource.buildRawResourceUri(R.raw.win)


        loseAudio.apply {
            setMediaItem(MediaItem.fromUri(loseUri))
            prepare()
        }

        winAudio.apply {
            setMediaItem(MediaItem.fromUri(winUri))
            prepare()
        }

        binding.slider.addOnChangeListener { slider, value, _ ->
            if (value < 10.0) {

                binding.right.visibility = View.VISIBLE
                binding.wrong.visibility = View.INVISIBLE
                binding.next.visibility = View.VISIBLE
                binding.celebrate.visibility = View.VISIBLE
                binding.celebrate.playAnimation()

                slider.value = 0.0f
                slider.isEnabled = false
                winAudio.play()

                if (sharedPreferences.getScore() < 2) {
                    sharedPreferences.addScore()
                }

            } else if (value > 50.0) {

                binding.right.visibility = View.GONE
                binding.wrong.visibility = View.VISIBLE
                loseAudio.play()
            }

        }
    }
}

