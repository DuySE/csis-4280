package com.example.wms

import android.os.Bundle
import com.example.wms.databinding.ActivityUsersBinding

class UserListActivity : DrawerActivity() {
    private lateinit var usersBinding: ActivityUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usersBinding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(usersBinding.getRoot())
        allocateActivityTitle("Messages")
    }
}