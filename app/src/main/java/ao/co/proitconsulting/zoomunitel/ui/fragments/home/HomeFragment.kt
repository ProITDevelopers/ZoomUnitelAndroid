package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.Resource
import ao.co.proitconsulting.zoomunitel.localDB.RevistaDatabase
import ao.co.proitconsulting.zoomunitel.ui.MainActivity
import ao.co.proitconsulting.zoomunitel.ui.RevistaViewModelProviderFactory
import ao.co.proitconsulting.zoomunitel.ui.repository.RevistaRepository
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



    lateinit var imgBackgnd: KenBurnsView
    lateinit var mViewPager: ViewPager2
    lateinit var revistasAdapter: RevistasAdapter


    val slideHandler = Handler()





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val revistaRepository = RevistaRepository(RevistaDatabase(requireContext()))
        val viewModelProviderFactory = RevistaViewModelProviderFactory(revistaRepository)

        val viewModel =
            ViewModelProvider(this,viewModelProviderFactory)
                .get(HomeViewModel::class.java)

        val frameLayout = MainActivity.getFrameLayoutImgToolbar()
        if (frameLayout != null)
            frameLayout.visibility = View.VISIBLE




        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imgBackgnd = binding.imgBackgnd
        mViewPager = binding.viewPagerImageSlider

        val aDI = AccelerateDecelerateInterpolator()
        val generator = RandomTransitionGenerator(3000,aDI)
        imgBackgnd.setTransitionGenerator(generator)

        setViewPager()


        revistasAdapter.itemClickListener = { revistaList, position->
            val bundle = Bundle().apply {
                putSerializable("revistaList",revistaList as Serializable)
                putInt("position",position)
            }

            findNavController().navigate(
                R.id.action_nav_home_to_revistaDetailFragment,
                bundle
            )
        }


        viewModel.home.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                            revistaResponse ->

                        revistasAdapter.differ.submitList(revistaResponse)


                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                            message ->
                        MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,message)
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
        binding.shimmerFrameLayout.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.spinKitBottom.visibility = View.VISIBLE
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.shimmerFrameLayout.startShimmer()
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

//                Glide.with(requireContext())
//                    .load(Constants.IMAGE_PATH + revistasAdapter.differ.currentList[position].imgUrl)
//                    .dontAnimate()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imgBackgnd)

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