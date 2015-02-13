package th.ac.tu.siit.its333.lab4exercise1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    CourseDBHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new CourseDBHelper(this);   //connect

    }

    @Override
    protected void onResume() {
        super.onResume();

        // This method is called when this activity is put foreground.
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT sum(credit * value) as X, sum(credit) as Y FROM course", null);
        cursor.moveToFirst(); // get the first row
        double gradepoint = cursor.getDouble(cursor.getColumnIndex("X"));  //maybe we dont know which column to get from, so specify the column name
        double credit = cursor.getDouble(1);

        TextView v = (TextView)findViewById(R.id.tvGP);
        v.setText(String.format("%.1f", gradepoint));       //change from double to string with a format .0

        TextView v2 = (TextView)findViewById(R.id.tvCR);
        v2.setText(String.format("%.0f",credit));

        TextView v3 = (TextView)findViewById(R.id.tvGPA);
        v3.setText(String.format("%.2f", (gradepoint / credit)));

    }

    public void buttonClicked(View v) {
        int id = v.getId();
        Intent i;

        switch(id) {
            case R.id.btAdd:
                i = new Intent(this, AddCourseActivity.class);
                startActivityForResult(i, 88);
                break;

            case R.id.btShow:
                i = new Intent(this, ListCourseActivity.class);
                startActivity(i);
                break;

            case R.id.btReset:
                SQLiteDatabase db = helper.getWritableDatabase();
                int n_rows = db.delete("course", "", null);   //delete all dont have to specify
                onResume();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 88) {
            if (resultCode == RESULT_OK) {
                String code = data.getStringExtra("code");
                int credit = data.getIntExtra("credit", 0);
                String grade = data.getStringExtra("grade");

                SQLiteDatabase db = helper.getWritableDatabase();  //get so can write into database
                ContentValues r = new ContentValues();
                r.put("code", code);
                r.put("credit", credit);
                r.put("grade", grade);
                r.put("value", gradeToValue(grade));
                long new_id = db.insert("course", null, r);

                onResume();
            }
        }

        Log.d("course", "onActivityResult"); //show in logcat
    }

    double gradeToValue(String g) {
        if (g.equals("A"))
            return 4.0;
        else if (g.equals("B+"))
            return 3.5;
        else if (g.equals("B"))
            return 3.0;
        else if (g.equals("C+"))
            return 2.5;
        else if (g.equals("C"))
            return 2.0;
        else if (g.equals("D+"))
            return 1.5;
        else if (g.equals("D"))
            return 1.0;
        else
            return 0.0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
