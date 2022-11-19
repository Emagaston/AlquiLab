package com.example.alquilab;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.alquilab.model.Casa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;


public class AlquilerAdapter extends RecyclerView.Adapter<AlquilerAdapter.ViewHolder> {

    ArrayList<Casa> list;
    Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db =FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Casa");


    public AlquilerAdapter(Context context, ArrayList<Casa> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_list_alquileres,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Casa casa = list.get(position);
        holder.nombre.setText(casa.getNombre());
        holder.barrio.setText(casa.getBarrio());
        holder.precio.setText(casa.getPrecio());
        holder.estado.setText(casa.getEstado());
        String userID = mAuth.getUid();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                root.child(userID).removeValue();
            }
        });
        String photoAl = casa.getUrlFoto();
        Glide.with(context)
                        .load(photoAl)
                        .centerCrop()
                        .into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        Button delete, update;
        TextView nombre, barrio, precio, estado;
        ImageView photo;

        public ViewHolder(@NonNull View v) {
            super(v);
            delete = v.findViewById(R.id.btnViewDelete);
            update = v.findViewById(R.id.btnViewUpdate);
            estado = v.findViewById(R.id.textViewEstado);
            nombre = v.findViewById(R.id.nombreView);
            barrio = v.findViewById(R.id.barrioView);
            precio = v.findViewById(R.id.precioView);
            photo = v.findViewById(R.id.photo);
        }
    }
}
