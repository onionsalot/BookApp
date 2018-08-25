package com.example.superonion.bookapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.ContentValues.TAG;

public class BookAdapter extends ArrayAdapter<Books> {


    public BookAdapter(@NonNull Context context, int resource, @NonNull List<Books> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_items, parent, false);
        }
        final Books w = getItem(position);
        // Casts the LinearLayout. This is to check if the position of the item created is odd or even.
        // This is how you cast different colors to the layout programmably
        LinearLayout listItemLayout = (LinearLayout) listItemView.findViewById(R.id.single_book_layout);
        if ((position % 2) == 0) {
            // number is even
            listItemLayout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            listItemLayout.setBackgroundColor(typedValue.data);
        }


        // Create and setText for the "title" field
        TextView itemName = (TextView) listItemView.findViewById(R.id.item_name);
        itemName.setText(w.getTitle());

        // Create and setText for the "subtitle" field. If subtitle is empty, delete the view to save data
        TextView itemSubtitle = (TextView) listItemView.findViewById(R.id.item_subtitle);
        if (w.getSubtitle().equals("")) {
            itemSubtitle.setVisibility(View.GONE);
        } else {
            itemSubtitle.setText(w.getSubtitle());
        }

        // Create and setText for the "description" field.
        TextView itemDescription = (TextView) listItemView.findViewById(R.id.item_description);
        itemDescription.setText(w.getDescription());

        // Create and setText for the "published date" field.
        TextView itemDate = (TextView) listItemView.findViewById(R.id.item_date);
        itemDate.setText(w.getPublishedDate());

        // Create and setText for the "authors" field.
        TextView itemAuthors = (TextView) listItemView.findViewById(R.id.item_authors);
        itemAuthors.setText(w.getAuthors());

        // Create and setText for the "pictures"
        ImageView itemPicture = (ImageView) listItemView.findViewById(R.id.item_image);
        /*
         Picasso framework is used to get images easily
         Picasso is unable to accept http responses so we convert
         the http: responses to https: responses.
         Some responses may vary. Picasso is loaded in via importing from
         the build.gradle.

         Callback() method used as a listener part of the new Picasso framework.
         Can be used to check for errors.

         If the callback returns an error, then to catch the error we pass in
         a temp clipart to not make the field empty.
          */
        if (w.getPicture().equals("")) {
            itemPicture.setImageResource(R.drawable.placeholderbook);
        } else {
            Picasso.get()
                    .load(w.getPicture().replace("http:", "https:"))
                    .into(itemPicture, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("error", "onError: ERRROROORORR" + e);
                        }
                    });
        }
        return listItemView;
    }

}
