package com.passaparola.thiagodesales.passaparolaview.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.passaparola.thiagodesales.passaparolaview.R;


public class MeditationItemHolder extends RecyclerView.ViewHolder {

    public TextView parolaDate;
    public TextView parolaTitle;
    public TextView meditation;
    public ImageView viewButton;
    public ImageView shareButton;
    public ImageView writeExButton;
    public int positionOnList;

    public MeditationItemHolder(View itemView) {
        super(itemView);

        parolaDate = (TextView) itemView.findViewById(R.id.parola_date_text_view);
        parolaTitle = (TextView) itemView.findViewById(R.id.parola_title_text_view);
        meditation = (TextView) itemView.findViewById(R.id.meditation_text_view);

        viewButton = (ImageView) itemView.findViewById(R.id.view_meditation_image_view);
        shareButton = (ImageView) itemView.findViewById(R.id.share_meditation_image_view);
        writeExButton = (ImageView) itemView.findViewById(R.id.write_experience_image_view);

    }

}
