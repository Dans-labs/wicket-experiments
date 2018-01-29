package com.github.rvanheest.shoppingList;

import java.io.Serializable;
import java.util.Objects;

public class ShoppingListItem implements Serializable {

  private String text;

  public ShoppingListItem() {}

  public ShoppingListItem(String text) {
    this.text = text;
  }

  public ShoppingListItem(ShoppingListItem li) {
    this.text = li.text;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof ShoppingListItem) {
      ShoppingListItem that = (ShoppingListItem) other;
      return Objects.equals(this.text, that.text);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.text);
  }

  @Override
  public String toString() {
    return String.format("<ShoppingListItem[%s]>", this.text);
  }
}
