package io.bloco.cardcase.common.di;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;

import dagger.Module;
import dagger.Provides;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.data.DatabaseHelper;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;

import java.sql.SQLException;
import java.util.UUID;

import javax.inject.Singleton;

@Module
public class ApplicationModule {
    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    public Resources provideResources(Context context) {
        return context.getResources();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public RuntimeExceptionDao<Card, UUID> provideCardDao() {
        DatabaseHelper databaseHelper =
                new DatabaseHelper(application.getApplicationContext(), application.getMode());
        ConnectionSource connectionSource = databaseHelper.getConnectionSource();
        try {
            return RuntimeExceptionDao.createDao(connectionSource, Card.class);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Provides
    @Singleton
    public RuntimeExceptionDao<Category, UUID> provideCategoryDao() {
        DatabaseHelper databaseHelper =
                new DatabaseHelper(application.getApplicationContext(), application.getMode());
        ConnectionSource connectionSource = databaseHelper.getConnectionSource();
        try {
            return RuntimeExceptionDao.createDao(connectionSource, Category.class);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
