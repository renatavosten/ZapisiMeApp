package android.unipu.zapisime_app.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.unipu.zapisime_app.AddNewTask;
import android.unipu.zapisime_app.MainActivity;
import android.unipu.zapisime_app.Model.ToDoModel;
import android.unipu.zapisime_app.R;
import android.unipu.zapisime_app.Utils.DatabaseHandler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList; // definiranje varijable koja će služiti kao lista (popis) bilješki
    private MainActivity activity;
    private DatabaseHandler db; // definiranje baze podataka

    // konstruktor
    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    // definiranje ViewHolder funkcije
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    // definiranje OnBind funkcije
    public void onBindViewHolder(ViewHolder holder, int position){
        db.openDatabase();
        ToDoModel item = todoList.get(position); // traženje pozicije neke određene bilješke s liste
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        // svaki put kad korisnik označi bilješku to se treba poslati u bazu
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // ako je bilješka označena kao riješena
                if(isChecked) {
                    db.updateStatus(item.getId(), 1);
                }
                else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    // funkcija za brisanje bilješke
    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position); // bilješka koja se briše
        db.deleteTask(item.getId()); // brisanje bilješke u bazi po id-u
        todoList.remove(position);
        notifyItemRemoved(position); // automatski će ažurirati RecycleView kad se bilješka obriše
    }

    // funkcija za uređivanje pojedine bilješke
    public void editItem(int position) {
        ToDoModel item = todoList.get(position); // za dobivanje bilješke koju korisnik želi urediti
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;

        ViewHolder(View view){
            super(view);
            task= view.findViewById(R.id.todoChekBox);
        }
    }
}
