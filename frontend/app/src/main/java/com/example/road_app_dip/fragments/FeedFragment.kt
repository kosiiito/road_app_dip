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
import com.example.road_app_dip.models.Post
import com.example.road_app_dip.network.ApiInterface
import com.example.road_app_dip.network.ApiService
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val api: ApiInterface = ApiService.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadFeed()
        return view
    }

    private fun loadFeed() {
        lifecycleScope.launch {
            try {
                val response = api.getFeed()
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    recyclerView.adapter = PostAdapter(posts)
                } else {
                    showError("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
