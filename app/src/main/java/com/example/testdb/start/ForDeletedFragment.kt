package com.example.testdb.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.testdb.databinding.FragmentForDeletedBinding
import com.example.testdb.utils.REF_DATABASE
import com.example.testdb.utils.USER
import com.google.firebase.auth.FirebaseAuth

class ForDeletedFragment : Fragment() {
    private lateinit var binding: FragmentForDeletedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentForDeletedBinding.inflate(layoutInflater, container, false)
        initListeners()
        return binding.root
    }

    private fun initListeners() {
        binding.button.setOnClickListener {
            FirebaseAuth.getInstance().apply {
                currentUser?.delete()
                signOut()
            }
            REF_DATABASE.child(USER.id).removeValue()
            findNavController().popBackStack()
        }
    }
}