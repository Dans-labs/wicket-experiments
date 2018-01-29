package com.github.rvanheest.shoppingList;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShoppingList extends WebPage {

  private final List<ShoppingListItem> shoppingList;

  private final ShowItems showItems;

  public ShoppingList() {
    this.shoppingList = new ArrayList<>();
    this.shoppingList.addAll(Arrays.asList(new ShoppingListItem("sugar"), new ShoppingListItem("tea")));

    Label header = new Label("shoppinglist-header", "Shopping list");
    this.showItems = new ShowItems("showItems", shoppingList);
    ItemForm form = new ItemForm("form", shoppingList, showItems);
    Button clearButton = new Button("clear-button") {{
      add(ShoppingList.this.clearBehavior());
    }};

    this.add(header, clearButton, this.showItems, form);
  }

  private AjaxEventBehavior clearBehavior() {
    return new AjaxEventBehavior("click") {

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        clearItems(target);
      }
    };
  }

  private void clearItems(AjaxRequestTarget target) {
    this.shoppingList.clear();
    target.add(this.showItems);
  }
}