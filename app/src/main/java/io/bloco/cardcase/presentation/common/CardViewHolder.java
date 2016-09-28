package io.bloco.cardcase.presentation.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.home.CardDetailDialog;

import javax.inject.Inject;

public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Inject
    DateTimeFormat dateTimeFormat;
    @Inject
    ImageLoader imageLoader;

    @Bind(R.id.card_avatar)
    ImageView avatar;
    @Bind(R.id.card_name)
    EditText name;
    @Bind(R.id.card_company)
    EditText company;
    @Bind(R.id.card_address)
    EditText address;
    @Bind(R.id.card_website)
    EditText website;
    @Bind(R.id.card_position)
    EditText position;
    @Bind(R.id.card_email)
    EditText email;
    @Bind(R.id.card_phone)
    EditText phone;
    @Bind(R.id.card_fields)
    ViewGroup fields;
    @Bind(R.id.card_time)
    TextView time;

    private final CardDetailDialog cardDetailDialog;
    private Card card;

    public CardViewHolder(View view, CardDetailDialog cardDetailDialog) {
        super(view);
        view.setOnClickListener(this);
        this.cardDetailDialog = cardDetailDialog;

        ((AndroidApplication) view.getContext().getApplicationContext()).getApplicationComponent()
                .inject(this);
        ButterKnife.bind(this, view);
    }

    public void bind(Card card) {
        this.card = card;
        name.setText(card.getName());
        company.setText(card.getCompany());
        address.setText(card.getAddress());
        website.setText(card.getWebsite());
        position.setText(card.getPosition());
        email.setText(card.getEmail());
        phone.setText(card.getPhone());

        CharSequence timeStr = dateTimeFormat.getRelativeTimeSpanString(card.getUpdatedAt());
        time.setText(timeStr);

        if (card.hasAvatar()) {
            imageLoader.loadAvatar(avatar, card.getAvatarPath());
        }
    }

    @Override
    public void onClick(View view) {
        cardDetailDialog.show(card);
    }
}
