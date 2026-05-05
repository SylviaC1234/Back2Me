package com.sylvia.back2me.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.sylvia.back2me.models.User
import com.sylvia.back2me.navigation.ROUTE_HOME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sylvia.back2me.navigation.ROUTE_ADD_ITEM
import com.sylvia.back2me.navigation.ROUTE_LOGIN
import com.sylvia.back2me.navigation.ROUTE_REGISTER


class AuthViewModel(var navController: NavController, var context: Context){
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(username: String, email: String, password: String, confirmpassword: String) {

        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmpassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val uid = mAuth.currentUser?.uid

                    if (uid == null) {
                        Toast.makeText(context, "User ID error", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }

                    val role = "user"

                    val userdata = User(
                        username = username,
                        email = email,
                        password = password,
                        uid = uid,
                        role = role
                    )

                    val regRef = FirebaseDatabase.getInstance()
                        .getReference("Users/$uid")

                    regRef.setValue(userdata)
                        .addOnCompleteListener { dbTask ->

                            if (dbTask.isSuccessful) {

                                Toast.makeText(
                                    context,
                                    "Registered Successfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                // ✅ Navigate to login after success
                                navController.navigate(ROUTE_LOGIN) {
                                    popUpTo(0)
                                }

                            } else {

                                Toast.makeText(
                                    context,
                                    dbTask.exception?.message ?: "Database error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                } else {

                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Signup failed",
                        Toast.LENGTH_LONG
                    ).show()
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
                            navController.navigate(ROUTE_HOME)
                        }

                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_HOME)
                    }

                } else {
                    Toast.makeText(context, "Input correct email and password", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    fun logout(){
        mAuth.signOut()
        navController.navigate(ROUTE_HOME)
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