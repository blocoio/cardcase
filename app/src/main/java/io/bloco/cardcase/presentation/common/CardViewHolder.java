package io.bloco.cardcase.presentation.common;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.home.CardDetailDialog;

import javax.inject.Inject;

public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Inject
    DateTimeFormat dateTimeFormat;
    @Inject
    ImageLoader imageLoader;

    Database database;

    @Bind(R.id.card_avatar)
    ImageView avatar;
    @Bind(R.id.card_name)
    TextView name;
    @Bind(R.id.card_time)
    TextView time;
    @Bind(R.id.card_category)
    TextView cardCategory;

    private final CardDetailDialog cardDetailDialog;
    private Card card;

    public CardViewHolder(View view, CardDetailDialog cardDetailDialog, Database database) {
        super(view);
        view.setOnClickListener(this);
        this.cardDetailDialog = cardDetailDialog;
        this.database = database;

        ((AndroidApplication) view.getContext().getApplicationContext()).getApplicationComponent()
                .inject(this);
        ButterKnife.bind(this, view);
    }

    public void bind(Card card) {
        this.card = card;
        name.setText(card.getName());

        CharSequence timeStr = dateTimeFormat.getRelativeTimeSpanString(card.getUpdatedAt());
        time.setText(timeStr);

        cardCategory.setText(database.getCategory(card.getCategoryId()).getName());

        if (card.hasAvatar()) {
            imageLoader.loadAvatar(avatar, card.getAvatarPath());
        }
    }

    @Override
    public void onClick(View view) {
        cardDetailDialog.show(card);
    }
}
