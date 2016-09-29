package io.bloco.cardcase.presentation.common;

import android.content.Context;
import android.util.Log;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.faker.Faker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

public class Bootstrap {

    private static final int NUM_CARDS = 3;
    private static final int NUM_AVATARS = 10;

    private final Context context;
    private final Database database;
    private final FileHelper fileHelper;
    private final Faker faker;
    private int avatarIndex = 0;

    @Inject
    public Bootstrap(Context context, Database database, FileHelper fileHelper) {
        this.context = context;
        this.database = database;
        this.fileHelper = fileHelper;
        this.faker = new Faker();
    }

    public void clearAndBootstrap() {
        database.clear();
        bootstrap();
    }

    private void bootstrap() {
        Card userCard = buildFakeCard();
        userCard.setIsUser(true);
        database.saveCard(userCard);

        for (int i = 0; i < NUM_CARDS; i++) {
            Card card = buildFakeCard();
            Log.d("CREATE", "id:" + card.getId());
        }
    }

    private Category buildFakeCategory() {
        Category category = new Category();

        Random random = new Random();
        category.setName("test category " + random.nextInt(1));
        database.saveCategory(category);
        Log.d("CREATE", "SAVED, categoryId: " + category.getId() + " " + category.getName());
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

        card.setCompany(faker.company.name());
        card.setAddress(faker.address.city().toString());
        card.setWebsite(faker.internet.email());
        card.setPosition(faker.company.profession());
        card.setCreatedAt(faker.time.backward(365));
        card.setUpdatedAt(card.getCreatedAt());
        card.setAvatarPath(avatarPath);

        UUID categoryId = buildFakeCategory().getId();
        card.setCategoryId(categoryId);
        database.saveCard(card);
        Log.d("CREATE", "SAVED, id:" + card.getId());
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
