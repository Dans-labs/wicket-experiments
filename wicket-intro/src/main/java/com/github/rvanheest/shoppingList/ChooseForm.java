package com.github.rvanheest.shoppingList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// see also https://stackoverflow.com/questions/23353416/wicket-how-can-a-dropdownchoice-select-change-the-options-of-another-dropdownch
public class ChooseForm extends Panel {

  private static Map<String, List<String>> choices = new HashMap<>();
  private static List<String> keys;

  static {
    // @formatter:off
    choices.put("Dairy", Arrays.asList(
        "Milk",
        "Buttermilk",
        "Butter",
        "Yogurt"));
    choices.put("Meat", Arrays.asList(
        "Chicken",
        "Beef",
        "Pork",
        "Turkey",
        "Kangaroo"));
    // @formatter:on

    keys = new ArrayList<>(choices.keySet());
  }

  private final List<ShoppingListItem> shoppingList;
  private final Component showItems;

  private final Model<String> groupModel = Model.of();
  private final PropertyModel<String> itemModel;

  public ChooseForm(String id, List<ShoppingListItem> shoppingList, Component showItems) {
    super(id);
    this.setOutputMarkupId(true);
    this.shoppingList = shoppingList;
    this.showItems = showItems;

    ShoppingListItem formItem = new ShoppingListItem();
    Form<ShoppingListItem> form = new Form<>("choose-form", new CompoundPropertyModel<>(formItem));

    final DropDownChoice<String> groups = new DropDownChoice<String>("groups", this.groupModel, keys) {{
      add(ChooseForm.this.groupsChangeBehavior());
    }};

    this.itemModel = PropertyModel.of(formItem, "text");
    DropDownChoice<String> items = new DropDownChoice<String>("items", this.itemModel, this.itemsModel(groups)) {

      @Override
      public boolean isEnabled() {
        return groups.getModelObject() != null;
      }
    };

    AjaxButton button = new AjaxButton("submit-button", form) {

      @Override
      protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        ChooseForm.this.submitForm((ShoppingListItem) form.getDefaultModelObject(), target);
      }
    };

    form.add(groups, items, button);
    this.add(form);
  }

  private Behavior groupsChangeBehavior() {
    return new AjaxFormComponentUpdatingBehavior("change") {

      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        target.add(ChooseForm.this);
      }
    };
  }

  private IModel<List<String>> itemsModel(final FormComponent<String> dependingChoice) {
    return new LoadableDetachableModel<List<String>>() {

      @Override
      protected List<String> load() {
        return choices.getOrDefault(dependingChoice.getModelObject(), Collections.emptyList());
      }
    };
  }

  private void submitForm(ShoppingListItem formItem, AjaxRequestTarget target) {
    // copy the formItem into a new ShoppingListItem and add that to the shoppingList
    this.shoppingList.add(new ShoppingListItem(formItem));

    // reset the form
    formItem.reset();
    this.resetForm();

    // repaint the form based on the previous resetting
    target.add(this);

    // repaint the listview
    target.add(this.showItems);
  }

  private void resetForm() {
    this.groupModel.setObject(null);
    this.itemModel.setObject(null);
  }
}
