package com.github.rvanheest.shoppingList;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

public class ShowItems extends Panel {

  private final List<ShoppingListItem> shoppingList;

  public ShowItems(String id, final List<ShoppingListItem> shoppingList) {
    super(id);
    this.setOutputMarkupId(true);
    this.shoppingList = shoppingList;

    ListView<ShoppingListItem> shoppingListUI = new ListView<ShoppingListItem>("shoppinglist-items", shoppingList) {

      @Override
      protected void populateItem(final ListItem<ShoppingListItem> item) {
        final Label text = new Label("text", new PropertyModel<String>(item.getDefaultModel(), "text")) {{
          add(ShowItems.this.removeShoppingListItemBehavior(item));
        }};
        item.add(text);
      }

      @Override
      public boolean isVisible() {
        return !ShowItems.this.shoppingList.isEmpty();
      }
    };

    Label emptyLabel = new Label("no-shoppinglist-items", "<No items in the list>") {
      @Override
      public boolean isVisible() {
        return ShowItems.this.shoppingList.isEmpty();
      }
    };

    this.add(shoppingListUI, emptyLabel);
  }

  private AjaxEventBehavior removeShoppingListItemBehavior(final ListItem<ShoppingListItem> item) {
    return new AjaxEventBehavior("dblclick") {

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        removeShoppingListItem(item, target);
      }
    };
  }

  private void removeShoppingListItem(final ListItem<ShoppingListItem> item, AjaxRequestTarget target) {
    // retrieve the clicked ShoppingListItem
    ShoppingListItem shoppingItem = (ShoppingListItem) item.getDefaultModelObject();

    // remove it from the model
    this.shoppingList.remove(shoppingItem);

    // repaint the panel
    target.add(this);
  }
}
