package io.bloco.cardcase.domain;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SaveCategories {

    private final Database database;

    public interface Callback {
        void onSavedCategories(List<Category> savedCategories);
    }

    @Inject public SaveCategories(Database database) {
        this.database = database;
    }

    public void save(List<Category> categories, Callback callback) {
        database.saveCategories(categories);
        callback.onSavedCategories(categories);
    }
}
