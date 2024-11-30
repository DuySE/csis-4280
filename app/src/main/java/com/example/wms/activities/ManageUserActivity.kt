package com.example.wms.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.adapters.UserRecyclerViewAdapter
import com.example.wms.apis.UserRepository
import com.example.wms.databinding.ActivityManageUserBinding
import com.example.wms.models.User
import java.util.Locale

class ManageUserActivity : DrawerActivity(), UserRecyclerViewAdapter.OnItemClickListener {
    private lateinit var userList: MutableList<User>
    private lateinit var recyclerViewUser: RecyclerView
    private lateinit var userAdapter: UserRecyclerViewAdapter
    private lateinit var searchView: SearchView
    private lateinit var txtViewNoUser: TextView
    private lateinit var filteredList: MutableList<User>

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manageUserBinding = ActivityManageUserBinding.inflate(layoutInflater)
        setContentView(manageUserBinding.root)
        allocateActivityTitle(getString(R.string.menuManageUserActivity))

        // Initialize user repository with context
        repository = UserRepository(this)

        // Initialize filtered user list
        userList = mutableListOf()
        filteredList = mutableListOf()

        txtViewNoUser = manageUserBinding.noUsersText
        searchView = manageUserBinding.searchView

        recyclerViewUser = manageUserBinding.usersList
        userAdapter = UserRecyclerViewAdapter(userList, this)
        recyclerViewUser.adapter = userAdapter
        recyclerViewUser.layoutManager = GridLayoutManager(this, 2)

        repository.getAllUsers(onSuccess = { users ->
            userList = users.filter { !it.isAdmin }.toMutableList()
            txtViewNoUser.text = if (userList.isEmpty()) getString(R.string.txtNoUserFound) else ""
            userAdapter.setFilteredList(userList)
        }, onError = { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterUser(newText, userList)
                return true
            }
        })
    }

    private fun filterUser(newText: String, users: List<User>) {
        filteredList = users.filter {
            it.username.lowercase(Locale.getDefault())
                .contains(newText.lowercase(Locale.getDefault()))
        }.toMutableList()
        txtViewNoUser.text = if (filteredList.isEmpty()) getString(R.string.txtNoUserFound) else ""
        userAdapter.setFilteredList(filteredList)
    }

    override fun onItemEdit(i: Int) {
        val intent: Intent = Intent(this, MyProfileEditActivity::class.java)
        val bundle: Bundle = Bundle()
        val list: MutableList<User> = if (filteredList.isEmpty()) userList else filteredList
        bundle.putString("USERNAME", list[i].username)
        bundle.putString("PHONE", list[i].phone)
        val address = list[i].address
        if (address?.split(", ")?.size == 3) {
            val city = address.split(", ")[1]
            val province = address.split(", ")[2]
            val numberAndStreet = address.split(", ")[0]
            val homeNumber = numberAndStreet.split(" ")[0]
            val streetName = numberAndStreet.split(" ").drop(1).joinToString(" ")

            bundle.putString("HOME_NUMBER", homeNumber)
            bundle.putString("STREET", streetName.toString())
            bundle.putString("CITY", city)
            bundle.putString("PROVINCE", province)
        }
        bundle.putString("IMG_NAME", list[i].profileImg)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onItemDelete(i: Int) {
        repository.deleteUser(
            username = if (filteredList.isEmpty()) userList[i].username.toString() else filteredList[i].username.toString(),
            onSuccess = { _ ->
                if (filteredList.isEmpty()) userList.removeAt(i)
                else filteredList.removeAt(i)
                userAdapter.notifyItemRemoved(i)
                txtViewNoUser.text = getString(R.string.txtNoUserFound)
                Toast.makeText(this, "User deleted successfully.", Toast.LENGTH_SHORT).show()
            }, onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            })
    }
}