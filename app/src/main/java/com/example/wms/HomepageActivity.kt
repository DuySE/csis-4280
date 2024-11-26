package com.example.wms

import android.os.Bundle
import android.widget.Toast
import com.example.wms.api.UserRepository
import com.example.wms.databinding.ActivityHomepageBinding


// This activity is mainly here to add a buffer so user won't crash on back button press.
class HomepageActivity : DrawerActivity() {
    private lateinit var repository: UserRepository

    override fun setUpBackButton() {
        return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homepageBinding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(homepageBinding.getRoot())
        allocateActivityTitle("Homepage")

        // Initialize repository with context
        repository = UserRepository(this)

        repository.getUser(StoredDataHelper.get(this),
            onSuccess = { user ->
                val textViewUsername = homepageBinding.textHomeUsername
                textViewUsername.text = user.username
            }, onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            })
    }
}