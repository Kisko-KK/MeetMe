package com.example.faketinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        val exploreFragment = ExploreFragment()
        val likedFragment = LikedFragment()

        //get userId
        val userId : String? = arguments?.getString("id").toString()

        //send userId to ExploreFragment
        val bundle = Bundle()
        bundle.putString("id",userId)
        exploreFragment.arguments = bundle

        //send userId to LikedFragment
        val bundle1 = Bundle()
        bundle1.putString("id",userId)
        likedFragment.arguments = bundle1



        //set up tabs
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(exploreFragment,"")
        adapter.addFragment(likedFragment,"")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)!!.setIcon(R.drawable.baseline_fire)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.baseline_star_24)



        return view
    }

}