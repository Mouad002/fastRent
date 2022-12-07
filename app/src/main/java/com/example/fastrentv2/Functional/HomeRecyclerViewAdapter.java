package com.example.fastrentv2.Functional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastrentv2.Model.Property;
import com.example.fastrentv2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> implements Filterable
{
    // attributes
    Context context;
    ArrayList<Property> firstList,secondList;
    MyActionListener listener;
    // constructor
    public HomeRecyclerViewAdapter(Context context, ArrayList<Property> list)
    {
        this.context = context;
        this.firstList = list;
        secondList = new ArrayList<>(list);
    }

    public interface MyActionListener
    {
        public void onItemClickAction(Property p);
    }

    public void setOnItemClickAction(MyActionListener listener)
    {
        this.listener = listener;
    }
    @NonNull
    @Override
    public HomeRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_home_row,parent,false);

        return new HomeRecyclerViewAdapter.MyViewHolder(view,listener, firstList);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewAdapter.MyViewHolder holder, int position)
    {
        holder.tv_title.setText(firstList.get(position).getPropertyTitle());
        holder.tv_description.setText(firstList.get(position).getDescription());
        holder.tv_city.setText(firstList.get(position).getCity());
        holder.tv_price.setText(Double.toString(firstList.get(position).getPrice()));
        Picasso.get().load(firstList.get(position).getImage()).into(holder.postImage);
//        holder.postImage.setImageResource(list.get(position).getImage());
    }

    @Override
    public int getItemCount()
    {
        return firstList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        // row attribute
        private ImageView postImage;
        private TextView tv_title,tv_description,tv_city,tv_price;
        public MyViewHolder(@NonNull View itemView, MyActionListener listener, ArrayList<Property> list)
        {
            super(itemView);
            postImage = itemView.findViewById(R.id.home_row_iv);
            tv_title = itemView.findViewById(R.id.home_row_tv_title);
            tv_description = itemView.findViewById(R.id.home_row_tv_description);
            tv_city = itemView.findViewById(R.id.home_row_tv_city);
            tv_price = itemView.findViewById(R.id.home_row_tv_price);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null)
                    {
                        listener.onItemClickAction(list.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Property> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(secondList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Property item : secondList) {
                    if (item.getPropertyTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            firstList.clear();
            firstList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
