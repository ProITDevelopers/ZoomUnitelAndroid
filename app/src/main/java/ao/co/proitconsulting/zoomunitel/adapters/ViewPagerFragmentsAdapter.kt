package ao.co.proitconsulting.zoomunitel.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerFragmentsAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {


    val mFragmentList: MutableList<Fragment> = ArrayList()
    val mFragmentTitleList: MutableList<String> = ArrayList()



    fun addFrag(fragment: Fragment, title: String){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }
}