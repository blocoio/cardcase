package io.bloco.cardcase.data.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

/**
 * Created by Mtrs on 28.09.2016.
 */

@DatabaseTable(tableName = "categories") public class Category {

  @DatabaseField(generatedId = true) private UUID id;
  @DatabaseField private String name;

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Category() {
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Category category = (Category) o;

    return id.equals(category.id);
  }

  @Override public int hashCode() {
    return id.hashCode();
  }
}
