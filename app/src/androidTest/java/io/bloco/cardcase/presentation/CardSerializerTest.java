package io.bloco.cardcase.presentation;

import android.test.ApplicationTestCase;
import com.google.gson.Gson;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.common.FileHelper;
import io.bloco.cardcase.presentation.exchange.CardSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CardSerializerTest extends ApplicationTestCase<AndroidApplication> {

  private static final long FILE_LENGTH = 1000;

  private CardSerializer cardSerializer;
  private File avatarFile;

  public CardSerializerTest() {
    super(AndroidApplication.class);
  }

  @Override public void setUp() throws Exception {
    cardSerializer = new CardSerializer(new Gson(), new FileHelper(getContext()));

    avatarFile = File.createTempFile("temp", ".jpg", getContext().getCacheDir());
    FileOutputStream fos = new FileOutputStream(avatarFile);
    for (int i = 0; i < FILE_LENGTH; i++) {
      fos.write(new Random().nextInt());
    }
    fos.close();
  }

  public void test() throws Exception {
    Card card = new Card();
    card.setId("card-id");
    card.setAvatar(avatarFile);

    byte[] data = cardSerializer.serialize(card);
    avatarFile.delete();
    Card outputCard = cardSerializer.deserialize(data);

    assertThat(outputCard.getId(), is(equalTo(card.getId())));
    assertTrue(outputCard.getAvatar().exists());
    assertThat(outputCard.getAvatar().length(), is(equalTo(FILE_LENGTH)));
  }

  @Override public void tearDown() throws Exception {
    if (avatarFile != null && avatarFile.exists()) {
      avatarFile.delete();
      avatarFile = null;
    }
  }
}
