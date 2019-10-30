package ch.beerpro.presentation.details.createrating;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ch.beerpro.R;

public class BitternessDialog extends DialogFragment {
    public interface BitternessDialogListener {
        void onBitternessResult(String bitterness);
    }
    private BitternessDialogListener listener;
    private String[] bitternessList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.bitternessList = getResources().getStringArray(R.array.bitterness);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bitterkeit")
                .setItems(R.array.bitterness, (dialog, which) -> {
                    listener.onBitternessResult(this.bitternessList[which]);
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BitternessDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BitternessDialogListener");
        }
    }
}
