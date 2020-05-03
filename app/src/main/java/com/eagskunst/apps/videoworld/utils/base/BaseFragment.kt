package com.eagskunst.apps.videoworld.utils.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Created by eagskunst in 2/5/2020.
 */
abstract class BaseFragment<B: ViewBinding>(@LayoutRes layout: Int): Fragment(layout) {

    abstract val bindingFunction:(view: View) -> B
    private var _binding: B? = null
    protected val binding: B
        get() = _binding ?: throw IllegalAccessException("Should only be accessed after onCreateView and before onDestroyView")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        if(_binding == null){
            val view = super.onCreateView(inflater, container, savedInstanceState)!!
            _binding = bindingFunction(view)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}