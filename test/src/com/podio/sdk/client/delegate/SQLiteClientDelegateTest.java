package com.podio.sdk.client.delegate;

import java.util.ArrayList;
import java.util.List;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.InstrumentationTestCase;

import com.podio.sdk.client.RestResult;
import com.podio.sdk.client.delegate.mock.MockContentItem;
import com.podio.sdk.parser.ItemToJsonParser;
import com.podio.sdk.parser.JsonToItemParser;

public class SQLiteClientDelegateTest extends InstrumentationTestCase {

    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Delete any previously created database.
        Instrumentation instrumentation = getInstrumentation();
        Context context = instrumentation.getTargetContext();
        context.deleteDatabase(DATABASE_NAME);
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a delete request even if the caller provides an empty URI.
     * 
     * <pre>
     * 
     * 1. Pass on an empty Uri to the database helpers delete method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testDeleteHandlesEmptyUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.delete(Uri.EMPTY);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
        assertNull(result.message());
        assertNull(result.item());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a delete request even if the caller provides a null-pointer URI.
     * 
     * <pre>
     * 
     * 1. Pass on a null pointer Uri to the database helpers delete method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testDeleteHandlesNullUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.delete(null);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns the correct status
     * when a row was successfully deleted as a result of a valid URI pointing
     * to an existing row.
     * 
     * <pre>
     * 
     * 1. Insert a test row into a table.
     * 
     * 2. Create a valid Uri that points to the test row.
     * 
     * 3. Call the delete method of the database helper with the test URI.
     * 
     * 4. Verify that the result has a success flag.
     * 
     * </pre>
     */
    public void testDeleteReturnsCorrectStatusWhenRowDeleted() {
        Uri validUri = Uri.parse("content://test/uri");

        ContentValues values = new ContentValues();
        values.put("uri", validUri.toString());
        values.put("json", "{text: 'test'}");

        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        sqliteDatabase.insert("content", null, values);

        RestResult result = databaseHelper.delete(validUri);
        assertNotNull(result);
        assertEquals(true, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns the correct status
     * when there was no row to be deleted as a result of a valid URI not
     * pointing to an existing row.
     * 
     * <pre>
     * 
     * 2. Create a valid URI that doesn't point to any existing row in the table.
     * 
     * 3. Call the delete method of the database helper with the test URI.
     * 
     * 4. Verify that the result has a success flag.
     * 
     * </pre>
     */
    public void testDeleteReturnsCorrectStatusWhenNoRowDeleted() {
        Uri validUri = Uri.parse("content://test/uri");

        ContentValues values = new ContentValues();
        values.put("uri", validUri.toString() + "/not-this-one");
        values.put("json", "{text: 'test'}");

        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        sqliteDatabase.insert("content", null, values);

        RestResult result = databaseHelper.delete(validUri);
        assertNotNull(result);
        assertEquals(true, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a get request even if the caller provides an empty URI.
     * 
     * <pre>
     * 
     * 1. Pass on an empty Uri to the database helpers get method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testGetHandlesEmptyUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.get(Uri.EMPTY, null);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a get request even if the caller provides a null-pointer URI.
     * 
     * <pre>
     * 
     * 1. Pass on an empty Uri to the database helpers get method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testGetHandlesNullUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.get(null, null);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns the correct result
     * to a get request when an existing row is requested.
     * 
     * <pre>
     * 
     * 1. Insert test rows to the table.
     * 
     * 2. Request a known test row with the database helper object.
     * 
     * 3. Verify that the result has a success flag.
     * 
     * 4. Verify that the result contains the correct content.
     * 
     * </pre>
     */
    public void testGetReturnsCorrectItem() {
        MockContentItem[] content = { new MockContentItem("test://uri/app/0", "{text:'test 0'}"),
                new MockContentItem("test://uri/app/1", "{text:'test 1'}") };

        ItemToJsonParser parser = new ItemToJsonParser();
        ContentValues[] values = { new ContentValues(), new ContentValues() };
        values[0].put("uri", content[0].uri);
        values[0].put("json", parser.parse(content[0], MockContentItem.class));
        values[1].put("uri", content[1].uri);
        values[1].put("json", parser.parse(content[1], MockContentItem.class));

        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        sqliteDatabase.insert("content", null, values[0]);
        sqliteDatabase.insert("content", null, values[1]);

        Uri uri = Uri.parse(content[0].uri);
        RestResult result = databaseHelper.get(uri, MockContentItem.class);

        assertNotNull(result);
        assertNotNull(result.item());
        assertEquals(true, result.isSuccess());

        MockContentItem item = (MockContentItem) result.item();
        assertEquals(content[0].uri, item.uri);
        assertEquals(content[0].json, item.json);
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a post request even if the caller provides an empty URI.
     * 
     * <pre>
     * 
     * 1. Pass on an empty Uri to the database helpers post method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testPostHandlesEmptyUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.post(Uri.EMPTY, null, null);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a post request even if the caller provides a null-pointer URI.
     * 
     * <pre>
     * 
     * 1. Pass on an empty Uri to the database helpers post method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testPostHandlesNullUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.post(null, null, null);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns the correct status
     * when a row was successfully inserted.
     * 
     * <pre>
     * 
     * 1. Insert a test row with the database helper.
     * 
     * 2. Verify that the result has a success flag.
     * 
     * </pre>
     */
    public void testPostReturnsCorrectStatusWhenRowAdded() {
        MockContentItem item = new MockContentItem("test://uri/app/", "{text: 'test'}");
        Uri uri = Uri.parse(item.uri);

        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.post(uri, item, MockContentItem.class);
        assertNotNull(result);
        assertEquals(true, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a put request even if the caller provides an empty URI.
     * 
     * <pre>
     * 
     * 1. Pass on an empty Uri to the database helpers put method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testPutHandlesEmptyUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.put(Uri.EMPTY, null, null);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns a valid result to
     * a put request even if the caller provides a null-pointer URI.
     * 
     * <pre>
     * 
     * 1. Pass on an empty Uri to the database helpers put method.
     * 
     * 2. Verify that a {@link RestResult} is returned.
     * 
     * 3. Verify that the result has a no-success flag.
     * 
     * </pre>
     */
    public void testPutHandlesNullUriCorrectly() {
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        RestResult result = databaseHelper.put(null, null, null);
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns the correct status
     * when a row was successfully updated.
     * 
     * <pre>
     * 
     * 1. Insert a test row in the table.
     * 
     * 2. Update the test row with the database helper.
     * 
     * 3. Verify that the result has a success flag.
     * 
     * </pre>
     */
    public void testPutReturnsCorrectStatusWhenRowAdded() {
        MockContentItem item = new MockContentItem("test://uri/app/", "{text: 'test'}");

        ContentValues values = new ContentValues();
        values.put("uri", item.uri);
        values.put("json", item.json);

        Uri uri = Uri.parse(item.uri);

        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        sqliteDatabase.insert("content", null, values);

        item.json = "{text: 'test-updated'}";

        RestResult result = databaseHelper.put(uri, item, MockContentItem.class);
        assertNotNull(result);
        assertEquals(true, result.isSuccess());
    }

    /**
     * Verifies that the {@link SQLiteClientDelegate} returns the updated values
     * when a row was successfully updated.
     * 
     * <pre>
     * 
     * 1. Insert a test row into the task table.
     * 
     * 2. Update the test row with a new value.
     * 
     * 3. Verify that the updated values are returned once requested.
     * 
     * </pre>
     */
    public void testPutReturnsCorrectValuesAfterRowUpdated() {
        MockContentItem item = new MockContentItem("test://uri/app/", "{text: 'test'}");

        ContentValues values = new ContentValues();
        values.put("uri", item.uri);
        values.put("json", item.json);

        Uri uri = Uri.parse(item.uri);

        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        sqliteDatabase.insert("content", null, values);

        item.json = "{text: 'test-updated'}";
        databaseHelper.put(uri, item, MockContentItem.class);

        String table = "content";
        String key = "uri=?";
        String[] value = { item.uri };
        Cursor cursor = sqliteDatabase.query(table, null, key, value, null, null, null);

        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();

        String fetchedUri = cursor.getString(cursor.getColumnIndex("uri"));
        String fetchedJson = cursor.getString(cursor.getColumnIndex("json"));

        JsonToItemParser parser = new JsonToItemParser();
        MockContentItem fetchedItem = (MockContentItem) parser.parse(fetchedJson,
                MockContentItem.class);

        assertEquals(item.uri, fetchedUri);
        assertEquals(item.uri, fetchedItem.uri);
        assertEquals(item.json, fetchedItem.json);
    }

    /**
     * Verifies that the database is initialized with the expected tables when
     * created.
     * 
     * <pre>
     * 
     * 1. Create a new {@link SQLiteClientDelegate} object.
     * 
     * 2. Verify that the expected tables have been created automatically.
     * 
     * </pre>
     */
    public void testTablesCreated() {
        // Query all non-system tables from the database and verify that there
        // is five of them.
        String query = "SELECT name FROM sqlite_master WHERE type = ? AND name != ? AND name != ?";
        String[] arguments = { "table", "sqlite_master", "android_metadata" };
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery(query, arguments);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        // Collect all table names in a list.
        List<String> tables = new ArrayList<String>();
        cursor.moveToFirst();

        do {
            String table = cursor.getString(0);
            tables.add(table);
        } while (cursor.moveToNext());

        // Verify that the list has all expected table names.
        assertTrue(tables.contains("content"));
    }

    /**
     * Verifies that the content table is reset when the database is downgraded
     * to an older version.
     * 
     * <pre>
     * 
     * 1. Create a new {@link SQLiteClientDelegate} object.
     * 
     * 2. Add some data to the content table.
     * 
     * 3. Create a new {@link SQLiteClientDelegate} object, with a lower version.
     * 
     * 4. Verify that the delegate object with lower version has properly reset
     *      the tables.
     * 
     * </pre>
     */
    public void testTaskTableResetOnVersionDowngrade() {
        // Insert test data.
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        sqliteDatabase.execSQL("INSERT INTO content (uri, json) VALUES ('a://b', '{c:d}')");

        Cursor cursor = sqliteDatabase.rawQuery("SELECT COUNT(content.uri) FROM content", null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(1, cursor.getInt(0));

        // Verify the database is reset properly.
        SQLiteClientDelegate databaseHelper2 = getDatabaseHelper(DATABASE_VERSION - 1);
        SQLiteDatabase sqliteDatabase2 = databaseHelper2.getReadableDatabase();

        Cursor cursor2 = sqliteDatabase2.rawQuery("SELECT COUNT(content.uri) FROM content", null);
        assertNotNull(cursor2);
        assertEquals(1, cursor2.getCount());

        cursor2.moveToFirst();
        assertEquals(0, cursor2.getInt(0));
    }

    /**
     * Verifies that the content table is reset when the database is upgraded to
     * a newer version.
     * 
     * <pre>
     * 
     * 1. Create a new {@link SQLiteClientDelegate} object.
     * 
     * 2. Add some data to the content table.
     * 
     * 3. Create a new {@link SQLiteClientDelegate} object, with a higher version.
     * 
     * 4. Verify that the delegate object with higher version has properly reset
     *      the tables.
     * 
     * </pre>
     */
    public void testTaskTableResetOnVersionUpgrade() {
        // Insert test data.
        SQLiteClientDelegate databaseHelper = getDatabaseHelper(DATABASE_VERSION);
        SQLiteDatabase sqliteDatabase = databaseHelper.getWritableDatabase();
        sqliteDatabase.execSQL("INSERT INTO content (uri, json) VALUES ('a://b', '{c:d}')");

        Cursor cursor = sqliteDatabase.rawQuery("SELECT COUNT(content.uri) FROM content", null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(1, cursor.getInt(0));

        // Verify the database is reset properly.
        SQLiteClientDelegate databaseHelper2 = getDatabaseHelper(DATABASE_VERSION + 1);
        SQLiteDatabase sqliteDatabase2 = databaseHelper2.getReadableDatabase();

        Cursor cursor2 = sqliteDatabase2.rawQuery("SELECT COUNT(content.uri) FROM content", null);
        assertNotNull(cursor2);
        assertEquals(1, cursor2.getCount());

        cursor2.moveToFirst();
        assertEquals(0, cursor2.getInt(0));
    }

    /**
     * Creates a new {@link SQLiteClientDelegate} object with the given version
     * number.
     * 
     * @param version
     *            The desired version number of the database helper.
     * @return The newly created database helper object.
     */
    private SQLiteClientDelegate getDatabaseHelper(int version) {
        Instrumentation instrumentation = getInstrumentation();
        Context context = instrumentation.getTargetContext();
        return new SQLiteClientDelegate(context, DATABASE_NAME, version);
    }

}