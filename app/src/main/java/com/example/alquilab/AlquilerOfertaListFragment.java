package com.example.alquilab;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Debug;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alquilab.databinding.FragmentAlquilerofertaListBinding;
import com.example.alquilab.databinding.AlquilerofertaListContentBinding;

import com.example.alquilab.placeholder.PlaceholderContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * A fragment representing a list of AlquilerOfertas. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link AlquilerOfertaDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */



public class AlquilerOfertaListFragment extends Fragment {

    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private FragmentAlquilerofertaListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAlquilerofertaListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        RecyclerView recyclerView = binding.alquilerofertaList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.alquileroferta_detail_nav_container);

        setupRecyclerView(recyclerView, itemDetailFragmentContainer);
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View itemDetailFragmentContainer
    ) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                PlaceholderContent.ITEMS,
                itemDetailFragmentContainer
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PlaceholderContent.PlaceholderItem> mValues;
        private final View mItemDetailFragmentContainer;

        SimpleItemRecyclerViewAdapter(List<PlaceholderContent.PlaceholderItem> items,
                                      View itemDetailFragmentContainer) {
            mValues = items;
            mItemDetailFragmentContainer = itemDetailFragmentContainer;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_alquileres,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            DatabaseReference mDatabase;
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            String userID = mAuth.getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Casa").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nombre = snapshot.child("estado").getValue(String.class);
                        String barrio = snapshot.child("barrio").getValue(String.class);
                        holder.nombre.setText(nombre);
                        holder.barrio.setText(barrio);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Fallo la lectura: " + error.getCode());
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                // Setting the item id as the clip data so that the drop target is able to
                // identify the id of the content
                ClipData.Item clipItem = new ClipData.Item(mValues.get(position).id);
                ClipData dragData = new ClipData(
                        ((PlaceholderContent.PlaceholderItem) v.getTag()).content,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        clipItem
                );

                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                } else {
                    v.startDrag(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nombre, barrio, precio;
            ImageView photo;

            public ViewHolder(@NonNull View v) {
                super(v);
                nombre = v.findViewById(R.id.nombreView);
                barrio = v.findViewById(R.id.barrioView);
                precio = v.findViewById(R.id.precioView);
                photo = v.findViewById(R.id.photo);
            }
        }
    }
}