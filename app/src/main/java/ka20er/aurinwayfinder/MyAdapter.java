package ka20er.aurinwayfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<RelevantLocation> {
    private int resourceId;

    public MyAdapter(Context context, int resource, List<RelevantLocation> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelevantLocation oneLoc = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);

        TextView retTradeName = (TextView) view.findViewById(R.id.displayTradeName);
        TextView retAddress = (TextView) view.findViewById(R.id.displayAddress);

        retTradeName.setText(oneLoc.getSavedTradeName());
        retAddress.setText(oneLoc.getSavedAddress());

        return view;
    }
}
