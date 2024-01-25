package com.example.foodbox.AdminFile.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodbox.AdminFile.Model.A_MenuNote;
import com.example.foodbox.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

public class A_MenuAdapter extends FirestoreRecyclerAdapter<A_MenuNote, A_MenuAdapter.MenuViewHolder> {

    public A_MenuAdapter(@NonNull FirestoreRecyclerOptions<A_MenuNote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull A_MenuNote model) {
        holder.m_name.setText(model.getMenu_name());
        holder.m_price.setText(""+model.getPrice());
        Picasso.get().load(model.getImage()).into(holder.m_image);

    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.a_menu_view,parent,false);
        return new MenuViewHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
//delete dhaba menu code
//    public void deleteItem(int position) {
//        getSnapshots().getSnapshot(position).getReference().delete();
//
//    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView m_name,m_price;
        ImageView m_image;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            m_name=itemView.findViewById(R.id.m_name);
            m_price=itemView.findViewById(R.id.m_price);
            m_image=itemView.findViewById(R.id.m_image);
        }
    }
}
