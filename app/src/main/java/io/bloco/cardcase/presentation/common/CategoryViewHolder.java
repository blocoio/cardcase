package io.bloco.cardcase.presentation.common;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;

import butterknife.OnTextChanged;
import io.bloco.cardcase.presentation.home.HomeActivity;
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
import io.bloco.cardcase.presentation.home.HomeContract;
import io.bloco.cardcase.presentation.home.HomePresenter;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    @Bind(R.id.name_text_edit)
    MyEditText nameEditText;

    private HomeContract.View homeContract;
    private Database database;

    private Category category;

    @Inject
    public CategoryViewHolder(View view, HomeContract.View homeContract, Database database) {
        super(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        ((AndroidApplication) view.getContext().getApplicationContext()).getApplicationComponent()
                .inject(this);

        this.homeContract = homeContract;
        this.database = database;
        ButterKnife.bind(this, view);

        nameEditText.setInputType(InputType.TYPE_NULL);
        nameEditText.setBackgroundColor(Color.TRANSPARENT);
        nameEditText.setOnLongClickListener(this);
        nameEditText.setOnClickListener(this);
    }

    public void bind(Category category) {
        this.category = category;
        nameEditText.setText(category.getName());
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

    @Override
    public boolean onLongClick(View v) {
        nameEditText.setText(category.getName());
        nameEditText.activate();
        Log.d("test", category.getName());
        return true;
    }

    @OnTextChanged(R.id.name_text_edit)
    public void afterTextChanged (CharSequence text) {
        if (text == null || text.length() == 0) { return; }
        category.setName(text.toString());
        database.saveCategory(category);
    }

}
