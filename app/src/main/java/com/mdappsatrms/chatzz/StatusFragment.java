package com.mdappsatrms.chatzz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatusFragment extends Fragment {
    private View statusView;
    TextView tvStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        statusView= inflater.inflate(R.layout.fragment_status, container, false);
        tvStatus = statusView.findViewById(R.id.textViewStatus);
        tvStatus.setText("HERE STATUS WILL BE SHOWN");
        Log.d("NOWCALLED ONCREATE","NOW_CALLED ONCREATE");

        return statusView;
    }
    public StatusFragment() {

        Log.d("NOWCALLED CONSTRUCTOR","NOW_CALLED CONSTRUCTOR");

    }


}