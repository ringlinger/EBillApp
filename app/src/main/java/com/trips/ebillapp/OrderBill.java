package com.trips.ebillapp;

/**
 * This is the order bill class
 * This class will set up the full bill to the user
 */

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class OrderBill extends AppCompatActivity {

    //Defined all the variables from the layout
    Toolbar mToolbar;
    TextView bill_type, bill_status, bill_day_date, bill_address;
    TextView bill_job_assignment;
    TextView bill_start_time_ans, bill_end_time_ans, bill_payment_status_ans, bill_total_time_ans;
    TextView bill_services_ans, bill_services_loc_ans;
    TextView bill_rate_ans,bill_tax_ans_sgst,bill_tax_ans_cgst,bill_discount_ans,bill_grand_total_ans;
    ImageView bill_img;
    Button bill_cancel_btn;

    //Defined firebase firestore reference ans a storage reference
    FirebaseFirestore db1;
    FirebaseFirestore db2;
    StorageReference mStorage;

    //Defined other variables to be used in functions
    String TAG = "Value3: ";
    int final_HH,final_ss;
    String doc_Id;
    String start_time,end_time;
    String order_Type;
    String job_assign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_bill);

        //This method will find the view from the layout resource file that are attached with current Activity
        bill_status = (TextView)findViewById(R.id.bill_status);
        bill_day_date = (TextView)findViewById(R.id.bill_day_date);
        bill_address = (TextView)findViewById(R.id.bill_address);
        bill_job_assignment = (TextView)findViewById(R.id.bill_job_assignment);
        bill_start_time_ans = (TextView)findViewById(R.id.bill_start_time_ans);
        bill_end_time_ans = (TextView)findViewById(R.id.bill_end_time_ans);
        bill_payment_status_ans = (TextView)findViewById(R.id.bill_payment_status_ans);
        bill_services_ans = (TextView)findViewById(R.id.bill_services_ans);
        bill_services_loc_ans = (TextView)findViewById(R.id.bill_services_loc_ans);
        bill_total_time_ans = (TextView)findViewById(R.id.bill_total_time_ans);
        bill_rate_ans = (TextView)findViewById(R.id.bill_rate_ans); 
        bill_tax_ans_cgst = (TextView)findViewById(R.id.bill_tax_ans_cgst); 
        bill_tax_ans_sgst = (TextView)findViewById(R.id.bill_tax_ans_sgst); 
        bill_discount_ans = (TextView)findViewById(R.id.bill_discount_ans);
        bill_cancel_btn = (Button)findViewById(R.id.bill_cancel_btn);
        bill_grand_total_ans = (TextView)findViewById(R.id.bill_grand_total_ans);
        bill_img = (ImageView)findViewById(R.id.bill_img);
        bill_type = (TextView)findViewById(R.id.bill_type);

        //This function will fetch the data from parent intent and pass it to this class variables
        String order_ID = getIntent().getStringExtra("orderId");
        order_Type = getIntent().getStringExtra("orderType");
        doc_Id = getIntent().getStringExtra("docId");

        //Declaring an instance of FirebaseStorage
        mStorage = FirebaseStorage.getInstance().getReference();

        //Setting up the action bar for OrderBill Activity
        mToolbar = (Toolbar)findViewById(R.id.bill_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("BN "+order_ID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bill_type.setText(order_Type);

        /*This method will fetch data from the Firebase Firestore
        and will set the layout resource files with that data */
        db1 = FirebaseFirestore.getInstance();
        db1.collection("Orders").document(doc_Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                bill_day_date.setText(String.valueOf(documentSnapshot.get("day_date")));
                bill_status.setText(String.valueOf(documentSnapshot.get("status")));
                bill_address.setText(String.valueOf(documentSnapshot.get("address")));
                job_assign = String.valueOf(documentSnapshot.get("job_assignment"));
                bill_job_assignment.setText(job_assign);
                Log.d(TAG,String.valueOf(job_assign));
                bill_start_time_ans.setText(String.valueOf(documentSnapshot.get("start_time")));
                bill_end_time_ans.setText(String.valueOf(documentSnapshot.get("end_time")));
                bill_payment_status_ans.setText(String.valueOf(documentSnapshot.get("payment_status")));
                bill_services_ans.setText(String.valueOf(documentSnapshot.get("services")));
                bill_services_loc_ans.setText(String.valueOf(documentSnapshot.get("services_loc")));
                start_time = String.valueOf(documentSnapshot.get("start_time"));
                end_time = String.valueOf(documentSnapshot.get("end_time"));
                totalTime();
                bill_total_time_ans.setText(String.valueOf(final_HH + " hrs " + final_ss + " min"));
            }
        });

        //This is cancel button which will take you back to main activity
        bill_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderBill.this,MainActivity.class));
            }
        });

        setGrandTotal();

        //This method will set the image to the default image
        mStorage.child("status_img.jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                //Using Picasso library to load the image
                Picasso.get().load(task.getResult()).placeholder(R.drawable.user_image).into(bill_img);
            }
        });
    }


    /*
    This is the setGrandTotal() method
    It has a switch method that will detect the order type that is fetched from the database
    Then it will go to a new collection "Order_type"
    It will search for the order type document and if its is found, it will fetch all the rates/taxes
    Then this method will use the data to calculate the total bill
     */
    private void setGrandTotal() {
        db2 = FirebaseFirestore.getInstance();
        switch (order_Type){
            case "Electrician":

                //Referencing to the document
                db2.collection("Order_type").document("Electrician").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        //Fetch data from databse and converting to float
                        float rate = Float.parseFloat(String.valueOf(document.get("rate")));
                        float sgst1 = Float.parseFloat(String.valueOf(document.get("sgst")));
                        float cgst1 = Float.parseFloat(String.valueOf(document.get("cgst")));
                        float discount = Float.parseFloat(String.valueOf(document.get("discount")));

                        //Calculation of the total bill
                        int total_time = final_HH*60 + final_ss;
                        float grandTotal_without = (rate/60)*total_time;
                        float grandTotal = ((cgst1+sgst1)/100)*grandTotal_without + grandTotal_without -
                                (discount/100)*grandTotal_without;

                        //Setting up the layout resource files with the data fetched and produced
                        bill_rate_ans.setText(String.valueOf("₹ "+rate+"/hr"));
                        bill_tax_ans_sgst.setText(String.valueOf(sgst1+"% (SGST)"));
                        bill_tax_ans_cgst.setText(String.valueOf(cgst1+"% (CGST)"));
                        bill_discount_ans.setText(String.valueOf(discount+"%"));
                        bill_grand_total_ans.setText(String.valueOf("₹ "+grandTotal));
                        if (grandTotal!=0.0){
                            db2.collection("Orders").document(doc_Id).update("grand_total",grandTotal);
                        }
                    }
                });
                break;

            //Same as above
            case "Plumber":
                db2.collection("Order_type").document("Plumber").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Float rate = Float.parseFloat(String.valueOf(document.get("rate")));
                        float sgst1 = Float.parseFloat(String.valueOf(document.get("sgst")));
                        float cgst1 = Float.parseFloat(String.valueOf(document.get("cgst")));
                        float discount = Float.parseFloat(String.valueOf(document.get("discount")));
                        int total_time = final_HH*60 + final_ss;
                        float grandTotal_without = (rate/60)*total_time;
                        float grandTotal = ((cgst1+sgst1)/100)*grandTotal_without + grandTotal_without -
                                (discount/100)*grandTotal_without;
                        bill_rate_ans.setText(String.valueOf("₹ "+rate+"/hr"));
                        bill_tax_ans_sgst.setText(String.valueOf(sgst1+"% (SGST)"));
                        bill_tax_ans_cgst.setText(String.valueOf(cgst1+"% (CGST)"));
                        bill_discount_ans.setText(String.valueOf(discount+"%"));
                        bill_grand_total_ans.setText(String.valueOf("₹ "+grandTotal));
                        if (grandTotal!=0.0){
                            db2.collection("Orders").document(doc_Id).update("grand_total",grandTotal);
                        }
                    }
                });
                break;

            //Same as above
            case "Entrepreneur":
                db2.collection("Order_type").document("Entrepreneur").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        float rate = Float.parseFloat(String.valueOf(document.get("rate")));
                        float sgst1 = Float.parseFloat(String.valueOf(document.get("sgst")));
                        float cgst1 = Float.parseFloat(String.valueOf(document.get("cgst")));
                        float discount = Float.parseFloat(String.valueOf(document.get("discount")));
                        int total_time = (final_HH*60) + final_ss;
                        float grandTotal_without = (rate/60)*total_time;
                        float grandTotal = ((cgst1+sgst1)/100)*grandTotal_without + grandTotal_without -
                                (discount/100)*grandTotal_without;
                        bill_rate_ans.setText(String.valueOf("₹ "+rate+"/hr"));
                        bill_tax_ans_sgst.setText(String.valueOf(sgst1+"% (SGST)"));
                        bill_tax_ans_cgst.setText(String.valueOf(cgst1+"% (CGST)"));
                        bill_discount_ans.setText(String.valueOf(discount+"%"));
                        bill_grand_total_ans.setText(String.valueOf("₹ "+grandTotal));
                        if (grandTotal!=0.0){
                            db2.collection("Orders").document(doc_Id).update("grand_total",grandTotal);
                        }
                    }
                });
                break;
        }
    }

    /*
    This is the total time calculation method
    This will take the string type start and end time input in HH:mm format from the database
    Split it to HH and mm using split() function
    Convert them to integers
    Calculate the time difference
     */
    private void totalTime() {

        //Splitting HH:mm to HH ans mm
        String[] start_timeArray = start_time.split(":");
        int HH_s = Integer.parseInt(start_timeArray[0]);
        int mm_s = Integer.parseInt(start_timeArray[1]);

        String[] end_timeArray = end_time.split(":");
        int HH_e = Integer.parseInt(end_timeArray[0]);
        int mm_e = Integer.parseInt(end_timeArray[1]);

        //Putting the values to the variables
        final_HH = HH_e - HH_s;
        int ss = mm_e - mm_s;
        if (ss<0){
            final_ss = -ss;
        }else{
            final_ss = ss;
        }
    }
}
