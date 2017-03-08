package by.fk.belpoetry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String KEY_AUTHOR_ID = "author_id";
    public static final String KEY_AUTHOR_FULL_NAME = "author_full_name";
    public static final String KEY_AUTHOR_SHORT_NAME = "author_short_name";

    public static final String KEY_POEM_TITLE_ID = "poem_title_id";
    public static final String KEY_POEM_TITLE = "poem_title";

    public static final String KEY_POEM_TEXT_ID = "poem_text_id";
    public static final String KEY_POEM_TEXT = "poem_text";

    public static final String KEY_SINGLE_POEM_ID = "single_poem_id";
    public static final String KEY_SINGLE_POEM_AUTHOR = "single_poem_author";
    public static final String KEY_SINGLE_POEM_TITLE = "single_poem_title";
    public static final String KEY_SINGLE_POEM_TEXT_ID = "single_poem_text_id";

    public static final String KEY_FAVOURITE_ID = "favourite_id";
    public static final String KEY_FAVOURITE_AUTHOR_ID = "favourite_author_id";
    public static final String KEY_FAVOURITE_TITLE_ID = "favourite_title_id";
    public static final String KEY_FAVOURITE_TEXT_ID = "favourite_text_id";

    public static final String DATABASE_NAME = "poet_database.db";
    public static final String DATABASE_TABLE_AUTHORS = "Authors";
    public static final String DATABASE_TABLE_POEM_TITLES = "Poem_titles";
    public static final String DATABASE_TABLE_POEM_TEXTS = "Poem_texts";
    public static final String DATABASE_TABLE_SINGLE_POEMS = "Single_poems";
    public static final String DATABASE_TABLE_FAVOURITE = "Favourite";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_AUTHORS = "create table "
            + DATABASE_TABLE_AUTHORS + " ("
            + KEY_AUTHOR_ID + " integer primary key autoincrement, "
            + KEY_AUTHOR_FULL_NAME + " text not null, "
            + KEY_AUTHOR_SHORT_NAME + " text not null);";

    private static final String DATABASE_CREATE_POEM_TITLES = "create table "
            + DATABASE_TABLE_POEM_TITLES + " ("
            + KEY_POEM_TITLE_ID + " integer primary key autoincrement, "
            + KEY_POEM_TITLE + " text not null, "
            + KEY_POEM_TEXT_ID + " integer,"
            + KEY_AUTHOR_ID + " integer);";

    private static final String DATABASE_CREATE_POEM_TEXTS = "create table "
            + DATABASE_TABLE_POEM_TEXTS + " ("
            + KEY_POEM_TEXT_ID + " integer primary key autoincrement, "
            + KEY_POEM_TEXT + " text not null);";

    private static final String DATABASE_CREATE_SINGLE_POEMS = "create table "
            + DATABASE_TABLE_SINGLE_POEMS + " ("
            + KEY_SINGLE_POEM_ID + " integer primary key autoincrement, "
            + KEY_SINGLE_POEM_AUTHOR + " text not null,"
            + KEY_SINGLE_POEM_TITLE + " text not null,"
            + KEY_SINGLE_POEM_TEXT_ID + " integer);";

    private static final String DATABASE_CREATE_FAVOURITE = "create table "
            + DATABASE_TABLE_FAVOURITE + " ("
            + KEY_FAVOURITE_ID + " integer primary key autoincrement, "
            + KEY_FAVOURITE_AUTHOR_ID + " integer, "
            + KEY_FAVOURITE_TITLE_ID + " integer, "
            + KEY_FAVOURITE_TEXT_ID + " integer);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_AUTHORS);
        db.execSQL(DATABASE_CREATE_POEM_TEXTS);
        db.execSQL(DATABASE_CREATE_POEM_TITLES);
        db.execSQL(DATABASE_CREATE_SINGLE_POEMS);
        db.execSQL(DATABASE_CREATE_FAVOURITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("TaskDBAdapter", "Upgrading from version "
                + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        // database upgrading (transfer data to new table)
        // simple case:
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_AUTHORS);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_POEM_TEXTS);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_POEM_TITLES);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SINGLE_POEMS);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FAVOURITE);
        onCreate(db);
    }

    public void upgradeTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        switch (tableName) {
            case DATABASE_TABLE_AUTHORS:
                db.execSQL(DATABASE_CREATE_AUTHORS);
                break;
            case DATABASE_TABLE_FAVOURITE:
                db.execSQL(DATABASE_CREATE_FAVOURITE);
                break;
            case DATABASE_TABLE_POEM_TEXTS:
                db.execSQL(DATABASE_CREATE_POEM_TEXTS);
                break;
            case DATABASE_TABLE_POEM_TITLES:
                db.execSQL(DATABASE_CREATE_POEM_TITLES);
                break;
            case DATABASE_TABLE_SINGLE_POEMS:
                db.execSQL(DATABASE_CREATE_SINGLE_POEMS);
                break;
        }
    }

    public String getAuthorShortName(String authorFullName) {
        String[] titlesResultColumns = {
                KEY_AUTHOR_FULL_NAME,
                KEY_AUTHOR_SHORT_NAME
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_AUTHORS, titlesResultColumns, null, null, null, null, null);
        int authorFullNameIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_FULL_NAME);
        int authorShortNameIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_SHORT_NAME);
        String authorShortName = null;

        while (cursor.moveToNext()) {
            if (cursor.getString(authorFullNameIndex).equalsIgnoreCase(authorFullName)) {
                authorShortName = cursor.getString(authorShortNameIndex);
                break;
            }
        }

        cursor.close();
        return authorShortName;
    }

    public String[] getPoemTitlesByAuthor(String authorFullName) {
        if (authorFullName.equals(Keys.KEY_SINGLE_POEM_AUTHOR)) {
            return getSinglePoemTitles();
        }
        if (authorFullName.equals(Keys.KEY_FAVOURITE_POEM_AUTHOR)) {
            return getFavouritePoemTitles();
        }

        int authorId = getAutorId(authorFullName);
        if (authorId == -1) {
            return null;
        }

        String[] resultColumns = {
                KEY_POEM_TITLE,
                KEY_AUTHOR_ID
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_POEM_TITLES, resultColumns, null, null, null, null, null);
        int poemTitleIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TITLE);
        int authorIdInTitlesIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_ID);
        List<String> titles = new ArrayList<>();

        while (cursor.moveToNext()) {
            if (cursor.getInt(authorIdInTitlesIndex) == authorId) {
                titles.add(cursor.getString(poemTitleIndex));
            }
        }

        cursor.close();
        return toStringArray(titles.toArray());
    }

    public String getText(String title, String author) {
        int authorId = getAutorId(author);
        if (authorId == -1) {
            return getTextFromSingle(title, author);
        }

        String[] resultColumns = {
                KEY_POEM_TITLE,
                KEY_POEM_TEXT_ID,
                KEY_AUTHOR_ID
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_POEM_TITLES, resultColumns, null, null, null, null, null);
        int poemTitleIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TITLE);
        int poemTextIdIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TEXT_ID);
        int authorIdInTitlesIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_ID);
        String text = null;

        while (cursor.moveToNext()) {
            String poemTitle = cursor.getString(poemTitleIndex);
            int authorIdInTitles = cursor.getInt(authorIdInTitlesIndex);
            if (title.equalsIgnoreCase(poemTitle) && authorIdInTitles == authorId) {
                text = getText(cursor.getLong(poemTextIdIndex));
                break;
            }
        }

        cursor.close();
        return text;
    }

    public boolean isFavourite(String title, String author) {
        int authorId = author.equals(Keys.KEY_SINGLE_POEM_AUTHOR) ? Keys.KEY_SINGLE_POEM_AUTHOR_ID : getAutorId(author);
        if (authorId == -1) {
            long singlePoemId = getSinglePoemId(title, author);
            if (singlePoemId == -1) {
                return false;
            }

            String[] resultColumns = {
                    KEY_FAVOURITE_TITLE_ID,
                    KEY_FAVOURITE_AUTHOR_ID
            };
            Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_FAVOURITE, resultColumns, null, null, null, null, null);
            int favouriteTitleIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_TITLE_ID);
            int favouriteAuthorIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_AUTHOR_ID);

            while (cursor.moveToNext()) {
                if (cursor.getLong(favouriteTitleIndex) == singlePoemId && cursor.getLong(favouriteAuthorIndex) == Keys.KEY_SINGLE_POEM_AUTHOR_ID) {
                    cursor.close();
                    return true;
                }
            }

            cursor.close();
            return false;
        }

        long[] titleAndTextIds = getTitleAndTextId(title, authorId);
        String[] resultColumns = {
                KEY_FAVOURITE_TITLE_ID,
                KEY_FAVOURITE_AUTHOR_ID
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_FAVOURITE, resultColumns, null, null, null, null, null);
        int favouriteTitleIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_TITLE_ID);
        int favouriteAuthorIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_AUTHOR_ID);

        while (cursor.moveToNext()) {
            if (cursor.getLong(favouriteTitleIndex) == titleAndTextIds[0] && cursor.getLong(favouriteAuthorIndex) == authorId) {
                cursor.close();
                return true;
            }
        }

        cursor.close();
        return false;
    }

    public long insertAuthor(String authorFullName, String authorShortName) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_AUTHOR_FULL_NAME, authorFullName);
        newValues.put(KEY_AUTHOR_SHORT_NAME, authorShortName);
        return getWritableDatabase().insert(DATABASE_TABLE_AUTHORS, null, newValues);
    }

    public void insertAuthors(String[] authorFullNames, String[] authorShortNames) {
        for (int i = 0; i < authorFullNames.length; i++) {
            insertAuthor(authorFullNames[i], authorShortNames[i]);
        }
    }

    public void insertPoems(String[] titles, String[] texts, String authorFullName) {
        int authorId = getAutorId(authorFullName);
        if (authorId == -1) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        for (int i = 0; i < titles.length; i++) {
            long textId = insertText(texts[i]);
            ContentValues newValues = new ContentValues();
            newValues.put(KEY_POEM_TITLE, titles[i]);
            newValues.put(KEY_POEM_TEXT_ID, textId);
            newValues.put(KEY_AUTHOR_ID, authorId);
            db.insert(DATABASE_TABLE_POEM_TITLES, null, newValues);
        }
    }

    public long insertSinglePoem(String title, String text, String author) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_SINGLE_POEM_AUTHOR, author);
        newValues.put(KEY_SINGLE_POEM_TITLE, title);
        newValues.put(KEY_SINGLE_POEM_TEXT_ID, insertText(text));
        return getWritableDatabase().insert(DATABASE_TABLE_SINGLE_POEMS, null, newValues);
    }

    public void insertSinglePoems(String[] titles, String[] texts, String[] authors) {
        for (int i = 0; i < titles.length; i++) {
            insertSinglePoem(titles[i], texts[i], authors[i]);
        }
    }

    public long insertFavourite(String title, String author) {
        if (isFavourite(title, author)) {
            return -1;
        }
        int authorId = author.equals(Keys.KEY_SINGLE_POEM_AUTHOR) ? Keys.KEY_SINGLE_POEM_AUTHOR_ID : getAutorId(author);
        if (authorId == -1) {
            return insertSingleInFavourite(title, author);
        }

        long[] titleAndTextIds = getTitleAndTextId(title, authorId);
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_FAVOURITE_AUTHOR_ID, authorId);
        newValues.put(KEY_FAVOURITE_TITLE_ID, titleAndTextIds[0]);
        newValues.put(KEY_FAVOURITE_TEXT_ID, titleAndTextIds[1]);
        return getWritableDatabase().insert(DATABASE_TABLE_FAVOURITE, null, newValues);
    }

    public long deleteFavourite(String title, String author) {
        if (!isFavourite(title, author)) {
            return -1;
        }
        int authorId = author.equals(Keys.KEY_SINGLE_POEM_AUTHOR) ? Keys.KEY_SINGLE_POEM_AUTHOR_ID : getAutorId(author);
        if (authorId == -1) {
            long singlePoemId = getSinglePoemId(title, author);
            if (singlePoemId == -1) {
                return -1;
            }

            return getWritableDatabase().delete(DATABASE_TABLE_FAVOURITE, KEY_FAVOURITE_ID + "=" + getFavouriteId(singlePoemId, Keys.KEY_SINGLE_POEM_AUTHOR_ID), null);
        }

        long[] titleAndTextIds = getTitleAndTextId(title, authorId);
        return getWritableDatabase().delete(DATABASE_TABLE_FAVOURITE, KEY_FAVOURITE_ID + "=" + getFavouriteId(titleAndTextIds[0], authorId), null);
    }

    public void insertData(String poemTitles, String poemTexts, String authorFullName) {
        String[] titles = poemTitles.split("=");
        String[] texts = new String[titles.length];
        StringTokenizer textsTokenizer = new StringTokenizer(poemTexts, "=");

        for (int i = 0; i < texts.length && textsTokenizer.hasMoreTokens(); i++) {
            String token = textsTokenizer.nextToken();
            int index = token.indexOf('\n');
            String title = token.substring(0, index);
            String text = token.substring(index + 1);
            int goalIndex = 0;
            for (int j = 0; j < titles.length; j++) {
                if (titles[j].equalsIgnoreCase(title)) {
                    goalIndex = j;
                    break;
                }
            }
            texts[goalIndex] = text;
        }

        insertSinglePoem(titles[0], texts[0], "***");
        //insertPoems(titles, texts, authorFullName);
    }

    private int getAutorId(String authorFullName) {
        String[] resultColumns = {
                KEY_AUTHOR_ID,
                KEY_AUTHOR_FULL_NAME
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_AUTHORS, resultColumns, null, null, null, null, null);
        int authorIdIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_ID);
        int authorFullNameIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_FULL_NAME);
        int authorId = -1;

        while (cursor.moveToNext()) {
            if (cursor.getString(authorFullNameIndex).equalsIgnoreCase(authorFullName)) {
                authorId = cursor.getInt(authorIdIndex);
                break;
            }
        }

        cursor.close();
        return authorId;
    }

    private String[] getAuthor(long authorId) {
        if (authorId == Keys.KEY_SINGLE_POEM_AUTHOR_ID) {
            return new String[] {Keys.KEY_SINGLE_POEM_AUTHOR, Keys.KEY_SINGLE_POEM_AUTHOR};
        }

        String[] resultColumns = {
                KEY_AUTHOR_ID,
                KEY_AUTHOR_FULL_NAME,
                KEY_AUTHOR_SHORT_NAME
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_AUTHORS, resultColumns, null, null, null, null, null);
        int authorIdIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_ID);
        int authorFullNameIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_FULL_NAME);
        int authorShortNameIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_SHORT_NAME);
        String[] author = null;

        while (cursor.moveToNext()) {
            if (cursor.getLong(authorIdIndex) == authorId) {
                author = new String[] {cursor.getString(authorFullNameIndex), cursor.getString(authorShortNameIndex)};
                break;
            }
        }

        cursor.close();
        return author;
    }

    private long[] getTitleAndTextId(String title, int authorId) {
        String[] resultColumns = {
                KEY_POEM_TITLE_ID,
                KEY_POEM_TITLE,
                KEY_POEM_TEXT_ID,
                KEY_AUTHOR_ID
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_POEM_TITLES, resultColumns, null, null, null, null, null);
        int poemTitleIdIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TITLE_ID);
        int poemTitleIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TITLE);
        int poemTextIdIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TEXT_ID);
        int authorIdIndex = cursor.getColumnIndexOrThrow(KEY_AUTHOR_ID);
        long[] ids = null;

        while (cursor.moveToNext()) {
            if (cursor.getString(poemTitleIndex).equalsIgnoreCase(title) && cursor.getInt(authorIdIndex) == authorId) {
                ids = new long[] { cursor.getLong(poemTitleIdIndex), cursor.getLong(poemTextIdIndex) };
                break;
            }
        }

        cursor.close();
        return ids;
    }

    private String getTitle(long titleId) {
        String[] resultColumns = {
                KEY_POEM_TITLE_ID,
                KEY_POEM_TITLE,
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_POEM_TITLES, resultColumns, null, null, null, null, null);
        int poemTitleIdIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TITLE_ID);
        int poemTitleIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TITLE);
        String title = null;

        while (cursor.moveToNext()) {
            if (cursor.getLong(poemTitleIdIndex) == titleId) {
                title = cursor.getString(poemTitleIndex);
                break;
            }
        }

        cursor.close();
        return title;
    }

    private String getText(long poemTextId) {
        String[] resultColumns = {
                KEY_POEM_TEXT_ID,
                KEY_POEM_TEXT
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_POEM_TEXTS, resultColumns, null, null, null, null, null);
        int poemTextIdIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TEXT_ID);
        int poemTextIndex = cursor.getColumnIndexOrThrow(KEY_POEM_TEXT);
        String poemText = null;

        while (cursor.moveToNext()) {
            if (cursor.getLong(poemTextIdIndex) == poemTextId) {
                poemText = cursor.getString(poemTextIndex);
                break;
            }
        }

        cursor.close();
        return poemText;
    }

    private long getSinglePoemId(String title, String author) {
        String[] resultColumns = {
                KEY_SINGLE_POEM_ID,
                KEY_SINGLE_POEM_AUTHOR,
                KEY_SINGLE_POEM_TITLE,
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_SINGLE_POEMS, resultColumns, null, null, null, null, null);
        int singlePoemIdIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_ID);
        int singlePoemAuthorIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_AUTHOR);
        int singlePoemTitleIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_TITLE);
        long singlePoemId = -1;

        while (cursor.moveToNext()) {
            if (cursor.getString(singlePoemAuthorIndex).equals(author) && cursor.getString(singlePoemTitleIndex).equals(title)) {
                singlePoemId = cursor.getLong(singlePoemIdIndex);
                break;
            }
        }

        cursor.close();
        return singlePoemId;
    }

    private String getTextFromSingle(String title, String author) {
        String[] resultColumns = {
                KEY_SINGLE_POEM_AUTHOR,
                KEY_SINGLE_POEM_TITLE,
                KEY_SINGLE_POEM_TEXT_ID
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_SINGLE_POEMS, resultColumns, null, null, null, null, null);
        int singlePoemAuthorIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_AUTHOR);
        int singlePoemTitleIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_TITLE);
        int singlePoemTextIdIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_TEXT_ID);
        String text = null;

        while (cursor.moveToNext()) {
            if (cursor.getString(singlePoemAuthorIndex).equals(author) && cursor.getString(singlePoemTitleIndex).equals(title)) {
                text = getText(cursor.getLong(singlePoemTextIdIndex));
                break;
            }
        }

        cursor.close();
        return text;
    }

    private String[] getSinglePoemTitles() {
        String[] resultColumns = {
                KEY_SINGLE_POEM_AUTHOR,
                KEY_SINGLE_POEM_TITLE,
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_SINGLE_POEMS, resultColumns, null, null, null, null, null);
        int singlePoemAuthorIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_AUTHOR);
        int singlePoemTitleIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_TITLE);
        List<String> titles = new ArrayList<>();

        while (cursor.moveToNext()) {
            String title = cursor.getString(singlePoemTitleIndex) +
                    " - " +
                    cursor.getString(singlePoemAuthorIndex);
            titles.add(title);
        }

        cursor.close();
        return toStringArray(titles.toArray());
    }

    private long getFavouriteId(long titleId, int authorId) {
        String[] resultColumns = {
                KEY_FAVOURITE_ID,
                KEY_FAVOURITE_AUTHOR_ID,
                KEY_FAVOURITE_TITLE_ID,
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_FAVOURITE, resultColumns, null, null, null, null, null);
        int favouriteIdIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_ID);
        int authorIdIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_AUTHOR_ID);
        int titleIdIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_TITLE_ID);
        long favouriteId = -1;

        while (cursor.moveToNext()) {
            if (cursor.getLong(titleIdIndex) == titleId && cursor.getInt(authorIdIndex) == authorId) {
                favouriteId = cursor.getLong(favouriteIdIndex);
                break;
            }
        }

        cursor.close();
        return favouriteId;
    }

    private String[] getFavouritePoemTitles() {
        String[] resultColumns = {
                KEY_FAVOURITE_AUTHOR_ID,
                KEY_FAVOURITE_TITLE_ID
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_FAVOURITE, resultColumns, null, null, null, null, null);
        int favouriteAuthorIdIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_AUTHOR_ID);
        int favouriteTitleIdIndex = cursor.getColumnIndexOrThrow(KEY_FAVOURITE_TITLE_ID);
        List<String> titles = new ArrayList<>();

        while (cursor.moveToNext()) {
            long favouriteAuthorId = cursor.getLong(favouriteAuthorIdIndex);
            long favouriteTitleId = cursor.getLong(favouriteTitleIdIndex);
            String title = null;

            if (favouriteAuthorId == Keys.KEY_SINGLE_POEM_AUTHOR_ID) {
                String[] singleResultColumns = {
                        KEY_SINGLE_POEM_ID,
                        KEY_SINGLE_POEM_AUTHOR,
                        KEY_SINGLE_POEM_TITLE,
                };
                Cursor singleCursor = getReadableDatabase().query(DATABASE_TABLE_SINGLE_POEMS, singleResultColumns, null, null, null, null, null);
                int singlePoemIdIndex = singleCursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_ID);
                int singlePoemAuthorIndex = singleCursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_AUTHOR);
                int singlePoemTitleIndex = singleCursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_TITLE);

                while (singleCursor.moveToNext()) {
                    if (singleCursor.getLong(singlePoemIdIndex) == favouriteTitleId) {
                        title = singleCursor.getString(singlePoemTitleIndex) +
                                " - " +
                                singleCursor.getString(singlePoemAuthorIndex);
                        break;
                    }
                }

                singleCursor.close();
            } else {
                title = getTitle(favouriteTitleId) +
                        " - " +
                        getAuthor(favouriteAuthorId)[1];
            }

            titles.add(title);
        }

        cursor.close();
        return toStringArray(titles.toArray());
    }

    private long insertText(String text) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_POEM_TEXT, text);
        return getWritableDatabase().insert(DATABASE_TABLE_POEM_TEXTS, null, newValues);
    }

    private long insertSingleInFavourite(String title, String author) {
        String[] resultColumns = {
                KEY_SINGLE_POEM_ID,
                KEY_SINGLE_POEM_AUTHOR,
                KEY_SINGLE_POEM_TITLE,
        };
        Cursor cursor = getReadableDatabase().query(DATABASE_TABLE_SINGLE_POEMS, resultColumns, null, null, null, null, null);
        int singlePoemIdIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_ID);
        int singlePoemAuthorIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_AUTHOR);
        int singlePoemTitleIndex = cursor.getColumnIndexOrThrow(KEY_SINGLE_POEM_TITLE);
        long singlePoemId = -1;

        while (cursor.moveToNext()) {
            if (cursor.getString(singlePoemAuthorIndex).equals(author) && cursor.getString(singlePoemTitleIndex).equals(title)) {
                singlePoemId = cursor.getLong(singlePoemIdIndex);
                break;
            }
        }

        cursor.close();

        if (singlePoemId == -1) {
            return -1;
        }

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_FAVOURITE_AUTHOR_ID, Keys.KEY_SINGLE_POEM_AUTHOR_ID);
        newValues.put(KEY_FAVOURITE_TITLE_ID, singlePoemId);
        newValues.put(KEY_FAVOURITE_TEXT_ID, singlePoemId);
        return getWritableDatabase().insert(DATABASE_TABLE_FAVOURITE, null, newValues);
    }

    private String[] toStringArray(Object[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }
}
