package io.bloco.cardcase.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.data.models.Card;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

  private static final String DATABASE_NAME = "database";
  private static final String TEST_DATABASE_NAME = "database_test";
  private static final int DATABASE_VERSION = 2;

  private Class[] mTables = new Class[] { Card.class };

  @Inject public DatabaseHelper(Context context, AndroidApplication.Mode mode) {
    super(context, getDbName(mode), null, DATABASE_VERSION);
  }

  private static String getDbName(AndroidApplication.Mode mode) {
    return mode == AndroidApplication.Mode.NORMAL ? DATABASE_NAME : TEST_DATABASE_NAME;
  }

  @Override public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
    try {
      for (Class tableClass : mTables) {
        TableUtils.createTableIfNotExists(connectionSource, tableClass);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
      int newVersion) {
    RuntimeExceptionDao<Card, String> cardsDao = getCardsDao(connectionSource);

    if (oldVersion < 2) {
      cardsDao.executeRaw("ALTER TABLE `cards` ADD COLUMN email VARCHAR;");
      cardsDao.executeRaw("ALTER TABLE `cards` ADD COLUMN phone VARCHAR;");
    }
  }

  public void clear() {
    try {
      for (Class tableClass : mTables) {
        TableUtils.clearTable(getConnectionSource(), tableClass);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void dropTables() throws SQLException {
    for (Class tableClass : mTables) {
      TableUtils.dropTable(connectionSource, tableClass, true);
    }
  }

  private RuntimeExceptionDao<Card, String> getCardsDao(ConnectionSource connectionSource) {
    try {
      return RuntimeExceptionDao.createDao(connectionSource, Card.class);
    } catch (SQLException exception) {
      throw new RuntimeException(exception);
    }
  }
}
