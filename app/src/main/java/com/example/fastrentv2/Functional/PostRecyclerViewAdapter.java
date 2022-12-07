package com.example.fastrentv2.Functional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastrentv2.Model.Property;
import com.example.fastrentv2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostRecyclerViewAdapter  extends RecyclerView.Adapter<PostRecyclerViewAdapter.MyViewHolder>
{

    // attributes
    Context context;
    ArrayList<Property> list;
    MyActionListener listener;

    // constructor
    public PostRecyclerViewAdapter(Context context, ArrayList<Property> list)
    {
        this.context = context;
        this.list = list;
    }

    public interface MyActionListener
    {
        public void onItemClickAction(Property p,int action);
    }

    public void setOnItemClickAction(MyActionListener listener)
    {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_post_row,parent,false);

        return new PostRecyclerViewAdapter.MyViewHolder(view,list,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerViewAdapter.MyViewHolder holder, int position)
    {
        holder.tv_title.setText(list.get(position).getPropertyTitle());
        holder.tv_description.setText(list.get(position).getDescription());
        holder.tv_city.setText(list.get(position).getCity());
        holder.tv_price.setText(Double.toString(list.get(position).getPrice()));
        Picasso.get().load(list.get(position).getImage()).into(holder.postImage);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        // row attribute
        private ImageView postImage,menu;
        private TextView tv_title,tv_description,tv_city,tv_price;
        private ArrayList<Property> list;
        public MyViewHolder(@NonNull View itemView,ArrayList<Property> list, MyActionListener listener)
        {
            super(itemView);
            this.list = list;
            postImage = itemView.findViewById(R.id.post_row_iv);
            tv_title = itemView.findViewById(R.id.post_row_tv_title);
            tv_description = itemView.findViewById(R.id.post_row_tv_description);
            tv_city = itemView.findViewById(R.id.post_row_tv_city);
            tv_price = itemView.findViewById(R.id.post_row_tv_price);
            menu = itemView.findViewById(R.id.post_row_iv_menu);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu menu = new PopupMenu(itemView.getContext(),view);

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if(listener!=null)
                            {
                                switch(menuItem.getItemId())
                                {
                                    case R.id.post_menu_show:
                                        listener.onItemClickAction(list.get(getAdapterPosition()),1);
                                        return true;
                                    case R.id.post_menu_edit:
                                        listener.onItemClickAction(list.get(getAdapterPosition()),2);
                                        return true;
                                    case R.id.post_menu_delete:
                                        listener.onItemClickAction(list.get(getAdapterPosition()),3);
                                        return true;
                                }
                            }
                            return false;
                        }
                    });
                    menu.inflate(R.menu.post_menu);
                    menu.show();
                }
            });
        }

        private void show()
        {
            Toast.makeText(itemView.getContext(), list.get(getAdapterPosition()).getPropertyTitle(), Toast.LENGTH_SHORT).show();
        }

        private void edit()
        {
            Toast.makeText(itemView.getContext(), "edit", Toast.LENGTH_SHORT).show();
        }

        private void delete()
        {
            Toast.makeText(itemView.getContext(), "delete", Toast.LENGTH_SHORT).show();
        }
    }
}
