package com.mahankalstatus.android.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mahankalstatus.android.R;
import com.mahankalstatus.android.bean.AddStatus;
import com.mahankalstatus.android.util.Clipboard;
import com.mahankalstatus.android.util.Utility;

import java.util.ArrayList;

public class DisplayStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    private ArrayList<AddStatus> mList = new ArrayList<>();

    public DisplayStatusAdapter(Activity activity, ArrayList<AddStatus> list) {
        this.mActivity = activity;
        this.mList = list;
        adView();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ad_view, parent, false);
            return new ViewHolderAd(view);
        } else {
            view = LayoutInflater.from(mActivity).inflate(R.layout.status_item, parent, false);
            return new ViewHolderStatus(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            AdRequest adRequestDashBoard = new AdRequest.Builder().build();
            ((ViewHolderAd) holder).adView.loadAd(adRequestDashBoard);
        } else {
            ((ViewHolderStatus) holder).textViewStatus.setText(mList.get(position).getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position) == null) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Ad view
     */
    private void adView() {
        if (Utility.isNetworkAvailable(mActivity)) {
            int count = 0;
            for (int i = 0; i < mList.size(); i++) {
                count++;
                if (count >= 5) {
                    mList.add(i, null);
                    count = 0;
                }
            }
        }
    }

    private class ViewHolderStatus extends RecyclerView.ViewHolder {

        TextView textViewStatus;
        ImageView imageViewShare;
        ImageView imageViewCopy;

        ViewHolderStatus(View view) {
            super(view);

            textViewStatus = (TextView) view.findViewById(R.id.textViewStatus);
            imageViewShare = (ImageView) view.findViewById(R.id.imageViewShare);
            imageViewCopy = (ImageView) view.findViewById(R.id.imageViewCopy);

            imageViewShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mList.get(getAdapterPosition()).getStatus());
                    sendIntent.setType("text/plain");
                    mActivity.startActivity(sendIntent);
                }
            });

            imageViewCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Clipboard.copyToClipboard(mActivity, mList.get(getAdapterPosition()).getStatus());
                }
            });
        }
    }

    private class ViewHolderAd extends RecyclerView.ViewHolder {

        AdView adView;

        ViewHolderAd(View view) {
            super(view);

            adView = (AdView) view.findViewById(R.id.adView);
        }
    }
}