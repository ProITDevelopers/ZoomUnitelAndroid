package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.adapters.RevistasAdapter
import ao.co.proitconsulting.zoomunitel.databinding.FragmentHomeBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.Resource
import ao.co.proitconsulting.zoomunitel.ui.MainActivity
import com.bumptech.glide.Glide
import com.flaviofaria.kenburnsview.KenBurnsView
import kotlin.math.abs

class HomeFragment : Fragment() {

    val TAG = "TAG_HomeFrag"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    //lateinit var viewModel: HomeViewModel
    lateinit var imgBackgnd: KenBurnsView
    lateinit var mViewPager: ViewPager2
    lateinit var revistasAdapter: RevistasAdapter


    val slideHandler = Handler()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel = MainActivity.getViewModel()


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imgBackgnd = binding.imgBackgnd
        mViewPager = binding.viewPagerImageSlider
        setViewPager()


        viewModel?.home?.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                            revistaResponse ->
                        
                        for (revista in revistaResponse){
                            Log.d(TAG, "onCreateView: revistaList: {${revista.title}}")
                        }
                        revistasAdapter.differ.submitList(revistaResponse)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                            message ->
                        Log.e(TAG, "onViewCreated: erro: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()


                }

            }

        })

        return root
    }


    private fun hideProgressBar() {
        binding.spinKitBottom.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.spinKitBottom.visibility = View.VISIBLE
    }

    private fun setViewPager(){

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = (0.60f + r * 0.25f)
        }


        revistasAdapter = RevistasAdapter()
        mViewPager.apply {
            adapter = revistasAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(compositePageTransformer)
        }

        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                Glide.with(requireContext())
                    .load(Constants.IMAGE_PATH + revistasAdapter.differ.currentList[position].imgUrl)
                    .into(imgBackgnd)

                slideHandler.removeCallbacks(sliderRunnable)
                slideHandler.postDelayed(
                    sliderRunnable,
                    Constants.REQUEST_TIMEOUT
                ) // Slide duration 5 seconds

                if (position + 1 == revistasAdapter.differ.currentList.size) {
                    slideHandler.postDelayed(
                        runnable,
                        Constants.REQUEST_TIMEOUT
                    ) // Slide duration 5 seconds
                }


            }
        })

    }

    private val sliderRunnable = Runnable {
        mViewPager.currentItem = mViewPager.currentItem + 1
    }

    private val runnable = Runnable {
        mViewPager.currentItem = 0
    }

    override fun onResume() {
        slideHandler.postDelayed(
            sliderRunnable,
            Constants.REQUEST_TIMEOUT
        ) // Slide duration 5 seconds
        super.onResume()
    }
    override fun onPause() {
        slideHandler.removeCallbacks(sliderRunnable)
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}