package io.bloco.cardcase.presentation.exchange;

import com.google.gson.Gson;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.common.FileHelper;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class CardSerializer {

  private final Gson gson;
  private final FileHelper fileHelper;

  @Inject public CardSerializer(Gson gson, FileHelper fileHelper) {
    this.gson = gson;
    this.fileHelper = fileHelper;
  }

  public byte[] serialize(Card card) {
    CardWrapper cardWrapper = newCardWrapper(card);
    return gson.toJson(cardWrapper).getBytes();
  }

  public Card deserialize(byte[] data) {
    String cardSerialized = new String(data);
    Timber.i("Card received: %s", cardSerialized);
    CardWrapper cardWrapper = gson.fromJson(cardSerialized, CardWrapper.class);
    return unwrapCard(cardWrapper);
  }

  public CardWrapper newCardWrapper(Card card) {
    CardWrapper cardWrapper = new CardWrapper();
    cardWrapper.card = card;
    if (card.getAvatarPath() != null) {
      cardWrapper.avatarData = fileHelper.getBytesFromFile(card.getAvatar());
    }
    return cardWrapper;
  }

  public Card unwrapCard(CardWrapper cardWrapper) {
    Card card = cardWrapper.card;
    if (card == null) {
      return null;
    }

    if (card.getAvatarPath() != null) {
      File avatarFile = fileHelper.createFinalImageFile();
      fileHelper.saveBytesToFile(cardWrapper.avatarData, avatarFile);
      card.setAvatar(avatarFile);
    }

    return card;
  }
}
