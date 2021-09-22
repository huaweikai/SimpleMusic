package com.byexoplayer.music.ui.activities


import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.byexoplayer.music.R
import com.byexoplayer.music.databinding.ActivityMainBinding
import com.byexoplayer.music.others.Utils.getPermission
import com.byexoplayer.music.others.Utils.hasPermission
import com.byexoplayer.music.base.BaseActivity
import com.byexoplayer.music.ui.viewmodels.MainViewModel
import com.gyf.immersionbar.ImmersionBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var bind:ActivityMainBinding
    private var ishasPermission=false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val viewModel:MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBar()
        bind= ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        viewModel.init(this)
        setSupportActionBar(bind.mainToolBar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hostFragment) as NavHostFragment
        navController=navHostFragment.navController
        initDrawer()
        initPermission()

    }

    private fun initBar() {
        if(this.applicationContext.resources.configuration.uiMode==0x21){
            ImmersionBar.with(this)
                .transparentStatusBar()
                .transparentBar()
                .statusBarDarkFont(false)
                .autoDarkModeEnable(true)
                .init()
        }
        else{
            ImmersionBar.with(this)
                .transparentStatusBar()
                .transparentBar()
                .statusBarDarkFont(true)
                .autoDarkModeEnable(true)
                .init()
        }
    }

    private fun initDrawer() {
        val set= setOf(R.id.musicFragment, R.id.settingsFragment)
        appBarConfiguration= AppBarConfiguration(set,bind.drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)
        bind.mainNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun initPermission() {
        ishasPermission = hasPermission(this)
        if (!ishasPermission) {
            getPermission(this)
        }
    }
}