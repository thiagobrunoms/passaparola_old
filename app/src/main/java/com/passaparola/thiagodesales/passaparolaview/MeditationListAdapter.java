package com.passaparola.thiagodesales.passaparolaview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.passaparola.thiagodesales.passaparolaview.MeditationItemHolder;
import com.passaparola.thiagodesales.passaparolaview.RSSMeditationItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MeditationListAdapter extends RecyclerView.Adapter<MeditationItemHolder> {

    private final List<RSSMeditationItem> meditationList;

    public MeditationListAdapter(List<RSSMeditationItem> meditationList) {
        this.meditationList = meditationList;
    }

    @Override
    public MeditationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MeditationItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meditation_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MeditationItemHolder holder, int position) {
        RSSMeditationItem meditationItem = meditationList.get(position);

        String publishedDate = meditationItem.getPublishedDate();
        String dateParts[] = publishedDate.split("-");
        //It looks weird, but it is faster than using dateformat or similar
        publishedDate = dateParts[2].split("T")[0] + "/" + dateParts[1] + "/" + dateParts[0];

        holder.parolaDate.setText(publishedDate);
        holder.parolaTitle.setText(meditationItem.getParolaPt()); //TODO find out the current language
        holder.meditation.setText(meditationItem.getMeditationPt());

//        holder.shareButton.setOnClickListener(); TODO setup an event listener passed from the constructor that implements AsyncTask for sharing...

    }

    @Override
    public int getItemCount() {
        return meditationList != null ? meditationList.size() : 0;
    }
}
