package io.bloco.cardcase.presentation.common;

import android.content.Context;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.faker.Faker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

public class Bootstrap {

    private static final int NUM_CARDS = 10;
    private static final int NUM_AVATARS = 10;

    private final Context context;
    private final Database database;
    private final FileHelper fileHelper;
    private final Faker faker;
    private int avatarIndex = 0;
    private Category category;

    @Inject
    public Bootstrap(Context context, Database database, FileHelper fileHelper) {
        this.context = context;
        this.database = database;
        this.fileHelper = fileHelper;
        this.faker = new Faker();
    }

    public void clearAndBootstrap() {
        database.clear();
        this.category = buildFakeCategory();
        bootstrap();
    }

    private void bootstrap() {
        Card userCard = buildFakeCard();
        userCard.setIsUser(true);
        database.saveCard(userCard);

        for (int i = 0; i < NUM_CARDS; i++) {
            Card card = buildFakeCard();
        }
    }

    private Category buildFakeCategory() {
        Category category = new Category();
        category.setName("Unsorted");
        database.saveCategory(category);
        return category;
    }


    private Card buildFakeCard() {
        String avatarFilePath = "avatars/avatar" + (avatarIndex + 1) + ".jpg";
        String avatarPath = fileFromAssetPath(avatarFilePath).getAbsolutePath();
        avatarIndex = (avatarIndex + 1) % NUM_AVATARS;

        Card card = new Card();
        card.setName(faker.name.name());
        card.setEmail(faker.internet.safeEmail(card.getName().split(" ")[0]));
        card.setPhone(faker.phoneNumber.cellPhone());
        card.setFacebookLink("LianchikGareeva");
        card.setVklink("xolodilnichka");
        card.setCompany(faker.company.name());
        card.setAddress(faker.address.city().toString());
        card.setWebsite(faker.internet.email());
        card.setPosition(faker.company.profession());
        card.setCreatedAt(faker.time.backward(365));
        card.setUpdatedAt(card.getCreatedAt());
        card.setAvatarPath(avatarPath);
        card.setLinkedinURL("alexey-merzlikin");
        card.setInstagramURL("pilotmaria");
        card.setCategoryId(category.getId());


        database.saveCard(card);
        return card;
    }

    private File fileFromAssetPath(String assetPath) {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(assetPath);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        File file = fileHelper.createFinalImageFile();
        copyInputStreamToFile(inputStream, file);
        return file;
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
