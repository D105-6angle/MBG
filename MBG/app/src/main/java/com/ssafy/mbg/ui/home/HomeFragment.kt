package com.ssafy.mbg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.mbg.R

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // fragment_home.xml 레이아웃 파일이 있어야 합니다.
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}
