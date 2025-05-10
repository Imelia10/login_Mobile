package com.example.rewear_app1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnItemClickListener listener;

    public UserAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout activity_admin_user.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Bind data ke elemen-elemen yang ada di layout
        holder.id.setText(String.valueOf(user.getId()));
        holder.firstName.setText(user.getFirstName());
        holder.lastName.setText(user.getLastName());
        holder.phone.setText(user.getPhone());
        holder.email.setText(user.getEmail());
        holder.password.setText(user.getPassword());
        holder.alamat.setText(user.getAlamat());
        holder.ttl.setText(user.getTtl());

        // Menangani event klik pada item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView id, firstName, lastName, phone, email, password, alamat, ttl;
        ImageView userPhoto;

        public UserViewHolder(View itemView) {
            super(itemView);

            // Temukan elemen berdasarkan ID di layout activity_admin_user.xml
            id = itemView.findViewById(R.id.textId); // Sesuaikan dengan ID yang benar
            firstName = itemView.findViewById(R.id.textFirstName); // Sesuaikan dengan ID yang benar
            lastName = itemView.findViewById(R.id.textLastName); // Sesuaikan dengan ID yang benar
            phone = itemView.findViewById(R.id.textPhone); // Sesuaikan dengan ID yang benar
            email = itemView.findViewById(R.id.textEmail); // Sesuaikan dengan ID yang benar
            password = itemView.findViewById(R.id.textPassword); // Sesuaikan dengan ID yang benar
            alamat = itemView.findViewById(R.id.textAlamat); // Sesuaikan dengan ID yang benar
            ttl = itemView.findViewById(R.id.textTtl); // Sesuaikan dengan ID yang benar
            userPhoto = itemView.findViewById(R.id.imageUserPhoto); // Sesuaikan dengan ID yang benar
        }
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
}
