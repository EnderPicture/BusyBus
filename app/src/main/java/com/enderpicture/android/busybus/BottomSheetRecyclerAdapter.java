package com.enderpicture.android.busybus;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BottomSheetRecyclerAdapter extends RecyclerView.Adapter {

    JSONArray mJSONArray;

    public BottomSheetRecyclerAdapter(JSONArray jsonArray) {
        mJSONArray = jsonArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.bottom_sheet_recycler_card, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            Holder h = (Holder) holder;
            h.setValues(mJSONArray.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mJSONArray.length();
    }

    public class Holder extends RecyclerView.ViewHolder{

        JSONObject jsonObject;
        View card;

        public Holder(View itemView) {
            super(itemView);

            card = itemView;
        }

        public void setValues(JSONObject obj) {
            jsonObject = obj;

            TextView textView = card.findViewById(R.id.textView);

            String s = "";

            try {
                s += jsonObject.getString("RouteNo") + "\n";
                s += jsonObject.getString("RouteName") + "\n";

                JSONArray schedules = jsonObject.getJSONArray("Schedules");

                for (int i = 0; i < schedules.length(); i++) {
                    JSONObject schedule = schedules.getJSONObject(i);

                    s += "\n\t" + schedule.getString("ExpectedLeaveTime") + "\n";
                    s += "\t arrives in " + schedule.getString("ExpectedCountdown") +" min \n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            textView.setText(s);
        }
    }

}
