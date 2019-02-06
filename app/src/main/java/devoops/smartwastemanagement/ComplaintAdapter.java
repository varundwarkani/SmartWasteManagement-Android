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

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    ArrayList<String> tvDesc = new ArrayList<>();
    ArrayList<String> tvStatus = new ArrayList<>();
    ArrayList<String> tvCurrently = new ArrayList<>();
    ArrayList<String> tvDate = new ArrayList<>();
    ArrayList<String> tvUid = new ArrayList<>();
    ArrayList<String> tvUidd = new ArrayList<>();

    public ComplaintAdapter(ArrayList<String> tvDesc,ArrayList<String> tvStatus, ArrayList<String> tvCurrently, ArrayList<String> tvDate, ArrayList<String> tvUid, ArrayList<String> tvUidd){
        this.tvDesc = tvDesc;
        this.tvStatus = tvStatus;
        this.tvCurrently = tvCurrently;
        this.tvDate = tvDate;
        this.tvUid = tvUid;
        this.tvUidd = tvUidd;
    }

    @Override
    public ComplaintAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_complaint, parent, false);
        return new ComplaintAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ComplaintAdapter.ViewHolder holder, final int position) {


        holder.tvdesc.setText("Desc: "+tvDesc.get(position));
        holder.tvstatus.setText("Status: "+tvStatus.get(position));
        holder.tvcurrently.setText("Currently: "+tvCurrently.get(position));
        holder.tvdate.setText("Date: "+tvDate.get(position));

        if (tvStatus.get(position).equals("working"))
        {
            holder.btcomplaint.setVisibility(View.GONE);
        }
        if (tvStatus.get(position).equals("completed"))
        {
            holder.btcomplaint.setText("Revert?");
        }

        holder.btcomplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //push to firebase changing to service

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();

                databaseReference.child("profile/"+tvUid.get(position)+"/complaint/"+tvUidd.get(position)+"/status").setValue("working");
                databaseReference.child("profile/"+tvUid.get(position)+"/complaint/"+tvUidd.get(position)+"/currently").setValue("service");

                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+tvUid.get(position)+"/uiddate").setValue(tvUidd.get(position));

                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+tvUid.get(position)+"/status").setValue("working");
                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+tvUid.get(position)+"/currently").setValue("service");
                databaseReference.child("complaint/garbage/Chennai/key/service/"+tvUid.get(position)+"/date").setValue(tvDate.get(position));
                databaseReference.child("complaint/garbage/Chennai/key/service/"+tvUid.get(position)+"/status").setValue(tvStatus.get(position));
                databaseReference.child("complaint/garbage/Chennai/key/service/"+tvUid.get(position)+"/currently").setValue(tvCurrently.get(position));
                databaseReference.child("complaint/garbage/Chennai/key/service/"+tvUid.get(position)+"/complaint").setValue(tvDesc.get(position));
                databaseReference.child("complaint/garbage/Chennai/key/service/"+tvUid.get(position)+"/uid").setValue(tvUid.get(position));
                databaseReference.child("complaint/garbage/Chennai/key/service/"+tvUid.get(position)+"/uiddate").setValue(tvUidd.get(position));

                Toast.makeText(holder.itemView.getContext(), "Successfully updated.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (holder.itemView.getContext(), Main2Activity.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tvDesc.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvdesc,tvstatus,tvcurrently,tvdate;
        public Button btcomplaint;

        public ViewHolder(View itemView) {
            super(itemView);

            tvdesc = itemView.findViewById(R.id.tvNotificationDesc);
            tvstatus = itemView.findViewById(R.id.tvNotificationStatus);
            tvcurrently = itemView.findViewById(R.id.tvNotificationCurrently);
            tvdate = itemView.findViewById(R.id.tvNotificationDate);
            btcomplaint = itemView.findViewById(R.id.btcomplaint);
        }
    }
}