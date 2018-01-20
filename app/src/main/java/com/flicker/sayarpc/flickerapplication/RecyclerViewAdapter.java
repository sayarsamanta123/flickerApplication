package com.flicker.sayarpc.flickerapplication;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<ItemObject> photoList;
    private List<ItemObject> photoListFiltered;
    private PhotoAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView phone;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.photo_name);
            phone = view.findViewById(R.id.photo);
            //thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onPhotoSelected(photoListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public RecyclerViewAdapter(Context context, List<ItemObject> photoList, PhotoAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.photoList = photoList;
        this.photoListFiltered = photoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ItemObject contact = photoListFiltered.get(position);
        holder.name.setText(contact.getName());
        //holder.phone.setText(contact.getPhone());

        Picasso.with(context)
                .load(contact.getUrl())
                .into(holder.phone);
    }

    @Override
    public int getItemCount() {
        return photoListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    photoListFiltered = photoList;
                } else {
                    List<ItemObject> filteredList = new ArrayList<>();
                    for (ItemObject row : photoList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    photoListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = photoListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                photoListFiltered = (ArrayList<ItemObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface PhotoAdapterListener {
        void onPhotoSelected(ItemObject photo);
    }
}


