package com.mkitsimple.counterboredom.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.ui.views.LatestChatItems
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_latest_chats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class LatestChatsFragment : Fragment() {

    companion object {
        val TAG = "LatestMessages"
        var latestMessagesMap = HashMap<String, ChatMessage>()
        val USER_KEY = "USER_KEY"
    }

    private lateinit var viewModel: LatestChatsViewModel
    val adapter = GroupAdapter<GroupieViewHolder>()
    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var job1: Job

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (  activity?.applicationContext as BaseApplication).appComponent
            .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory).get(LatestChatsViewModel::class.java)
        job1 = Job()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_latest_chats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // set item click listener on your adapter
        adapter.setOnItemClickListener { item, _ ->
            val row = item as LatestChatItems
            val intent = Intent(context, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        //Setup RecyclerView
        listenForLatestMessages()
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        val latestMessagesMap = HashMap<String, ChatMessage>()

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                val mWhen = "bukas"
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages(latestMessagesMap, mWhen)
            }

            override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                val mWhen = "bukas"
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages(latestMessagesMap, mWhen)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })



//        CoroutineScope(Main + job1).launch{
//            viewModel.listenForLatestMessages()
//            viewModel.listenForLatestMessagesResult?.observe(viewLifecycleOwner, Observer {
//                latestMessagesMap = it.first
//                refreshRecyclerViewMessages(latestMessagesMap, it.second)
//            })
//        }
    }

    private fun refreshRecyclerViewMessages(
        latestMessagesMap: HashMap<String, ChatMessage>,
        mWhen: String
    ) {
        adapter.clear()
        val resultMap = latestMessagesMap.entries.sortedByDescending { it.value.timestamp }.associate { it.toPair() }
        resultMap.values.forEach {
            adapter.add(LatestChatItems(it, mWhen))
            Toast.makeText(context, ""+it.type, Toast.LENGTH_SHORT).show()
        }
        recyclerviewLatestChats.adapter = adapter

        if (adapter.itemCount >= 1){
            textViewNoChats.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
    }

}
