package com.example.tasktracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tasktracker.R
import com.example.tasktracker.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SignInFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var fAuth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        binding.textViewSignUp.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isClickable = false
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                loginUser(email, pass)
            } else {
                Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isClickable = true

            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        fAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                navController.navigate(R.id.action_signInFragment_to_homeFragment)
            } else {
                val errorMessage = when (it.exception) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                    is FirebaseAuthInvalidUserException -> "Invalid email or user not found"
                    else -> "Authentication failed"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isClickable = true
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        fAuth = FirebaseAuth.getInstance()
    }

}