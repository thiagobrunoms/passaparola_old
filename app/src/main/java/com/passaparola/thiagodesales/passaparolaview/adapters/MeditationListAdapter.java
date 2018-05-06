package com.passaparola.thiagodesales.passaparolaview.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.passaparola.thiagodesales.passaparolaview.holders.MeditationItemHolder;
import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;

import java.util.List;

public class MeditationListAdapter extends RecyclerView.Adapter<MeditationItemHolder>  {

    private final List<RSSMeditationItem> meditationList;
    private View.OnClickListener clickListener;

    public MeditationListAdapter(List<RSSMeditationItem> meditationList, View.OnClickListener clickListener) {
        this.meditationList = meditationList;
        this.clickListener = clickListener;
    }

    @Override
    public MeditationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meditation_item, parent, false);
        view.setOnClickListener(clickListener);
        return new MeditationItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MeditationItemHolder holder, int position) {
        RSSMeditationItem meditationItem = meditationList.get(position);

        String publishedDate = meditationItem.getPublishedDate();
        String dateParts[] = publishedDate.split("-");
        //It looks weird, but it is faster than using dateformat or similar
        publishedDate = dateParts[2].split("T")[0] + "/" + dateParts[1] + "/" + dateParts[0];

        holder.parolaDate.setText(publishedDate);
        holder.parolaTitle.setText(meditationItem.getParola("pt")); //TODO find out the current language
        holder.meditation.setText(meditationItem.getMeditation("pt"));

//        holder.shareButton.setOnClickListener(); TODO setup an event listener passed from the constructor that implements AsyncTask for sharing...

    }

    @Override
    public int getItemCount() {
        return meditationList != null ? meditationList.size() : 0;
    }
}