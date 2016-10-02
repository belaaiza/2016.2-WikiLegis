package gppmds.wikilegis.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DaoUtilities {

    private static DatabaseHelper database;
    //private static SQLiteDatabase sqliteDatabase;
    private static Context context;

    //Método para inserir no banco local
    protected long insertAndClose(final SQLiteDatabase sqLiteDatabase, final String table,
                                  final ContentValues values) {

        sqLiteDatabase.insert(table, null, values);
        long resultInsert = 1;

        sqLiteDatabase.close();

        return resultInsert;
    }

    //Metodo para deletar do banco local
    protected long deleteAndClose(final SQLiteDatabase sqLiteDatabase, final String table) {
        int delete;

        delete = sqLiteDatabase.delete(table, null, null);

        sqLiteDatabase.close();

        return delete;
    }

    protected static DatabaseHelper getDatabase() {
        return database;
    }

    protected static void setDatabase(DatabaseHelper database) {
        DaoUtilities.database = database;
    }


    protected static Context getContext() {
        return context;
    }

    protected static void setContext(Context context) {
        DaoUtilities.context = context;
    }
}
