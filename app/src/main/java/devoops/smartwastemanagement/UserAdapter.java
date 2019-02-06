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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<String> tvDesc = new ArrayList<>();
    ArrayList<String> tvStatus = new ArrayList<>();
    ArrayList<String> tvCurrently = new ArrayList<>();
    ArrayList<String> tvDate = new ArrayList<>();

    public UserAdapter(ArrayList<String> tvDesc,ArrayList<String> tvStatus, ArrayList<String> tvCurrently, ArrayList<String> tvDate){
        this.tvDesc = tvDesc;
        this.tvStatus = tvStatus;
        this.tvCurrently = tvCurrently;
        this.tvDate = tvDate;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, final int position) {


        holder.tvdesc.setText("Desc: "+tvDesc.get(position));
        holder.tvstatus.setText("Status: "+tvStatus.get(position));
        holder.tvcurrently.setText("Currently: "+tvCurrently.get(position));
        holder.tvdate.setText("Date: "+tvDate.get(position));
    }

    @Override
    public int getItemCount() {
        return tvDesc.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvdesc,tvstatus,tvcurrently,tvdate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvdesc = itemView.findViewById(R.id.tvNotificationDesc);
            tvstatus = itemView.findViewById(R.id.tvNotificationStatus);
            tvcurrently = itemView.findViewById(R.id.tvNotificationCurrently);
            tvdate = itemView.findViewById(R.id.tvNotificationDate);
        }
    }
}