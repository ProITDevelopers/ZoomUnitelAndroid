package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.adapters.RevistasAdapter
import ao.co.proitconsulting.zoomunitel.databinding.FragmentHomeBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.Resource
import ao.co.proitconsulting.zoomunitel.localDB.RevistaDatabase
import ao.co.proitconsulting.zoomunitel.ui.activities.MainActivity
import ao.co.proitconsulting.zoomunitel.ui.repository.RevistaRepository
import ao.co.proitconsulting.zoomunitel.ui.repository.RevistaViewModelProviderFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import java.io.Serializable
import kotlin.math.abs

class HomeFragment : Fragment() {

    val TAG = "TAG_HomeFrag"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!



    private lateinit var imgBackgnd: KenBurnsView
    private lateinit var mViewPager: ViewPager2
    private lateinit var revistasAdapter: RevistasAdapter


    private val slideHandler = Handler(Looper.getMainLooper())

    private lateinit var coordinatorLayout: ConstraintLayout
    private lateinit var errorLayout: RelativeLayout
    private lateinit var imgErro: ImageView
    private lateinit var txtErro: TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val revistaRepository = RevistaRepository(RevistaDatabase(requireContext()))

        val viewModelProviderFactory = RevistaViewModelProviderFactory(revistaRepository)

        val viewModel =
            ViewModelProvider(this,viewModelProviderFactory)[HomeViewModel::class.java]

        val frameLayout = MainActivity.getFrameLayoutImgToolbar()
        if (frameLayout != null)
            frameLayout.visibility = View.VISIBLE



        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        coordinatorLayout = root.findViewById(R.id.coordinatorLayout)
        errorLayout = root.findViewById(R.id.erroLayout)
        imgErro = root.findViewById(R.id.imgErro)
        txtErro = root.findViewById(R.id.txtErro)

        imgBackgnd = binding.imgBackgnd
        mViewPager = binding.viewPagerImageSlider

        val aDI = AccelerateDecelerateInterpolator()
        val generator = RandomTransitionGenerator(3000,aDI)
        imgBackgnd.setTransitionGenerator(generator)

        setViewPager()


        revistasAdapter.itemClickListener = {v, revistaList, position->
            v.setOnClickListener(null)
            val bundle = Bundle().apply {
                putSerializable("revistaList",revistaList as Serializable)
                putInt("position",position)
            }

            findNavController().navigate(
                R.id.action_nav_home_to_revistaDetailFragment,
                bundle
            )

        }



        viewModel.home.observe(viewLifecycleOwner){ result ->

            revistasAdapter.differ.submitList(result.data)
            hideProgressBar()


            if (result is Resource.Loading && result.data.isNullOrEmpty()){
                showProgressBar()
            }

            else if (result is Resource.Error && result.data.isNullOrEmpty()){
                hideProgressBar()
                val message = result.message?.localizedMessage
                if (message != null) {
                    Log.d(TAG, "onCreateView: $message")
                    showErrorScreen(message)
                }
            }
        }


        return root
    }


    private fun hideProgressBar() {
        binding.shimmerFrameLayout.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.shimmerFrameLayout.startShimmer()
    }

    private fun showErrorScreen(msg:String){


        if (!Constants.isNetworkAvailable){
            imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24)
            txtErro.text = getString(R.string.msg_erro_internet)

        }else if (msg.contains("timeout")){
            imgErro.setImageResource(R.drawable.ic_baseline_error_outline_24)
            txtErro.text = getString(R.string.msg_erro_internet_timeout)

        }else{
            imgErro.setImageResource(R.drawable.ic_baseline_error_outline_24)
            txtErro.text = getString(R.string.msg_erro_servidor)
        }

        if (errorLayout.visibility == View.GONE){
            errorLayout.visibility = View.VISIBLE
            coordinatorLayout.visibility = View.GONE
        }
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



                context?.let{
                    Glide
                        .with(it)
                        .asBitmap()
                        .load(Constants.IMAGE_PATH + revistasAdapter.differ.currentList[position].imgUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(object : SimpleTarget<Bitmap>() {



                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                imgBackgnd.setImageBitmap(resource)
                            }




                        })


                }


                slideHandler.removeCallbacks(sliderRunnable)
                slideHandler.postDelayed(
                    sliderRunnable,
                    Constants.SLIDE_TIME_DELAY
                ) // Slide duration 5 seconds

                if (position + 1 == revistasAdapter.differ.currentList.size) {
                    slideHandler.postDelayed(
                        runnable,
                        Constants.SLIDE_TIME_DELAY
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
            Constants.SLIDE_TIME_DELAY
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