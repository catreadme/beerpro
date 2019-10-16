package ch.beerpro.presentation.details.createrating;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import ch.beerpro.R;

public class AromasDialog extends DialogFragment {

    public interface AromasDialogListener {
        void onAromasResult(ArrayList<Integer> aromas);
    }

    private AromasDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<Integer> aromas = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Aromas")
                .setMultiChoiceItems(R.array.aromas, null,
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                aromas.add(which);
                            } else if (aromas.contains(which)) {
                                aromas.remove(which);
                            }
                        })
                .setPositiveButton("Fertig", (dialog, id) -> {
                    this.listener.onAromasResult(aromas);
                })
                .setNegativeButton("Abbrechen", (dialog, id) -> {
                    // Do nothing
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AromasDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AromasDialogListener");
        }
    }
}
