package app.ewtc.masterung.easyform.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import app.ewtc.masterung.easyform.R;
import app.ewtc.masterung.easyform.sqlite.MyManager;
import app.ewtc.masterung.easyform.sqlite.MyOpenHelper;
import app.ewtc.masterung.easyform.utility.MyAlertDialog;

/**
 * Created by masterung on 9/17/2017 AD.
 */

public class MainFragment extends Fragment{

    //Explicit
    private String nameString, genderString;
    private boolean genderABoolean = true;
    private int indexAnInt = 0;
    private String[] provinceStrings = new String[]{
            "โปรดเลือกจังหวัด",
            "กรุงเทพ",
            "กรุงศรี",
            "กรุงธน",
            "กรุงไทย"};



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //AddData Controller
        addDataController();

        //Radio Controller
        radioController();

        //Spinner Controller
        spinnerController();

        createListView();

    }

    private void spinnerController() {
        Spinner spinner = getView().findViewById(R.id.spnProvince);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                provinceStrings
        );
        spinner.setAdapter(stringArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                indexAnInt = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                indexAnInt = 0;
            }
        });

    }

    private void radioController() {
        RadioGroup radioGroup = getView().findViewById(R.id.ragGender);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                genderABoolean = false;
                switch (i) {
                    case R.id.radMale:
                        genderString = "Male";
                        break;
                    case R.id.radFemale:
                        genderString = "Female";
                        break;
                }
            }
        });
    }

    private void addDataController() {
        Button button = getView().findViewById(R.id.btnAddData);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get Value From Edittext
                EditText editText = getView().findViewById(R.id.edtName);
                nameString = editText.getText().toString().trim();

                //Check Space
                if (nameString.equals("")) {
                    //Have Space
                    MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
                    myAlertDialog.myDialog("Have Space", "Please Fill All Blank");
                } else if (genderABoolean) {
                    //Non Choose Gender
                    MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
                    myAlertDialog.myDialog("Non Choose Gender",
                            "Please Choose Male or Female ?");
                } else if (indexAnInt == 0) {
                    MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
                    myAlertDialog.myDialog(getResources().getString(R.string.title),
                            getResources().getString(R.string.message));
                } else {
                    MyManager myManager = new MyManager(getActivity());
                    myManager.addNameToSQLite(
                            nameString,
                            genderString,
                            provinceStrings[indexAnInt]);

                    //Create ListView
                    createListView();


                }   // if




            }   // onClick
        });
    }

    private void createListView() {

        try {

            SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase(
                    MyOpenHelper.database_name,
                    Context.MODE_PRIVATE,
                    null
            );
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM nameTABLE", null);
            cursor.moveToFirst();
            String[] nameStrings = new String[cursor.getCount()];
            String[] genderStrings = new String[cursor.getCount()];
            String[] provinceStrings = new String[cursor.getCount()];

            for (int i=0; i<cursor.getCount(); i+=1) {

                nameStrings[i] = cursor.getString(1);
                genderStrings[i] = cursor.getString(2);
                provinceStrings[i] = cursor.getString(3);
                Log.d("17SepV1", "Name[" + i + "] ==> " + nameStrings[i]);
                cursor.moveToNext();
            }   // for
            ListView listView = getView().findViewById(R.id.livName);
            ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    nameStrings
            );
            listView.setAdapter(stringArrayAdapter);



        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}   // Main Class
