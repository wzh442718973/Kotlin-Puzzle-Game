package com.thana.simplegame.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.thana.simplegame.R
import com.thana.simplegame.databinding.FragmentHomeBinding
import com.thana.simplegame.ui.common.BaseFragment
import com.thana.simplegame.ui.common.viewBinding


class HomeFragment : BaseFragment(R.layout.fragment_home), View.OnTouchListener,
    View.OnDragListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.untouchable.isEnabled = false
        binding.sunglasses.setOnTouchListener(this)
        binding.mask.setOnTouchListener(this)
        binding.area.setOnDragListener(this)
    }


    override fun onDrag(layoutview: View, dragevent: DragEvent): Boolean {

        val view = dragevent.localState as View

        when (dragevent.action) {

            DragEvent.ACTION_DRAG_ENTERED -> {
                view.alpha = 0.3f
            }
            DragEvent.ACTION_DROP -> {
                view.alpha = 1.0f

                checkIfMaskIsOnFace(dragevent)

                if (!wrongArea(dragevent)) {
                    val owner = view.parent as ViewGroup
                    owner.removeView(view)
                    val container = layoutview as ConstraintLayout
                    view.x = dragevent.x - (view.width / 2)
                    view.y = dragevent.y - (view.height / 2)
                    container.addView(view)
                }

                view.visibility = View.VISIBLE
            }

        }
        return true
    }

    private fun wrongArea(dragEvent: DragEvent): Boolean {
        val areaXStart = binding.untouchable.x
        val areaYStart = binding.untouchable.y

        val areaXEnd = areaXStart + binding.untouchable.width
        val areaYEnd = areaYStart + binding.untouchable.height

        if (dragEvent.x in areaXStart..areaXEnd && dragEvent.y in areaYStart..areaYEnd) {
            return true
        }

        return false
    }

    private fun checkIfMaskIsOnFace(dragEvent: DragEvent) {
        val faceXStart = binding.faceArea.x
        val faceYStart = binding.faceArea.y

        val faceXEnd = faceXStart + binding.faceArea.width
        val faceYEnd = faceYStart + binding.faceArea.height

        if (dragEvent.x in faceXStart..faceXEnd && dragEvent.y in faceYStart..faceYEnd) {
            Toast.makeText(requireContext(), "Near the face", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        return if (motionEvent.action == MotionEvent.ACTION_DOWN) {

            view.performClick()
            val shadowBuilder = View.DragShadowBuilder(view)

            view.startDragAndDrop(null, shadowBuilder, view, 0)

            view.visibility = View.INVISIBLE
            true
        } else {
            false
        }
    }
}
