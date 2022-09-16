package ao.co.proitconsulting.zoomunitel.ui.fragments.revistadetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.adapters.RevistasLerDownloadAdapter
import ao.co.proitconsulting.zoomunitel.databinding.FragmentRevistaDetailLerDownloadBinding
import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import kotlin.math.abs

class RevistaLerDownloadFragment : Fragment() {

    private var _binding: FragmentRevistaDetailLerDownloadBinding? = null
    private val binding get() = _binding!!

    lateinit var mViewPager: ViewPager2
    lateinit var revistaList: List<RevistaModel>
    var currentPosition: Int = 0
    lateinit var revistasDetailAdapter: RevistasLerDownloadAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            revistaList = (bundle.getSerializable("revistaList") as List<RevistaModel>)
            currentPosition = bundle.getInt("position")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRevistaDetailLerDownloadBinding.inflate(inflater, container, false)
        val root: View = binding.root


        setViewPager()



        return root
    }




    private fun setViewPager(){
        mViewPager = binding.viewPagerDetail
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = (0.85f + r * 0.25f)
        }


        revistasDetailAdapter = RevistasLerDownloadAdapter()
        revistasDetailAdapter.differ.submitList(revistaList)
        mViewPager.apply {
            adapter = revistasDetailAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(compositePageTransformer)
        }
        mViewPager.setCurrentItem(currentPosition,false)

        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                currentPosition = position

            }
        })

        revistasDetailAdapter.itemClickListener = {revista ->

            context?.let {
                Toast.makeText(it, "Ler revista "+revista.title, Toast.LENGTH_SHORT).show()
            }

        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}