package fr.ffnet.downloader.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import dagger.android.support.DaggerFragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.category.CategoryActivity
import kotlinx.android.synthetic.main.fragment_categories.*
import javax.inject.Inject

class CategoriesFragment : DaggerFragment(), CategoryListener {

    @Inject lateinit var viewModel: CategoriesViewModel
    private val adapter: CategoryAdapter by lazy { CategoryAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_categories, container, false).also {
        requireActivity().title = resources.getString(R.string.categories_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.refreshCategories()
        categoriesRecyclerView.adapter = adapter
        initObservers()
    }

    override fun onCategorySelected(category: CategoryDisplayModel) {
        startActivity(CategoryActivity.newIntent(requireContext(), category.url))
    }

    private fun initObservers() {
        viewModel.getCategoryList().observe(viewLifecycleOwner, Observer {
            adapter.categoryList = it
        })
    }
}
