package com.github.rvanheest.shoppingList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.lambda.components.ComponentFactory;

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

  private final IModel<String> groupModel = Model.of();
  private final IModel<String> itemModel;

  public ChooseForm(String id, List<ShoppingListItem> shoppingList, Component showItems) {
    super(id);
    this.setOutputMarkupId(true);
    this.shoppingList = shoppingList;
    this.showItems = showItems;

    ShoppingListItem formItem = new ShoppingListItem();
    Form<ShoppingListItem> form = new Form<>("choose-form", CompoundPropertyModel.of(formItem));

    DropDownChoice<String> groups = new DropDownChoice<String>("groups", this.groupModel, keys) {{
      add(AjaxFormComponentUpdatingBehavior
          .onUpdate("change", target -> target.add(ChooseForm.this)));
    }};

    this.itemModel = Model.of(formItem).flatMap(item -> LambdaModel.of(item::getText, item::setText));
    DropDownChoice<String> items = new DropDownChoice<String>("items", this.itemModel,
        this.itemsModel(groups)) {

      @Override
      public boolean isEnabled() {
        return groups.getModelObject() != null;
      }
    };

    AjaxButton button = ComponentFactory.ajaxButton("submit-button", (btn, target) -> this
        .submitForm((ShoppingListItem) btn.getForm().getDefaultModelObject(), target));

    form.add(groups, items, button);
    this.add(form);
  }

  private IModel<List<String>> itemsModel(FormComponent<String> dependingChoice) {
    return LoadableDetachableModel
        .of(() -> choices.getOrDefault(dependingChoice.getModelObject(), Collections.emptyList()));
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
