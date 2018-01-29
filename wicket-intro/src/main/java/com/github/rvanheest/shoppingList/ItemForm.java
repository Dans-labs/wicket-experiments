package com.github.rvanheest.shoppingList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import java.util.List;

public class ItemForm extends Panel {

  private final List<ShoppingListItem> shoppingList;
  private final Component showItems;

  public ItemForm(String id, final List<ShoppingListItem> shoppingList, final Component showItems) {
    super(id);
    this.setOutputMarkupId(true);
    this.shoppingList = shoppingList;
    this.showItems = showItems;

    Form<ShoppingListItem> form = new Form<>("form", new CompoundPropertyModel<>(new ShoppingListItem()));

    final TextField<String> tf = new TextField<>("text");
    AjaxButton button = new AjaxButton("submit-button", form) {

      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        submitForm((ShoppingListItem) this.getParent().getDefaultModelObject(), target);
      }
    };

    form.add(tf, button);
    this.add(form);
  }

  private void submitForm(ShoppingListItem formItem, AjaxRequestTarget target) {
    // copy the formItem into a new ShoppingListItem and add that to the shoppingList
    this.shoppingList.add(new ShoppingListItem(formItem));

    // reset the model that is in the form
    formItem.setText("");

    // repaint the form based on the resetting of the model
    target.add(ItemForm.this);

    // repaint the listview
    target.add(this.showItems);
  }
}
