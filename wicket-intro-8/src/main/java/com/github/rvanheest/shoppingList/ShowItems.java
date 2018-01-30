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

    ListView<ShoppingListItem> shoppingListUI = new ListView<ShoppingListItem>("shoppinglist-items",
        shoppingList) {

      @Override
      protected void populateItem(final ListItem<ShoppingListItem> item) {
        final Label text = new Label("text", PropertyModel.of(item.getDefaultModel(), "text")) {{
          add(AjaxEventBehavior.onEvent("dblclick",
              target -> removeShoppingListItem((ShoppingListItem) item.getDefaultModelObject(),
                  target)));
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

  private void removeShoppingListItem(ShoppingListItem shoppingItem, AjaxRequestTarget target) {
    // remove it from the model
    this.shoppingList.remove(shoppingItem);

    // repaint the panel
    target.add(this);
  }
}
