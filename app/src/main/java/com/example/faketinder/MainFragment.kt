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
        val matchedFragment = MatchedFragment()

        //get userId
        val userId : String? = arguments?.getString("id").toString()



        val bundle = Bundle()
        bundle.putString("id",userId)

        //send userId to ExploreFragment
        exploreFragment.arguments = bundle

        //send userId to LikedFragment
        likedFragment.arguments = bundle

        //send userId to MatchedFragment
        matchedFragment.arguments = bundle

        //set up tabs
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(exploreFragment,"")
        adapter.addFragment(likedFragment,"")
        adapter.addFragment(matchedFragment, "")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = adapter.count - 1;

        tabLayout.getTabAt(0)!!.setIcon(R.drawable.baseline_fire)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.baseline_favorite_24)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.baseline_star_24)



        return view
    }

}