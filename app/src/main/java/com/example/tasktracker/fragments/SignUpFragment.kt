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
import com.example.tasktracker.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init(view)

        binding.textViewSignIn.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.btnRegister.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            binding.btnRegister.isClickable = false

            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()
            val verifyPass = binding.verifyPassEt.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (pass == verifyPass) {

                    registerUser(email, pass)

                } else {
                    Toast.makeText(context, "Password is not same", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isClickable = true
                }

            } else {
                Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isClickable = true
            }
        }

    }

    private fun registerUser(email: String, pass: String) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) navController.navigate(R.id.action_signUpFragment_to_homeFragment)
            else {
                val errorMessage = when (it.exception) {
                    is FirebaseAuthWeakPasswordException -> "Weak password. Please choose a stronger password."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email format. Please enter a valid email address."
                    is FirebaseAuthUserCollisionException -> "Email already in use. Please choose a different email."
                    else -> "Registration failed"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            binding.btnRegister.isClickable = true
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
    }

}