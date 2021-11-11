package com.thana.simplegame.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.*
import com.thana.simplegame.R
import com.thana.simplegame.data.common.SharedPreferences
import com.thana.simplegame.databinding.FragmentLevelOneBinding
import com.thana.simplegame.ui.common.BaseFragment
import com.thana.simplegame.ui.common.viewBinding
import com.thana.simplegame.util.hideKeyboard


class LevelOneFragment : BaseFragment(R.layout.fragment_level_one), View.OnTouchListener,
    View.OnDragListener {

    private val binding by viewBinding(FragmentLevelOneBinding::bind)

    private lateinit var winAudio: ExoPlayer
    private lateinit var loseAudio: ExoPlayer

    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = SharedPreferences(requireContext())

        binding.next.setOnClickListener {
            nextLevel()
        }

        setListeners()
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

    private fun initAudio() {

        winAudio = ExoPlayer.Builder(requireContext()).build()
        loseAudio = ExoPlayer.Builder(requireContext()).build()

        val winUri = RawResourceDataSource.buildRawResourceUri(R.raw.win)
        val loseUri = RawResourceDataSource.buildRawResourceUri(R.raw.lose)

        winAudio.apply {
            setMediaItem(MediaItem.fromUri(winUri))
            prepare()

        }

        loseAudio.apply {
            setMediaItem(MediaItem.fromUri(loseUri))
            prepare()
        }
    }

    private fun nextLevel() {
        val action = LevelOneFragmentDirections.actionLevelOneFragmentToLevelTwoFragment()
        findNavController().navigate(action)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.bee1.setOnTouchListener(this)
        binding.bee2.setOnTouchListener(this)
        binding.bee3.setOnTouchListener(this)
        binding.bee4.setOnTouchListener(this)
        binding.bee5.setOnTouchListener(this)
        binding.bee6.setOnTouchListener(this)
        binding.bee7.setOnTouchListener(this)
        binding.area.setOnDragListener(this)
        binding.input.setOnDragListener { _, _ ->

            return@setOnDragListener true
        }
        binding.editText.setOnDragListener { _, _ ->

            return@setOnDragListener true
        }
    }

    private fun validateAnswer() {

        binding.submit.setOnClickListener {
            initAudio()
            val input = binding.editText.text.toString().trim()
            if (input == "7") {
                binding.right.visibility = View.VISIBLE
                binding.wrong.visibility = View.GONE
                binding.celebrate.visibility = View.VISIBLE
                binding.celebrate.playAnimation()
                binding.submit.isEnabled = false
                binding.next.visibility = View.VISIBLE

                if (::winAudio.isInitialized)
                    winAudio.play()

                if (sharedPreferences.getScore() < 1) {
                    sharedPreferences.addScore()
                }

            } else {
                binding.wrong.visibility = View.VISIBLE
                binding.right.visibility = View.GONE

                if (::loseAudio.isInitialized)
                    loseAudio.play()
            }
            hideKeyboard()
        }
    }


    override fun onDrag(layoutview: View, dragevent: DragEvent): Boolean {

        val view = dragevent.localState as View

        when (dragevent.action) {

            DragEvent.ACTION_DRAG_ENTERED -> {
                view.alpha = 0.3f
                view.visibility = View.INVISIBLE
            }
            DragEvent.ACTION_DROP -> {
                view.alpha = 1.0f
                val owner = binding.area
                owner.removeView(view)

                val container = layoutview as ConstraintLayout

                view.x = dragevent.x - (view.width / 2)
                view.y = dragevent.y - (view.height / 2)

                container.addView(view)
                view.visibility = View.VISIBLE
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.alpha = 1.0f
                view.visibility = View.VISIBLE

            }

        }
        return true
    }


    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        return if (motionEvent.action == MotionEvent.ACTION_DOWN) {

            view.performClick()
            val shadowBuilder = View.DragShadowBuilder(view)

            view.startDragAndDrop(null, shadowBuilder, view, 0)

            true
        } else {

            false
        }
    }
}
