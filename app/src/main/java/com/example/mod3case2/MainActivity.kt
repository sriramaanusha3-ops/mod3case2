package com.example.mod3case2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mod3case2.databinding.ActivityMainBinding
import com.example.mod3case2.ui.benchmark.BenchmarkFragment
import com.example.mod3case2.ui.compare.CompareFragment
import com.example.mod3case2.ui.dashboard.DashboardFragment
import com.example.mod3case2.ui.history.HistoryFragment
import com.example.mod3case2.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.nav_compare -> {
                    loadFragment(CompareFragment())
                    true
                }
                R.id.nav_benchmarks -> {
                    loadFragment(BenchmarkFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun navigateToCompare(prefilledQuestion: String? = null) {
        val compareFragment = CompareFragment.newInstance(prefilledQuestion)
        binding.bottomNavigationView.selectedItemId = R.id.nav_compare
        loadFragment(compareFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
