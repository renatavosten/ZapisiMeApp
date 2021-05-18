package android.unipu.zapisime_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.unipu.zapisime_app.Adapter.ToDoAdapter;
import android.unipu.zapisime_app.Model.ToDoModel;
import android.unipu.zapisime_app.Utils.DatabaseHandler;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private DatabaseHandler db;
    private FloatingActionButton fab;

    // varijabla koja će pohraniti sve unesene bilješke
    private List<ToDoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sakrivanje ActionBar-a
        Objects.requireNonNull(getSupportActionBar()).hide();

        // definiranje baze podataka
        db = new DatabaseHandler(this);
        db.openDatabase();

        taskList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        // postavljanje RecyclerView-a kao LinearLayout-a
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // postavljanje Adaptera na RecyclerView
        tasksAdapter = new ToDoAdapter(db, MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        fab = findViewById(R.id.fab);

        // pozivanje klase RecyclerItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskList = db.getAllTasks(); // dohvat svih bilješki iz baze
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList); // zadnje dodana bilješka će biti na prvom mjestu u listi
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}