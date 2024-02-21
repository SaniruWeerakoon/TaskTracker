package com.example.tasktracker.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tasktracker.R
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        val isLogin: Boolean = mAuth.currentUser != null


        val imgLogo = view.findViewById<View>(R.id.imgLogo)
        val rotationAnimator = ObjectAnimator.ofFloat(imgLogo, "rotation", 90f, 360f)
        rotationAnimator.duration = 1500

        rotationAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                if (isLogin) {
                    navController.navigate(R.id.action_splashFragment_to_homeFragment)
                } else {
                    navController.navigate(R.id.action_splashFragment_to_signInFragment)
                }
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        rotationAnimator.start()
    }

    private fun init(view: View) {
        mAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
    }
}