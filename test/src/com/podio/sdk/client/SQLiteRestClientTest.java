package com.podio.sdk.client;

import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.InstrumentationTestCase;

import com.podio.sdk.client.database.DatabaseHelper;
import com.podio.sdk.internal.request.RestOperation;
import com.podio.test.TestUtils;

public class SQLiteRestClientTest extends InstrumentationTestCase {

    private static final class ConcurrentResult {
        private boolean isDeleteCalled;
        private boolean isInsertCalled;
        private boolean isQueryCalled;
        private boolean isUpdateCalled;
    }

    private SQLiteRestClient target;
    private ConcurrentResult result;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Instrumentation instrumentation = getInstrumentation();
        Context context = instrumentation.getContext();

        result = new ConcurrentResult();

        target = new SQLiteRestClient(context, "authority");
        target.setDatabaseHelper(new DatabaseHelper() {

            @Override
            public Cursor delete(Uri uri) {
                result.isDeleteCalled = true;
                return null;
            }

            @Override
            public Cursor insert(Uri uri, ContentValues contentValues) {
                result.isInsertCalled = true;
                return null;
            }

            @Override
            public Cursor query(Uri uri) {
                result.isQueryCalled = true;
                return null;
            }

            @Override
            public Cursor update(Uri uri, ContentValues contentValues) {
                result.isUpdateCalled = true;
                return null;
            }

            @Override
            public void initialize(SQLiteDatabase database) {
            }

        });
    }

    /**
     * Verifies that a delete rest operation is delegated correctly to the
     * {@link DatabaseHelper}.
     * 
     * <pre>
     * 
     * 1. Create a new {@link SQLiteRestClient} and add a mock
     *      {@link DatabaseHelper} to it.
     * 
     * 2. Push a delete operation to the client.
     * 
     * 3. Verify that the delete method of the database helper
     *      is called.
     * 
     * </pre>
     */
    public void testDeleteOperationIsDelegatedCorrectly() {
        target.perform(new RestRequest().setOperation(RestOperation.DELETE));
        TestUtils.blockThread(20);
        assertEquals(true, result.isDeleteCalled);
        assertEquals(false, result.isInsertCalled);
        assertEquals(false, result.isQueryCalled);
        assertEquals(false, result.isUpdateCalled);
    }

    /**
     * Verifies that a get rest operation is delegated correctly to the
     * {@link DatabaseHelper}.
     * 
     * <pre>
     * 
     * 1. Create a new {@link SQLiteRestClient} and add a mock
     *      {@link DatabaseHelper} to it.
     * 
     * 2. Push a get operation to the client.
     * 
     * 3. Verify that the query method of the database helper
     *      is called.
     * 
     * </pre>
     */
    public void testGetOperationIsDelegatedCorrectly() {
        target.perform(new RestRequest().setOperation(RestOperation.GET));
        TestUtils.blockThread(20);
        assertEquals(false, result.isDeleteCalled);
        assertEquals(false, result.isInsertCalled);
        assertEquals(true, result.isQueryCalled);
        assertEquals(false, result.isUpdateCalled);
    }

    /**
     * Verifies that a post rest operation is delegated correctly to the
     * {@link DatabaseHelper}.
     * 
     * <pre>
     * 
     * 1. Create a new {@link SQLiteRestClient} and add a mock
     *      {@link DatabaseHelper} to it.
     * 
     * 2. Push a post operation to the client.
     * 
     * 3. Verify that the insert method of the database helper
     *      is called.
     * 
     * </pre>
     */
    public void testPostOperationIsDelegatedCorrectly() {
        target.perform(new RestRequest().setOperation(RestOperation.POST));
        TestUtils.blockThread(20);
        assertEquals(false, result.isDeleteCalled);
        assertEquals(true, result.isInsertCalled);
        assertEquals(false, result.isQueryCalled);
        assertEquals(false, result.isUpdateCalled);
    }

    /**
     * Verifies that a put rest operation is delegated correctly to the
     * {@link DatabaseHelper}.
     * 
     * <pre>
     * 
     * 1. Create a new {@link SQLiteRestClient} and add a mock
     *      {@link DatabaseHelper} to it.
     * 
     * 2. Push a put operation to the client.
     * 
     * 3. Verify that the update method of the database helper
     *      is called.
     * 
     * </pre>
     */
    public void testPutOperationIsDelegatedCorrectly() {
        target.perform(new RestRequest().setOperation(RestOperation.PUT));
        TestUtils.blockThread(20);
        assertEquals(false, result.isDeleteCalled);
        assertEquals(false, result.isInsertCalled);
        assertEquals(false, result.isQueryCalled);
        assertEquals(true, result.isUpdateCalled);
    }
}
