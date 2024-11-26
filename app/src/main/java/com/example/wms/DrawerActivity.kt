package com.example.wms

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wms.databinding.ActivityDrawerBinding
import com.google.android.material.navigation.NavigationView

open class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView
    private lateinit var backButton: ImageButton

    override fun setContentView(view: View?) {
        drawer = ActivityDrawerBinding.inflate(layoutInflater).root
        val container = drawer.findViewById<FrameLayout>(R.id.activityContainer)
        container.addView(view)
        super.setContentView(drawer)

        val toolbar = drawer.findViewById<Toolbar?>(R.id.drawerToolbar)
        setSupportActionBar(toolbar)

        setUpBackButton()
        navigation = drawer.findViewById<NavigationView>(R.id.drawerNavView)
        navigation.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navDrawOpen,
            R.string.navDrawClose
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    protected open fun setUpBackButton() {
        backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener(View.OnClickListener { view1: View? -> finish() })
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)
        //If else statement to change activities
        if (item.itemId == R.id.menuMainActivity) {
            startActivity(Intent(this, MainActivity::class.java))
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else if (item.itemId == R.id.menuMyProfileActivity) {
            startActivity(Intent(this, MyProfileActivity::class.java))
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else if (item.itemId == R.id.menuChatActivity) {
            startActivity(Intent(this, UserListActivity::class.java))
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else if (item.itemId == R.id.menuNewProductActivity) {
            startActivity(Intent(this, NewProductActivity::class.java))
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else if (item.itemId == R.id.menuManageProductActivity) {
            startActivity(Intent(this, ManageProductActivity::class.java))
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else if (item.itemId == R.id.menuTransactionHistoryActivity) {
            startActivity(Intent(this, TransactionActivity::class.java))
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else if (item.itemId == R.id.menuLogout) {
            val builder = AlertDialog.Builder(this)
            builder.setIcon(R.drawable.ic_launcher_foreground)
            builder.setTitle(R.string.app_name)
            builder.setMessage("Are you sure to logout?")
            builder.setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    StoredDataHelper.clear(this)
                    startActivity(Intent(this, LoginActivity::class.java))
                })
            builder.setNegativeButton(
                "CANCEL",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> dialog!!.cancel() })
            val alertDialog = builder.create()
            alertDialog.show()
        }
        return false
    }

    protected fun allocateActivityTitle(title: String?) {
        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }
    }
}
