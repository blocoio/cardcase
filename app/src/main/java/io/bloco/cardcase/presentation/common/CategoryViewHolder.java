package io.bloco.cardcase.presentation.common;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;

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

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    @Bind(R.id.category_name)
    TextView name;

    @Bind(R.id.name_text_edit)
    EditText nameEditText;

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
    }

    public void bind(Category category) {
        this.category = category;
        name.setText(category.getName());
    }

    @Override
    public void onClick(View view) {
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
        Log.d("TEST", "Long click ");

        return false;
    }
}
