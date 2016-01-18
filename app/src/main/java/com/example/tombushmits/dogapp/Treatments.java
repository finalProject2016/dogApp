package com.example.tombushmits.dogapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class Treatments extends AppCompatActivity implements HttpInterfaceHandler{
    String url = "";
    httpAsyncTask task;
    final public ArrayList<Treatment> treatmentList=new ArrayList<Treatment>();
    TreatmentAdapter adapter;
    String testVar;
    PopupWindow pop;
    RelativeLayout layout;
    RelativeLayout main_layout;
    TextView txt;
    boolean click = true;
    private PopupWindow pwindo;
    boolean changes = false;
    int lastAddedIndex=0;

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatments);
        getAllTreatment();
        lv = (ListView)findViewById(R.id.treatmentListView); //populating the arrayadapter into a listview
        adapter = new TreatmentAdapter(this, R.layout.treatment_row,treatmentList);
        lv.setAdapter(adapter);

        //TODO: need to set OnClick listener for each item in the list
        pop = new PopupWindow(this);
        layout = new RelativeLayout(this);
        main_layout = new RelativeLayout(this);


        final ImageButton button = (ImageButton)findViewById(R.id.imageButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) Treatments.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.popupmenu, (ViewGroup) findViewById(R.id.popup_element));
                pwindo = new PopupWindow(layout, 1100, 900, true);
                pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
                pwindo.setWindowLayoutType(ViewGroup.LayoutParams.WRAP_CONTENT);

                pwindo.setFocusable(true);

                final EditText new_treat_text = (EditText)layout.findViewById(R.id.new_treatment_treatmentname);
                final EditText new_treat_doctor= (EditText)layout.findViewById(R.id.new_treatment_doctor);
                final EditText new_treat_date = (EditText)layout.findViewById(R.id.new_treatment_date);
                final EditText new_treat_comments = (EditText)layout.findViewById(R.id.new_treatment_comments);
                Button new_treat_done = (Button)layout.findViewById(R.id.new_treatment_done);

                new_treat_done.setOnClickListener(new View.OnClickListener() {
                    Treatment toAdd ;
                    @Override
                    public void onClick(View v) {
                        toAdd  = new Treatment(new_treat_doctor.getText().toString(), new_treat_text.getText().toString(),new_treat_date.getText().toString(), new_treat_comments.getText().toString());
                        treatmentList.add(toAdd);
                        adapter.notifyDataSetChanged();
                        changes = true;
                        pwindo.dismiss();
                       int size =  treatmentList.size();
                        JSONObject json = new JSONObject();

                        try {
                            json.put("comment", treatmentList.get(size - 1).getDetails());
                            json.put("treatment_name",treatmentList.get(size-1).getName());
                            json.put("doctor", treatmentList.get(size - 1).getDoctorName());
                            json.put("date",treatmentList.get(size-1).getDate());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        httpAsyncTask task = (httpAsyncTask)new httpAsyncTask(Treatments.this);
                        task.setContext(Treatments.this);
                        task.execute(getString(R.string.server_url) + "/updateTreatments/" + DogHolder.getInstance().getId(),"POST",json.toString());



                    }


                });


            }



            });


    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            Treatment treatment = treatmentList.get(position);
            AlertDialog alert;
            AlertDialog.Builder builder= new AlertDialog.Builder(Treatments.this,R.style.PositiveAlertDialog);
            builder.setTitle(treatment.getName());
            builder.setMessage("Treating doctor is : " + treatment.getDoctorName() + "\n" + treatment.getDetails());
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

            });
            builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //delete
                    httpAsyncTask task  = (httpAsyncTask)new httpAsyncTask(Treatments.this);
                    task.setContext(Treatments.this);
                    task.execute(getString(R.string.server_url) + "/deleteTreatment/" + treatmentList.get(position).getId(), "GET");
                    treatmentList.remove(position);
                    adapter.notifyDataSetChanged();



                }
            });
            alert = builder.create();
            alert.show();






        }
    });



    }


    public void getAllTreatment() {
        task = (httpAsyncTask) new httpAsyncTask(Treatments.this);
        task.setContext(this);
        task.execute(getString(R.string.server_url) + "/getAllTreatments/" + DogHolder.getInstance().getId(), "GET");

    }

    public void backupTreatments() throws JSONException {
        JSONArray array  = new JSONArray();
        if(treatmentList.size()>0) {
            for (int i = 0; i < treatmentList.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("treatment_id", treatmentList.get(i).getId());
                obj.put("treatment_name", treatmentList.get(i).getName());
                obj.put("doctor", treatmentList.get(i).getDoctorName());
                obj.put("date", treatmentList.get(i).getDate());
                obj.put("comment", treatmentList.get(i).getDetails());
                array.put(obj);
            }
            JSONObject toSend = new JSONObject();
            toSend.put("Treatments", array); //this is sent on Post

            httpAsyncTask task = (httpAsyncTask) new httpAsyncTask(Treatments.this);
            task.setContext(this);
            task.execute(getString(R.string.server_url) + "/updateTreatments/" + DogHolder.getInstance().getId(), "POST", toSend.toString());
        }
    }
    @Override
    public void postHandle(String result) throws JSONException {

    }

    @Override
    public void getHandle(String result) throws JSONException {
        //get all treatment here
        JSONObject json = new JSONObject(result);
        Treatment toAdd=null ;
        JSONArray array = json.getJSONArray("Treatments"); //gettting top key from the json array
        int size = array.length(); //iterate over each value (=each treatment) and parse data
        for(int i=0; i< array.length(); i++)
        {
            JSONObject obj = array.getJSONObject(i);
            String treatment = obj.get("treatment_name").toString();
            String doctor = obj.get("doctor_name").toString();
            String date = obj.get("date").toString();
            String comments = obj.get("comments").toString();
            String treatment_id = obj.get("treatment_id").toString();
            toAdd = new Treatment(doctor,treatment,date,comments);
            toAdd.setId(treatment_id);
            treatmentList.add(toAdd);

        }
        adapter.notifyDataSetChanged();

    }






//costumizing special adapter for the Treatment Object
public static class TreatmentAdapter extends ArrayAdapter<Treatment> {
    private Activity activity;
    private ArrayList<Treatment> treatmentlist;
    private static LayoutInflater inflater = null;


    public TreatmentAdapter(Activity activity, int textViewResourceId, ArrayList<Treatment> _treatmentsList) {
        super(activity, textViewResourceId, _treatmentsList);
        try {
            this.activity = activity;
            this.treatmentlist = _treatmentsList;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }
    static class TreatmentHolder {
        TextView treatment_name, treatment_date;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final TreatmentHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.treatment_row, null);
                holder = new TreatmentHolder();

                holder.treatment_name = (TextView)vi.findViewById(R.id.row_Treatment_name);
                holder.treatment_date = (TextView) vi.findViewById(R.id.row_Treatment_date);


                vi.setTag(holder);
            } else {
                holder = (TreatmentHolder) vi.getTag();
            }


            holder.treatment_name.setText(treatmentlist.get(position).getName());
            holder.treatment_date.setText(treatmentlist.get(position).getDate());


        } catch (Exception e) {


        }
        return vi;
    }





}


    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        changes = false;
        try {
            backupTreatments();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onBackPressed();  // optional depending on your needs
    }


}