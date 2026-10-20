package com.example.ep2

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val pokedexView : ImageView = findViewById(R.id.imgPokedex)

        iniciarAnimacionFlotanteYRotacion(pokedexView)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun iniciarAnimacionFlotanteYRotacion(viewToAnimate : ImageView) {

        val distance = 8000
        val scale = viewToAnimate.resources.displayMetrics.density
        viewToAnimate.cameraDistance = distance * scale

        val translateY = ObjectAnimator.ofFloat(
            viewToAnimate,
            "translationY",
            0f, -40f
        ).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }

        val rotate = ObjectAnimator.ofFloat(
            viewToAnimate,
            "rotationY",
            0f, 360f
        ).apply {
            duration = 5500
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        AnimatorSet().apply {
            playTogether(translateY, rotate)
            start()
        }
    }
}