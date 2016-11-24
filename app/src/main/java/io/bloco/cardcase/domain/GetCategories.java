package io.bloco.cardcase.domain;

import java.util.List;

import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Category;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class GetCategories {

  private final Database database;

  public interface Callback {
    void onGetCategories(List<Category> categories);
  }

  @Inject public GetCategories(Database database) {
    this.database = database;
  }

  public void get(Callback callback) {
    List<Category> categories = this.database.getCategories();
    callback.onGetCategories(categories);
  }
}
