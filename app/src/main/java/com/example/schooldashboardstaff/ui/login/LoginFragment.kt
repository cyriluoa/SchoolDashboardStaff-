package com.example.schooldashboardstaff.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels

import android.view.ViewGroup
import android.widget.Toast
import com.example.schooldashboardstaff.databinding.FragmentLoginBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SchoolAdminDashboardActivity
import com.example.schooldashboardstaff.ui.dashboard.superAdmin.SuperAdminDashboardActivity
import com.example.schooldashboardstaff.ui.dashboard.teacher.TeacherDashboardActivity
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.login(username, password)
            }
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { user ->
                val intent = when (user.role.name) {
                    "SUPER_ADMIN" -> Intent(requireContext(), SuperAdminDashboardActivity::class.java)
                    "SCHOOL_ADMIN" -> Intent(requireContext(), SchoolAdminDashboardActivity::class.java)
                    "TEACHER" -> Intent(requireContext(), TeacherDashboardActivity::class.java)
                    else -> null
                }

                intent?.putExtra(Constants.USER_OBJECT_INTENT_KEY, user)
                if (intent != null) {
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Invalid user role", Toast.LENGTH_SHORT).show()
                }
            }

            result.onFailure { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
