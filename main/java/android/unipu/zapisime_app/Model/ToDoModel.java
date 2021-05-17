package android.unipu.zapisime_app.Model;

public class ToDoModel {
    // definiranje varijabli - svaki zadatak(task) će imati svoj id za dohvaćanje iz baze i status
    private int id, status;
    private String task;

    // definiranje gettera i settera za svaku varijablu

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
