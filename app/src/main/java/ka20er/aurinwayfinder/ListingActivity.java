package ka20er.aurinwayfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import static ka20er.aurinwayfinder.DataRetrievalService.listRelevantLocation;

public class ListingActivity extends AppCompatActivity {
    ListView listView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        listView = (ListView)findViewById(R.id.liView);
        adapter = new MyAdapter(this, R.layout.custom_layout, listRelevantLocation);
        listView.setAdapter(adapter);
/**
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String loc = parent.getItemAtPosition(position).toString();
                Log.i("Item " + position + " clicked with value ", loc);
            }
        }); */
    }
}
