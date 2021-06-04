package com.example.sqliteproject

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqliteproject.databinding.ActivityMainBinding
import com.example.sqliteproject.databinding.UpdateDialogBinding
import com.google.android.material.snackbar.Snackbar
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener, MyAdapter.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    lateinit var dbHelper: DBHelper
    lateinit var adapter: MyAdapter
    private var userList = ArrayList<Model>()
    private var newList = ArrayList<Model>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title= "User List"
        binding.insertBtn.setOnClickListener(this)
        binding.logoutBtn.setOnClickListener(this)

        binding.recycleId.layoutManager= LinearLayoutManager(this)
        binding.recycleId.setHasFixedSize(true)

        loadData()

        binding.swipeLayout.setOnRefreshListener {
            userList.clear()
            newList.clear()

            val cursor = dbHelper.show()

            while(cursor.moveToNext()){
                val id = cursor.getString(0).toString()
                val name = cursor.getString(1).toString()
                val email = cursor.getString(2).toString()

                val user = Model(id,name,email)
                userList.add(user)
            }
            newList.addAll(userList)
            adapter= MyAdapter(newList,this)
            binding.recycleId.adapter=adapter
            binding.swipeLayout.isRefreshing=false
        }
    }

    private fun loadData() {
        dbHelper = DBHelper(this)

        val cursor = dbHelper.show()

        if(cursor.count==0){
            Snackbar.make(binding.mainLayout,"No Data Found",Snackbar.LENGTH_SHORT).show()
        }
        else{

            while(cursor.moveToNext()){
                val id = cursor.getString(0).toString()
                val name = cursor.getString(1).toString()
                val email = cursor.getString(2).toString()

                val user = Model(id,name,email)
                userList.add(user)
            }
            newList.addAll(userList)
            adapter= MyAdapter(newList,this)
            binding.recycleId.adapter=adapter
        }
    }


    override fun onClick(view: View?) {
        when(view?.id){
            R.id.insertBtn ->{
                startActivity(Intent(this,InsertActivity::class.java))
            }

            R.id.logoutBtn ->{
                val sharedPreferences= getSharedPreferences("database",Context.MODE_PRIVATE)
                val edit= sharedPreferences.edit()
                edit.apply {
                    putString("status","notLogin")
                }.apply()

                val intent= Intent(this,LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onItemClick(position: Int) {
         Snackbar.make(binding.mainLayout,"Name is "+userList[position].name + "\nEmail is "+userList[position].email,Snackbar.LENGTH_SHORT).show()
    }

    override fun onLongItemClick(position: Int) {

        val dialog = AlertDialog.Builder(this)
        val option = arrayOf("Update", "Delete")
        dialog.setTitle("Choose an Option")

        dialog.setItems(option){dialogInterface, i: Int ->
          val select = option[i]

            if (select=="Update"){
                updateData(position)
            }
            else{
                val id= newList[position].id
                val value= dbHelper.delete(id)
                if(value>0){
                    newList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Snackbar.make(binding.mainLayout,"Delete Successfully",Snackbar.LENGTH_SHORT).show()

                }
                else{
                    Snackbar.make(binding.mainLayout,"Delete Failed",Snackbar.LENGTH_SHORT).show()

                }
            }

        }
        dialog.create().show()


    }

    private fun updateData(position: Int) {
        val dialog= AlertDialog.Builder(this)
        val view= UpdateDialogBinding.inflate(LayoutInflater.from(this))
        dialog.setView(view.root).setTitle("Update").setCancelable(true)
            .setPositiveButton("Update"){ dialogInterface: DialogInterface, i: Int ->

                val id= newList[position].id
                val name= view.updateName.text.toString()
                val email= view.updateEmail.text.toString()
                val user= Model(id,name, email)
                val value= dbHelper.update(user)
                if(value>0){
                    Snackbar.make(binding.mainLayout,"Update Successfully",Snackbar.LENGTH_SHORT).show()

                }
                else{
                    Snackbar.make(binding.mainLayout,"Update Failed",Snackbar.LENGTH_SHORT).show()

                }

            }.setNegativeButton("Close"){ dialogInterface: DialogInterface, i: Int ->

            }

        view.updateId.text= "Updating index no #${userList[position].id}"
        view.updateName.setText(userList[position].name)
        view.updateEmail.setText(userList[position].email)
        dialog.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater= menuInflater
        inflater.inflate(R.menu.menu_item,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.aboutId ->{
                Snackbar.make(binding.mainLayout,"About",Snackbar.LENGTH_SHORT).show()
            }
            R.id.searchId ->{

                val searchView: SearchView = item.actionView as SearchView

                searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if(newText!!.isNotEmpty()){
                            newList.clear()
                            val text= newText.toLowerCase(Locale.getDefault())
                            userList.forEach {
                                if(it.name.toLowerCase(Locale.getDefault()).contains(text) ||
                                        it.email.toLowerCase(Locale.getDefault()).contains(text)){
                                    newList.add(it)
                                }
                            }
                            binding.recycleId.adapter?.notifyDataSetChanged()
                        }
                        else{
                            newList.clear()
                            newList.addAll(userList)
                            binding.recycleId.adapter?.notifyDataSetChanged()
                        }
                        return true
                    }

                })
            }
        }

        return super.onOptionsItemSelected(item)
    }
}