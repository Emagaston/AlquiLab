package com.example.alquilab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.alquilab.model.Casa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    public void filterList(ArrayList<Casa> filterlist) {
        list = filterlist;
        notifyDataSetChanged();
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
        String estadoView = casa.getEstado();
        switch (estadoView) {
            case "Alquilado":
            case "Rented":
            case "Loué":
                holder.estado.setTextColor(Color.RED);
                break;
            case "Pausado":
            case "Slow":
            case "Lent":
                holder.estado.setTextColor(Color.parseColor("#FF9900"));
                break;
        }

        String Mid = casa.getId();
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.AlertTitle)
                        .setMessage(R.string.AlertMessage)
                        .setPositiveButton(R.string.AlertDeleteYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                root.child(Mid).removeValue();
                            }
                        })
                        .setNegativeButton(R.string.AlertDeleteNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("Mensaje", "Se cancelo la accion");
                            }
                        })
                        .show();
            }
        });

        String photoAl = casa.getUrlFoto();
        Glide.with(context).load(photoAl).centerCrop().into(holder.photo);

        String detail = casa.getDescripcion();
        String direccion = casa.getDireccion();
        String habitaciones = casa.getHabitaciones();
        String latitud = casa.getLatitud();
        String longitud = casa.getLongitud();
        String estado = casa.getEstado();

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetalleAlquiler.class);
                intent.putExtra("nom", holder.nombre.getText());
                intent.putExtra("barrio", holder.barrio.getText());
                intent.putExtra("precio", holder.precio.getText());
                intent.putExtra("imagen", photoAl);
                intent.putExtra("descripcion", detail);
                intent.putExtra("direccion", direccion);
                intent.putExtra("habitaciones", habitaciones);
                intent.putExtra("longitud1", longitud);
                intent.putExtra("latitud1", latitud);
                context.startActivity(intent);
            }
        });
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditarAlquiler.class);
                intent.putExtra("Mid", Mid);
                intent.putExtra("nom", holder.nombre.getText());
                intent.putExtra("precioM", holder.precio.getText());
                intent.putExtra("des", detail);
                intent.putExtra("est", estado);
                context.startActivity(intent);
            }
        });
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
