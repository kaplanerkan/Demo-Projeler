package com.lotuspecas.bottom_sheet_android

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.lotuspecas.bottom_sheet_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        initViews()
    }

    private fun initViews() {

        binding.btnModalBottomsheet.setOnClickListener {
            MyBottomSheetDialogFragment().apply {
                show(supportFragmentManager, tag)
            }
        }

        val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.test.bottomSheet)

        standardBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.e("MainActivity", "onSlide: $slideOffset")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.e("MainActivity", "onStateChanged: $newState")
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.test.imgClose.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                } else {
                    binding.test.imgClose.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                }
            }
        })

        binding.btnPersistentBottomsheet.setOnClickListener {
            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.test.btnProcessPayment.setOnClickListener{
            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.test.imgClose.setOnClickListener{
            if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }else {
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }


    }
}

