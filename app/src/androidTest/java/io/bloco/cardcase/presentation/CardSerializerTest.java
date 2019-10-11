package io.bloco.cardcase.presentation;


import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.presentation.common.FileHelper;
import io.bloco.cardcase.presentation.exchange.CardSerializer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CardSerializerTest {

  private static final long FILE_LENGTH = 1000;

  private CardSerializer cardSerializer;
  private File avatarFile;

  @Before
  public void setUp() throws Exception {
    cardSerializer = new CardSerializer(new Gson(), new FileHelper(getContext()));

    avatarFile = File.createTempFile("temp", ".jpg", getContext().getCacheDir());
    FileOutputStream fos = new FileOutputStream(avatarFile);
    for (int i = 0; i < FILE_LENGTH; i++) {
      fos.write(new Random().nextInt());
    }
    fos.close();
  }

  @Test
  public void test() {
    Card card = new Card();
    card.setId("card-id");
    card.setAvatar(avatarFile);

    byte[] data = cardSerializer.serialize(card);
    //noinspection ResultOfMethodCallIgnored
    avatarFile.delete();
    Card outputCard = cardSerializer.deserialize(data);

    assertThat(outputCard.getId(), is(equalTo(card.getId())));
    assertTrue(outputCard.getAvatar().exists());
    assertThat(outputCard.getAvatar().length(), is(equalTo(FILE_LENGTH)));
  }

  @After
  public void tearDown() {
    if (avatarFile != null && avatarFile.exists()) {
      //noinspection ResultOfMethodCallIgnored
      avatarFile.delete();
      avatarFile = null;
    }
  }

  private Context getContext() {
    return InstrumentationRegistry.getInstrumentation().getTargetContext();
  }
}
