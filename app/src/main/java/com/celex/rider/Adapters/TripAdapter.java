package com.celex.rider.Adapters;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.celex.rider.AllActivitys.TripActivity;
import com.celex.rider.R;
import com.roam.sdk.Roam;
import com.roam.sdk.callback.RoamDeleteTripCallback;
import com.roam.sdk.callback.RoamSyncTripCallback;
import com.roam.sdk.callback.RoamTripCallback;
import com.roam.sdk.callback.RoamTripStatusCallback;
import com.roam.sdk.models.ActiveTrips;
import com.roam.sdk.models.RoamError;
import com.roam.sdk.models.RoamTripStatus;


import java.util.ArrayList;
import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ItemHolder> {

    private TripActivity activity;
    private List<ActiveTrips> lists = new ArrayList<>();

    public TripAdapter(TripActivity activity) {
        this.activity = activity;
    }

    public void addList(List<ActiveTrips> lists) {
        this.lists.clear();
        this.lists.addAll(lists);
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final ActiveTrips activeTrips = lists.get(position);

        Roam.getTripStatus(activeTrips.getTripId(), new RoamTripStatusCallback() {
                 @Override
                public void onSuccess(RoamTripStatus RoamTrip) {

                     holder.mTxtDistance.setText( "getDistance"+ RoamTrip.getDistance());

                     holder.mTxtDuration.setText("getDuration"+ RoamTrip.getDuration());
                     holder.mTxtSpeed.setText("getSpeed"+RoamTrip.getSpeed());
                     holder.mTxtPase.setText( "getPace"+ RoamTrip.getPace());
                     holder.mTxtTripId.setText(activeTrips.getTripId());


                }
                @Override
                public void onFailure(RoamError RoamError) {
                    Log.d("geo","error"+ RoamError.getMessage());
                // do something when get trip details error
                // access Roam error code with RoamError.getCode()
                // access Roam error message with RoamError.getMessage()
                }

        });

        holder.mTxtTripId.setText(activeTrips.getTripId());

        holder.mTxtDate.setText(activeTrips.getUpdatedAt());
        if (!TextUtils.isEmpty(activeTrips.getSyncStatus())) {
            holder.mTxtSyncStatus.setText("Trip status: " + activeTrips.getSyncStatus());
        }
        if (activeTrips.getEnded()) {
            holder.mTxtStart.setTextColor(activity.getResources().getColor(R.color.accent));
            holder.mTxtResume.setTextColor(activity.getResources().getColor(R.color.accent));
            holder.mTxtPause.setTextColor(activity.getResources().getColor(R.color.accent));
            holder.mTxtStop.setTextColor(activity.getResources().getColor(R.color.accent));
            holder.mTxtForceStop.setTextColor(activity.getResources().getColor(R.color.accent));
        } else if (activeTrips.isStarted()) {
            if (activeTrips.isPaused()) {
                holder.mTxtStart.setTextColor(activity.getResources().getColor(R.color.accent));
                holder.mTxtResume.setTextColor(activity.getResources().getColor(R.color.black));
                holder.mTxtPause.setTextColor(activity.getResources().getColor(R.color.accent));
                holder.mTxtStop.setTextColor(activity.getResources().getColor(R.color.black));
                holder.mTxtForceStop.setTextColor(activity.getResources().getColor(R.color.black));
            } else {
                holder.mTxtStart.setTextColor(activity.getResources().getColor(R.color.accent));
                holder.mTxtResume.setTextColor(activity.getResources().getColor(R.color.accent));
                holder.mTxtPause.setTextColor(activity.getResources().getColor(R.color.black));
                holder.mTxtStop.setTextColor(activity.getResources().getColor(R.color.black));
                holder.mTxtForceStop.setTextColor(activity.getResources().getColor(R.color.black));
            }
        } else {
            holder.mTxtStart.setTextColor(activity.getResources().getColor(R.color.black));
            holder.mTxtResume.setTextColor(activity.getResources().getColor(R.color.accent));
            holder.mTxtPause.setTextColor(activity.getResources().getColor(R.color.accent));
            holder.mTxtStop.setTextColor(activity.getResources().getColor(R.color.accent));
            holder.mTxtForceStop.setTextColor(activity.getResources().getColor(R.color.accent));
        }
        holder.mTxtSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Roam.syncTrip(activeTrips.getTripId(), new RoamSyncTripCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        removeItem(position);
                        activity.refreshList();
                    }

                    @Override
                    public void onFailure(RoamError error) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        Toast.makeText(activity, "Trip Sync: " + activeTrips.getTripId() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.mTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showView(holder.mProgressBar);
                hideView(holder.mTxtStart);
                hideView(holder.mTxtResume);
                hideView(holder.mTxtPause);
                hideView(holder.mTxtStop);
                hideView(holder.mTxtForceStop);
                Roam.deleteTrip(activeTrips.getTripId(), new RoamDeleteTripCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        removeItem(position);
                        activity.refreshList();
                    }

                    @Override
                    public void onFailure(RoamError error) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        Toast.makeText(activity, "Trip deleted: " + activeTrips.getTripId() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.mTxtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showView(holder.mProgressBar);
                hideView(holder.mTxtStart);
                hideView(holder.mTxtResume);
                hideView(holder.mTxtPause);
                hideView(holder.mTxtStop);
                hideView(holder.mTxtForceStop);
                Roam.startTrip(activeTrips.getTripId(), null, new RoamTripCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        activity.refreshList();
                    }

                    @Override
                    public void onFailure(RoamError error) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        Toast.makeText(activity, "Trip started: " + activeTrips.getTripId() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.mTxtResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Roam.resumeTrip(activeTrips.getTripId(), new RoamTripCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        hideView(holder.mProgressBar);
                      //  showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                      //  showView(holder.mTxtStop);
                     //   showView(holder.mTxtForceStop);
                        activity.refreshList();
                    }

                    @Override
                    public void onFailure(RoamError error) {
                        hideView(holder.mProgressBar);
                        //showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                       // showView(holder.mTxtStop);
                       // showView(holder.mTxtForceStop);
                        Toast.makeText(activity, "Trip resume: " + activeTrips.getTripId() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.mTxtPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Roam.pauseTrip(activeTrips.getTripId(), new RoamTripCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        hideView(holder.mProgressBar);
                       // showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                      //  showView(holder.mTxtStop);
                      //  showView(holder.mTxtForceStop);
                        activity.refreshList();
                    }

                    @Override
                    public void onFailure(RoamError error) {
                        hideView(holder.mProgressBar);
                       // showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        //showView(holder.mTxtStop);
                       // showView(holder.mTxtForceStop);
                        Toast.makeText(activity, "Trip pause: " + activeTrips.getTripId() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.mTxtStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Roam.stopTrip(activeTrips.getTripId(), new RoamTripCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        activity.refreshList();
                    }

                    @Override
                    public void onFailure(RoamError error) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        Toast.makeText(activity, "Trip stop: " + activeTrips.getTripId() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.mTxtForceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Roam.forceStopTrip(activeTrips.getTripId(), new RoamTripCallback() {
                    @Override
                    public void onSuccess(String msg) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        activity.refreshList();
                    }

                    @Override
                    public void onFailure(RoamError error) {
                        hideView(holder.mProgressBar);
                        showView(holder.mTxtStart);
                        showView(holder.mTxtResume);
                        showView(holder.mTxtPause);
                        showView(holder.mTxtStop);
                        showView(holder.mTxtForceStop);
                        Toast.makeText(activity, "Trip force stop: " + activeTrips.getTripId() + " " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    private void removeItem(int position) {
        lists.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView mTxtTripId;
        private TextView mTxtDate;
        private TextView mTxtStart;
        private TextView mTxtResume;
        private TextView mTxtPause;
        private TextView mTxtStop;
        private TextView mTxtForceStop;
        private TextView mTxtSync;
        private TextView mTxtSyncStatus;
        private TextView mTxtDelete;
        private TextView mTxtDistance;
        private TextView mTxtDuration;
        private TextView mTxtSpeed;
        private TextView mTxtPase;
        private ProgressBar mProgressBar;

        ItemHolder(View itemView) {
            super(itemView);
            mTxtTripId = itemView.findViewById(R.id.txt_trip_id);
            mTxtDate = itemView.findViewById(R.id.txt_date);
            mTxtStart = itemView.findViewById(R.id.txt_start);
            mTxtResume = itemView.findViewById(R.id.txt_resume);
            mTxtPause = itemView.findViewById(R.id.txt_pause);
            mTxtStop = itemView.findViewById(R.id.txt_stop);
            mTxtForceStop = itemView.findViewById(R.id.txt_force_stop);
            mTxtSync = itemView.findViewById(R.id.txt_sync);
            mTxtSyncStatus = itemView.findViewById(R.id.txt_sync_status);
            mTxtDelete = itemView.findViewById(R.id.txt_delete);
            mTxtDistance = itemView.findViewById(R.id.txt_trip_distance);
            mTxtDuration = itemView.findViewById(R.id.txt_trip_duration);
            mTxtSpeed = itemView.findViewById(R.id.txt_trip_speed);
            mTxtPase = itemView.findViewById(R.id.txt_trip_pase);
            mProgressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}