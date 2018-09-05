package com.udacity.jonathan.bookapp.data;

import android.provider.BaseColumns;

public class BookContract {


    public static final class BookEntry implements BaseColumns {
       // Name of the main table
        public final static String TABLE_NAME = "books";

        // Database properties
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME ="name";
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_SUPPLIER_PHONE = "phone";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_QUANTITY = "quantity";


    }

}
