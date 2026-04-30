package com.sylvia.back2me.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.sylvia.back2me.models.User
import com.sylvia.back2me.navigation.ROUT_HOME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sylvia.back2me.navigation.ROUTE_ADD_ITEM
import com.sylvia.back2me.navigation.ROUTE_REGISTER


class AuthViewModel(var navController: NavController, var context: Context){
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(username:String, email:String, password:String, confirmpassword:String){

        if (email.isBlank() || password.isBlank() || confirmpassword.isBlank() || username.isBlank()) {
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        } else if (password != confirmpassword) {
            Toast.makeText(context,"Password do not match", Toast.LENGTH_LONG).show()
        } else {

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){

                    val uid = mAuth.currentUser!!.uid

                    // DEFAULT role = "user"
                    val role = "user"

                    val userdata = User(  //Info is fetched from the model User
                        username = username,
                        email = email,
                        password = password,
                        uid = uid,
                        role = role
                    )

                    val regRef = FirebaseDatabase.getInstance().getReference("Users/$uid") //it refers to the backend model where the info goes-Users

                    regRef.setValue(userdata).addOnCompleteListener { result ->

                        if (result.isSuccessful){
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "${result.exception!!.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_REGISTER)
                        }

                    }

                } else {
                    navController.navigate(ROUTE_REGISTER)
                }
            }
        }
    }



    fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val uid = mAuth.currentUser!!.uid

                    // Read the user type from Firebase
                    val userRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    userRef.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value?.toString() ?: "user"

                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

                        if (role == "admin") {
                            navController.navigate(ROUTE_ADD_ITEM)   // <-- change to your actual route
                        }


                        else {
                            navController.navigate(ROUT_HOME)
                        }

                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUT_HOME)
                    }

                } else {
                    Toast.makeText(context, "Input correct email and password", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    fun logout(){
        mAuth.signOut()
        navController.navigate(ROUT_HOME)
    }


    fun isLoggedIn(): Boolean = mAuth.currentUser != null

    var searchResults by mutableStateOf<List<String>>(emptyList())
        private set

    fun searchItems(query: String) {

        if (query.isEmpty()) {
            searchResults = emptyList()
            return
        }

        val ref = FirebaseDatabase.getInstance().getReference("items")

        ref.get().addOnSuccessListener { snapshot ->

            val results = mutableListOf<String>()

            for (child in snapshot.children) {
                val itemName = child.child("name").getValue(String::class.java)

                if (itemName != null && itemName.contains(query, ignoreCase = true)) {
                    results.add(itemName)
                }
            }

            searchResults = results
        }
    }

}