package com.udacity.jonathan.bookapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.jonathan.bookapp.R;
import com.udacity.jonathan.bookapp.data.BookContract.BookEntry;


public class BookCursorAdapter extends CursorAdapter {

    /** Tag for the log messages */
    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Get the current position of the cursor in order to set a TAG with it on the sell button
        final int position = cursor.getPosition();

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView sellBookButtonImageView = (ImageView) view.findViewById(R.id.sell_button);

        // Set a TAG on the sell button with current position of cursor
        sellBookButtonImageView.setTag(position);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        int bookQuantity = cursor.getInt(quantityColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(bookName);
        quantityTextView.setText(String.valueOf(bookQuantity));
        priceTextView.setText(bookPrice);


        sellBookButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the TAG of the view (sell ImageView) that was clicked on to arrive here
                // Define a position from this TAG
                Integer position = (Integer) v.getTag();
                // Move the cursor to this position
                cursor.moveToPosition(position);
                // Get the rowID  in the database of the product for which a sell is requested
                Long rowId = cursor.getLong(cursor.getColumnIndex(BookEntry._ID));
                // Find the column of product attributes that we're interested in : Quantity.
                int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
                // Get the current stock level (quantity) of the product for which a sell is requested
                int currentQuantity = cursor.getInt(quantityColumnIndex);

                // If the stock level is still positive, then proceed with the sell of 1 unit
                if(currentQuantity > 0) {
                    sellBookQuantity(context, rowId, currentQuantity);
                }
                else {
                    // Otherwise, show a toast message saying that the sell action is not possible
                    // as the stock level has reached 0.
                    Toast.makeText(context, context.getString(R.string.book_sell_stock_empty),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * helper to sell book quantity
     */
    private void sellBookQuantity(Context context, Long rowId, int quantity) {
        quantity--;

        Uri currentProductUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, rowId);

        final ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

        int rowsAffected = context.getContentResolver().update(currentProductUri, values, null, null);

        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(context, context.getString(R.string.book_sell_failed),
                    Toast.LENGTH_SHORT).show();

        } else {
            // Otherwise, the sell was successful and we can display a toast.
            Toast.makeText(context, context.getString(R.string.book_sell_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
