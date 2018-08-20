package com.trips.ebillapp;
/**
 * This is a adapter class
 * This class will fetch the data from the model class
 * And will bind it to the recycler view
 * It works as a bridge between model class and recycler view
 */

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderAdapter extends FirestoreRecyclerAdapter<Orders,OrderAdapter.ViewHolder> {

    //Defining context from the MainActivity class
    Context context;

    //Passing options from MainActivity class to the Adapter class
    public OrderAdapter(@NonNull FirestoreRecyclerOptions<Orders> options, Context context) {
        super(options);
        this.context = context;
    }

    /* This function is to bind all the parameters from the database
       to the layout views */
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Orders model) {
        holder.textViewDayDate.setText(model.getDay_date());
        holder.textViewType.setText(model.getType());
        holder.textViewStatus.setText(model.getStatus());
        holder.textViewId.setText("BN:"+model.getId());

        //This condition will only set the Job's person if a person has been assigned to the job
        if(!model.getJob_assignment().equals("Job not assigned yet")){
            holder.textViewjob_assign.setText(" "+model.getJob_assignment());
        }

        //This condition will set the bill if it has been calculated
        if(model.getGrand_total()!=0.0){
            holder.textViewTotal.setText("₹ "+model.getGrand_total());
        }

        /*Since the Electrician has been assigned, this condition wil set his/her image*/
        if(model.getType().equals("Electrician")){
            holder.setImage("https://firebasestorage.googleapis.com/v0/b/ebillapp-f92be.appspot.com/o/status_img.jpg?alt=media&token=a928ef8b-ed5a-427a-8e84-05fed2585984");
        }

        //This function will pass all the parameters that will be used again to the OrderBill class
        //It will also send the user to the OrderBill class
        final String orderId = model.getId();
        final String orderType = model.getType();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderBill = new Intent(context,OrderBill.class);
                orderBill.putExtra("orderId",orderId);
                orderBill.putExtra("orderType",orderType);
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                String docId = snapshot.getId();
                orderBill.putExtra("docId",docId);
                context.startActivity(orderBill);
            }
        });
    }

    //This method will inflate the layout to the recycler view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,
                parent,false);
        return new ViewHolder(view);
    }

    //Class to define all the layout views
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDayDate;
        TextView textViewId;
        TextView textViewType;
        TextView textViewStatus;
        TextView textViewTotal;
        TextView textViewjob_assign;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewDayDate = (TextView) itemView.findViewById(R.id.order_day_date);
            textViewId = (TextView) itemView.findViewById(R.id.order_id);
            textViewType = (TextView) itemView.findViewById(R.id.order_type);
            textViewStatus = (TextView) itemView.findViewById(R.id.order_status);
            textViewTotal = (TextView) itemView.findViewById(R.id.order_grand_total);
            textViewjob_assign = (TextView)itemView.findViewById(R.id.order_type_name);
        }

        //This method will the set the image to to circularImageView
        public void setImage(String thumb_image){
            CircleImageView userImageView = (CircleImageView)itemView.findViewById(R.id.order_img);
            //Using Picasso library to load the image
            Picasso.get().load(thumb_image).placeholder(R.drawable.user_image).into(userImageView);
        }
    }
}
