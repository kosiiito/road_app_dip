package com.example.road_app_dip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.road_app_dip.adapters.PostAdapter
import com.example.road_app_dip.network.ApiInterface
import com.example.road_app_dip.network.ApiService
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private val api: ApiInterface = ApiService.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        postAdapter = PostAdapter(emptyList())
        recyclerView.adapter = postAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFeed()
    }

    private fun loadFeed() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = api.getFeed()
                if (response.isSuccessful) {
                    val posts = response.body()?.posts ?: emptyList()

                    if (posts.isNotEmpty()) {
                        postAdapter = PostAdapter(posts)
                        recyclerView.adapter = postAdapter
                    } else {
                        showError("Няма налични постове.")
                    }
                } else {
                    showError("Грешка: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                showError("Грешка: ${e.message}")
            }
        }
    }




    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
