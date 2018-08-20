package com.trips.ebillapp;

/**
 * This is the Main Activity class
 * This activity will show the list of orders to the user
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    //Defining all the component variables of the Main Activity
    Toolbar mToolBar;
    OrderAdapter adapter;
    RecyclerView recyclerView;

    //Defining firestore reference for fetching data from firestore
    FirebaseFirestore db;
    CollectionReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the action bar for Main Activity
        mToolBar = (Toolbar)findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("My Orders");

        //Declaring an instance of FirebaseFirestore
        db = FirebaseFirestore.getInstance();
        orderRef = db.collection("Orders");

        //Method for setting up the Recycler View
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = orderRef;

        //Creating firestore recycler options for for passing the query to the Model class
        FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query,Orders.class).build();

        //Creating the adapter from the OrderAdapter class for Recycler view
        adapter = new OrderAdapter(options,MainActivity.this);

        //Setting attributes to the Recycler View
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    //Setting up the listening conditions of Recycler View Adapter

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
