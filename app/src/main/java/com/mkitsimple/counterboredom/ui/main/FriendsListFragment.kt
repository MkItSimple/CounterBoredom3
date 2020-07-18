package com.mkitsimple.counterboredom.ui.main


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.User
import com.mkitsimple.counterboredom.ui.views.UserItem
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_friends_list.*
import kotlinx.coroutines.Job
import javax.inject.Inject

class FriendsListFragment : Fragment() {

    companion object {
        fun newInstance() = FriendsListFragment()
        const val USER_KEY = "USER_KEY"
        const val TAG = "FriendsListFragment"
    }

    private lateinit var viewModel: FriendsListViewModel
    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var job5: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (  activity?.applicationContext as BaseApplication).appComponent
            .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory).get(FriendsListViewModel::class.java)
        job5 = Job()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    setupRecyclerView(snapshot)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setupRecyclerView(snapshot: DataSnapshot) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        val uid = FirebaseAuth.getInstance().uid

        snapshot.children.forEach {
            val user = it.getValue(User::class.java)
            if (user!!.uid != uid) {
                adapter.add(UserItem(user))
            }
        }

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as UserItem
            val intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, userItem.user)
            startActivity(intent)
        }

        recyclerviewFriendsList.adapter = adapter
    }
}
