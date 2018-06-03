package com.passaparola.thiagodesales.passaparolaview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.passaparola.thiagodesales.passaparolaview.facade.Facade;
import com.passaparola.thiagodesales.passaparolaview.holders.MeditationItemHolder;
import com.passaparola.thiagodesales.passaparolaview.R;
import com.passaparola.thiagodesales.passaparolaview.listeners.MyOnOptionsClickListener;
import com.passaparola.thiagodesales.passaparolaview.model.RSSMeditationItem;
import com.passaparola.thiagodesales.passaparolaview.utils.Utils;

import java.util.List;

public class MeditationListAdapter extends RecyclerView.Adapter<MeditationItemHolder> implements View.OnClickListener {

    private final List<RSSMeditationItem> meditationList;
    private MyOnOptionsClickListener clickListener;
    private String languageId;
    private RecyclerView recyclerView;
    private Context context;

    public MeditationListAdapter(List<RSSMeditationItem> meditationList, MyOnOptionsClickListener clickListener, RecyclerView recyclerView, String languageId, Context context) {
        this.meditationList = meditationList;
        this.clickListener = clickListener;
        this.languageId = languageId;
        this.recyclerView = recyclerView;
        this.context = context;
    }

    public void setCurrentParolaLanguage(String languageId) {
        this.languageId = languageId;
    }

    @Override
    public MeditationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meditation_item, parent, false);
        view.setOnClickListener(this);
        return new MeditationItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MeditationItemHolder holder, final int position) {
        final RSSMeditationItem meditationItem = meditationList.get(position);

        holder.parolaDate.setText(Utils.isoDateToStandardFormat(meditationItem.getPublishedDate()));

        String parola = meditationItem.getParola(languageId);
        String meditation = meditationItem.getMeditation(languageId);

        holder.parolaTitle.setText(parola != null ? parola : context.getString(R.string.parola_unavailable));
        holder.meditation.setText(meditation != null ? meditation : context.getString(R.string.meditation_unavailable));

        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Clicando", "Id = " + view.getId());
                Log.d("Item selecionado", meditationItem.getPublishedDate());
                clickListener.onReadExperiences(meditationItem);
            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Clicando", "Id = " + view.getId());
                Log.d("Item selecionado", meditationItem.getPublishedDate());
                meditationItem.setCurrentParolaLanguage(languageId);
                clickListener.onShareMeditation(meditationItem);
            }
        });
        holder.writeExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Clicando", "Id = " + view.getId());
                Log.d("Item selecionado", meditationItem.getPublishedDate());
                clickListener.onWriteExperiences(meditationItem);
            }
        });

        holder.positionOnList = position;
    }

    @Override
    public int getItemCount() {
        return meditationList != null ? meditationList.size() : 0;
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildAdapterPosition(view);
        RSSMeditationItem meditationItem = meditationList.get(position);
        clickListener.onReadMeditation(meditationItem);
    }
}
