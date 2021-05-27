package android.unipu.zapisime_app.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.unipu.zapisime_app.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

// koristit ćemo bazu SQLite za spremanje korisnikovih bilješki
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1; // verzija baze podataka
    private static final String NAME = "toDoListDatabase"; // ime baze podataka
    private static final String TODO_TABLE = "todo"; // ime tablice
    // imena stupaca
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    // definiranje upita za stvaranje baze
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    // konstruktor
    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE); // kreiranje tablice
    }

    // metoda koja je dio klase SQLiteOpenHelper
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // micanje starijih tablica ako postoje
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // ponovno kreiranje tablica
        onCreate(db);
    }

    // funkcije koje će Main aktivnost pozivati
    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    // funkcija za dohvaćanje svih bilješki iz baze podataka i njihovo pohranjivanje u ArrayList
    // iz ArrayList-a će se dohvaćati u Main aktivnost gdje će se onda prikazivati u RecyclerView-u
    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    // funkcija za označavanje bilješki jesu li izvršene ili ne
    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    // funkcija za ažuriranje bilješke
    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    // funkcija za brisanje bilješke
    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
