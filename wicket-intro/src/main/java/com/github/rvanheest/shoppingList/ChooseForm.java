package com.github.rvanheest.shoppingList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// see also https://stackoverflow.com/questions/23353416/wicket-how-can-a-dropdownchoice-select-change-the-options-of-another-dropdownch
public class ChooseForm extends Panel {

  private static Map<String, List<ShoppingListItem>> choices = new HashMap<>();
  private static List<String> keys;

  static {
    // @formatter:off
    choices.put("Dairy", Arrays.asList(
        new ShoppingListItem("Milk"),
        new ShoppingListItem("Buttermilk"),
        new ShoppingListItem("Butter"),
        new ShoppingListItem("Yogurt")));
    choices.put("Meat", Arrays.asList(
        new ShoppingListItem("Chicken"),
        new ShoppingListItem("Beef"),
        new ShoppingListItem("Pork"),
        new ShoppingListItem("Turkey"),
        new ShoppingListItem("Kangaroo")));
    // @formatter:on

    keys = new ArrayList<>(choices.keySet());
  }

  private final List<ShoppingListItem> shoppingList;
  private final Component showItems;

  private final Model<String> groupModel = new Model<>();
  private final Model<ShoppingListItem> itemModel = new Model<>();

  public ChooseForm(String id, List<ShoppingListItem> shoppingList, Component showItems) {
    super(id);
    this.setOutputMarkupId(true);
    this.shoppingList = shoppingList;
    this.showItems = showItems;

    Form<ShoppingListItem> form = new Form<>("choose-form");

    final DropDownChoice<String> groups = new DropDownChoice<String>("groups", this.groupModel,
        keys) {{
      add(ChooseForm.this.groupsChangeBehavior());
    }};

    DropDownChoice<ShoppingListItem> items = new DropDownChoice<ShoppingListItem>("items",
        this.itemModel, this.itemsModel(groups), this.itemChoiceRenderer()) {

      @Override
      public boolean isEnabled() {
        return groups.getModelObject() != null;
      }
    };

    AjaxButton button = new AjaxButton("submit-button", form) {

      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        ChooseForm.this.submitForm(ChooseForm.this.itemModel.getObject(), target);
      }
    };

    form.add(groups, items, button);
    this.add(form);
  }

  private AjaxFormComponentUpdatingBehavior groupsChangeBehavior() {
    return new AjaxFormComponentUpdatingBehavior("change") {

      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        target.add(ChooseForm.this);
      }
    };
  }

  private LoadableDetachableModel<List<ShoppingListItem>> itemsModel(
      final DropDownChoice<String> dependingChoice) {
    return new LoadableDetachableModel<List<ShoppingListItem>>() {

      @Override
      protected List<ShoppingListItem> load() {
        String group = dependingChoice.getModelObject();
        List<ShoppingListItem> items = choices.get(group);

        return items == null ? Collections.<ShoppingListItem> emptyList() : items;
      }
    };
  }

  private ChoiceRenderer<ShoppingListItem> itemChoiceRenderer() {
    return new ChoiceRenderer<ShoppingListItem>() {

      @Override
      public Object getDisplayValue(ShoppingListItem object) {
        return object.getText();
      }
    };
  }

  private void submitForm(ShoppingListItem formItem, AjaxRequestTarget target) {
    // copy the formItem into a new ShoppingListItem and add that to the shoppingList
    this.shoppingList.add(new ShoppingListItem(formItem));

    // reset the form
    formItem.setText("");
    this.groupModel.setObject(null);
    this.itemModel.setObject(null);

    // repaint the form based on the previous resetting
    target.add(this);

    // repaint the listview
    target.add(this.showItems);
  }
}
