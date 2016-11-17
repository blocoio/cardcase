package io.bloco.cardcase.presentation.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import butterknife.OnTextChanged;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.R;
import io.bloco.cardcase.data.Database;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.cardcase.presentation.home.HomeActivity;
import io.bloco.cardcase.presentation.home.HomeContract;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Bind(R.id.name_text_edit)
    MyEditText nameEditText;

    private final HomeContract.View homeContract;
    private final Database database;

    private Category category;

    @Inject
    public CategoryViewHolder(View view, HomeContract.View homeContract, Database database) {
        super(view);
//        nameEditText.setOnClickListener(this);
        view.setOnClickListener(this);
        ((AndroidApplication) view.getContext().getApplicationContext()).getApplicationComponent()
                .inject(this);

        this.homeContract = homeContract;
        this.database = database;
        ButterKnife.bind(this, view);

        nameEditText.setInputType(InputType.TYPE_NULL);
        nameEditText.setBackgroundColor(Color.TRANSPARENT);
        nameEditText.setOnClickListener(this);

        view.setOnLongClickListener(longClickListener);
        view.setOnCreateContextMenuListener(onCreateContextListener);

    }

    public void bind(Category category) {
        this.category = category;
        nameEditText.setText(category.getName());
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public void onClick(View view) {
        if (nameEditText.getInputType() == InputType.TYPE_CLASS_TEXT) { return; }

        List<Card> cards = database.getReceivedCards();
        List<Card> cardsByCategory = new ArrayList<>();
        for (Card card : cards) {
            if (card.getCategoryId().equals(category.getId())) {
                cardsByCategory.add(card);
            }
        }
        homeContract.hideCategories();
        homeContract.showCards(cardsByCategory);
    }

    @OnTextChanged(R.id.name_text_edit)
    public void afterTextChanged (CharSequence text) {
        if (text == null || text.length() == 0) { return; }
        category.setName(text.toString());
        database.saveCategory(category);
    }

    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        public boolean onLongClick(View view) {
            view.showContextMenu();
            return false;
        }
    };

    private View.OnCreateContextMenuListener onCreateContextListener = new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View view,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, 0, 0, "Edit").setOnMenuItemClickListener(mMenuItemClickListener);
            menu.add(0, 1, 0, "Delete").setOnMenuItemClickListener(mMenuItemClickListener);
        }

    };

    private MenuItem.OnMenuItemClickListener mMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 0:
                    homeContract.showDoneButton();

                    nameEditText.setText(category.getName());
                    nameEditText.activate();

                    return false;

                case 1:
                    AlertDialog alert = getDialog().create();
                    alert.show();

                    return false;
            }

            return false;
        }
    };

    private AlertDialog.Builder getDialog() {
        TextView title = new TextView((Activity) homeContract);
        title.setText("Remove the category");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(23);

        TextView msg = new TextView((Activity) homeContract);
        msg.setText("Are you sure ? All the cards would be deleted as well.");
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);

        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) homeContract);
        builder.setCustomTitle(title);
        builder.setView(msg);

        builder.setIcon(R.drawable.ic_delete_forever_white_24px);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                deleteCategory(category);

                if (homeContract != null) {
                    //// FIXME: 17.11.16 refresh categories somehow.
                    Intent intent = HomeActivity.Factory.getIntent(((Activity) homeContract).getApplicationContext());
                    ((Activity) homeContract).startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    private void deleteCategory(Category category) {
        database.deleteCards(database.getCardsByCategory(category));
        database.deleteCategory(category);
    }
}
