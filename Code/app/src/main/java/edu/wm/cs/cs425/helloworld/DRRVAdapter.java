package edu.wm.cs.cs425.helloworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DRRVAdapter extends RecyclerView.Adapter<DRRVAdapter.MyViewHolder> {

    Context context;

    ArrayList<ReviewDisplayModel> reviewDisplayModelArrayList;

    public DRRVAdapter(Context context, ArrayList<ReviewDisplayModel> reviewDisplayModelArrayList){
        this.context = context;
        this.reviewDisplayModelArrayList = reviewDisplayModelArrayList;
    }

    @NonNull
    @Override
    public DRRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.displayreviews_recycler,parent,false);
        return new DRRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DRRVAdapter.MyViewHolder holder, int position) {
        holder.foodName.setText(reviewDisplayModelArrayList.get(position).getFoodname());
        holder.foodName.setText(reviewDisplayModelArrayList.get(position).getLocationname());
        holder.foodName.setText(reviewDisplayModelArrayList.get(position).getusername());


    }

    @Override
    public int getItemCount() {
        return reviewDisplayModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView foodName, locationname, username;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            foodName= itemView.findViewById(R.id.Foodname);
            locationname=itemView.findViewById(R.id.locationname);
            username= itemView.findViewById(R.id.username);

        }
    }
}