package android.unipu.zapisime_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.unipu.zapisime_app.Adapter.ToDoAdapter;
import android.unipu.zapisime_app.Model.ToDoModel;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

// Klasa koja će se koristiti za swipe-anje ulijevo ili udesno za uređivanje ili brisanje bilješke

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    // konstruktor
    public RecyclerItemTouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition(); // pozicija elementa kojeg će se swipe-ati
        // ako se swipe-a ulijevo će se bilješka izbrisati
        if(direction == ItemTouchHelper.LEFT) {
            // prije brisanja će se korisnika pitati želi li stvarno obrisati tu bilješku
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Brisanje bilješke");
            builder.setMessage("Jeste li sigurni da želite izbrisati ovu bilješku?");
            builder.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.deleteItem(position); // brisanje bilješke
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            // ak se swipe-a udesno onda se uređuje bilješka
            adapter.editItem(position);
        }
    }

    // funkcije za swipe-anje ulijevo/udesno

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        // ako se swipe-a ulijevo
        if(dX > 0){
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.colorPrimaryDark));
        }
        else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
            background = new ColorDrawable(Color.RED);
        }

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        // swipe-anje udesno
        if(dX > 0){
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int)dX) + backgroundCornerOffset, itemView.getBottom());
        }
        // swipe-anje ulijevo
        else if(dX < 0){
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int)dX) - backgroundCornerOffset, itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
        }
        else {
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        icon.draw(c);
    }
}
