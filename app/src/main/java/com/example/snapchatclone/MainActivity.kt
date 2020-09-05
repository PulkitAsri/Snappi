package com.example.snapchatclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager:ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UserInformation.startFetching()

        viewPager=findViewById(R.id.viewPager)
        val adapter= MyPagerAdapter(supportFragmentManager)
        viewPager.adapter=adapter
        viewPager.currentItem=1
    }

    class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when(position){
                0->
                    ChatFragment().newInstance()
                1->
                    CameraFragment().newInstance()
                2->
                    StoryFragment().newInstance()
                else->
                    CameraFragment().newInstance()
            }
        }

        override fun getCount(): Int {
            return 3
        }

    }
}