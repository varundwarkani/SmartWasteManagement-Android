package devoops.smartwastemanagement;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ServerComplaintAdapter extends RecyclerView.Adapter<ServerComplaintAdapter.ViewHolder> {

    ArrayList<String> tvDesc = new ArrayList<>();
    ArrayList<String> tvUid = new ArrayList<>();

    public ServerComplaintAdapter(ArrayList<String> tvDesc, ArrayList<String> tvUid){
        this.tvDesc = tvDesc;
        this.tvUid = tvUid;
    }

    @Override
    public ServerComplaintAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_server_complaint, parent, false);
        return new ServerComplaintAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ServerComplaintAdapter.ViewHolder holder, final int position) {


        holder.tvNotificationDesc.setText(tvDesc.get(position));
        holder.tvNotificationPercent.setText(tvUid.get(position));
        holder.btaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "Forwarded!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tvDesc.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNotificationDesc,tvNotificationPercent;
        Button btaction;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNotificationDesc = itemView.findViewById(R.id.tvNotificationDesc);
            btaction = itemView.findViewById(R.id.btaction);
            tvNotificationPercent = itemView.findViewById(R.id.tvNotificationPercent);
        }
    }
}