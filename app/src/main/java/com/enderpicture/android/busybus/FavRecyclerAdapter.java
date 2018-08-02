package com.enderpicture.android.busybus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

public class FavRecyclerAdapter extends RecyclerView.Adapter<FavRecyclerAdapter.Holder> {

    Context mContext;

    ArrayList<String> stationIDs = new ArrayList<>();

    public FavRecyclerAdapter(Context context, int mode) {
        mContext = context;

        String type = "";
        if (mode == 0) {
            type = SQLiteFavHelper.TYPE_STATION;
        } else if (mode == 1) {
            type = SQLiteFavHelper.TYPE_BUSROUTE;
        }
        if (!type.equals("")) {
            SQLiteFavHelper sqLiteFavHelper = new SQLiteFavHelper(mContext);
            SQLiteDatabase database = sqLiteFavHelper.getReadableDatabase();

            String[] columns = {SQLiteFavHelper.COL_VALUE};
            String selection = SQLiteFavHelper.COL_TYPE + "='" + type + "'";
            Cursor cursor = database.query(SQLiteFavHelper.TAB_NAME, columns, selection, null, null, null, null);

            while (cursor.moveToNext()) {
                String stationID = cursor.getString(cursor.getColumnIndex(SQLiteFavHelper.COL_VALUE));
                stationIDs.add(stationID);
            }

            database.close();
        }
    }

    @NonNull
    @Override
    public FavRecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.fav_item, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull FavRecyclerAdapter.Holder holder, int position) {
        holder.setItem(stationIDs.get(position));
    }

    @Override
    public int getItemCount() {
        return stationIDs.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView textView;

        public Holder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        public void setItem(String text) {
            textView.setText(text);
        }
    }

}
