package io.bloco.cardcase.presentation.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.home.CardDetailDialog;

@SuppressWarnings("unused")
public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  @Inject
  DateTimeFormat dateTimeFormat;
  @Inject
  ImageLoader imageLoader;

  @BindView(R.id.card_avatar)
  ImageView avatar;
  @BindView(R.id.card_name)
  TextView name;
  @BindView(R.id.card_time)
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
