package com.example.nooda.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nooda.R;
import com.example.nooda.databinding.FragmentHomeBinding;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final boolean DEBUG = false;
    private FragmentHomeBinding binding;
    private ArrayList<String> namesList = new ArrayList<>();
    private ArrayList<String> selectedList = new ArrayList<>();
    private ArrayList<String> numberList = new ArrayList<>();
    private LinearLayout selectedLinearLayout;
    private LinearLayout numberLinearLayout;
    private LinearLayout namesLinearLayout;
    private TextView addNewText;
    private EditText addNewEdit;
    private Button addNewBt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        namesLinearLayout = binding.linearLayoutNames;
        selectedLinearLayout = binding.linearLayoutSelect;
        numberLinearLayout = binding.linearLayoutNumber;

        addNewText = binding.addText;
        addNewEdit = binding.addEdit;
        addNewBt = binding.addBt;

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "SentySnowMountain.ttf");
        addNewText.setTypeface(typeface);
        addNewEdit.setTypeface(typeface);
        addNewBt.setTypeface(typeface);

        loadListsFromFile();

        if (namesList.size()==0) {
            namesList.add("大一");
            namesList.add("多多");
            namesList.add("祥志");
            namesList.add("美身");
        }

        updateNamesList();
        updateNumberList();
        updateSelectedList();

        addNewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addNewEdit.getText().toString().equals("")) {
                    namesList.add(addNewEdit.getText().toString());
                } else {
                    showEmptyNameDialog();
                }
                updateNamesList();
            }
        });
    }

    private void showEmptyNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("空白客戶名")
                .setMessage("客戶名字不能空白")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void updateNamesList() {
        namesLinearLayout.removeAllViews();
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "SentySnowMountain.ttf");

        for (int i = 0; i < namesList.size(); i++) {
            Button button = new Button(getContext());
            button.setText(namesList.get(i));
            button.setTextColor(Color.GREEN);
            button.setBackgroundColor(getContext().getColor(R.color.alpha));
            button.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            button.setTypeface(typeface, Typeface.BOLD);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedList.add(button.getText().toString());
                    numberList.add("0");
                    updateSelectedList();
                    updateNumberList();
                }
            });

            button.setMaxLines(1);

            // Set button size
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = 200; // Height in pixels, set the desired size
            button.setLayoutParams(layoutParams);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                button.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                button.setAutoSizeTextTypeUniformWithConfiguration(
                        5, // Minimum text size in sp
                        100, // Maximum text size in sp
                        5, // Step size (granularity) in sp
                        TypedValue.COMPLEX_UNIT_SP);

            } else {
                // For older Android versions, you can use the AutoScaleTextView library
            }
            namesLinearLayout.addView(button);
        }
    }

    public void updateSelectedList() {
        selectedLinearLayout.removeAllViews();

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "SentySnowMountain.ttf");


        for (int i = 0; i < selectedList.size(); i++) {
            Button button = new Button(getContext());
            button.setText(selectedList.get(i));
            button.setTypeface(typeface, Typeface.BOLD);
            button.setClickable(false);
            button.setMaxLines(1);

            // Set button size
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = 200; // Height in pixels, set the desired size
            button.setLayoutParams(layoutParams);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                button.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                button.setAutoSizeTextTypeUniformWithConfiguration(
                        5, // Minimum text size in sp
                        100, // Maximum text size in sp
                        5, // Step size (granularity) in sp
                        TypedValue.COMPLEX_UNIT_SP);

            } else {
                // For older Android versions, you can use the AutoScaleTextView library
            }
            button.setBackgroundColor(getContext().getColor(R.color.alpha));
            selectedLinearLayout.addView(button);
        }
    }

    public void updateNumberList() {
        numberLinearLayout.removeAllViews();

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "SentySnowMountain.ttf");

        for (int i = 0; i < numberList.size(); i++) {
            Button button = new Button(getContext());
            button.setText(numberList.get(i));
            button.setTextColor(Color.BLUE);
            button.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            button.setMaxLines(1);
            button.setTypeface(typeface, Typeface.BOLD);
            // Set button size
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = 200; // Height in pixels, set the desired size
            button.setLayoutParams(layoutParams);
            button.setBackgroundColor(getContext().getColor(R.color.alpha));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                button.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                button.setAutoSizeTextTypeUniformWithConfiguration(
                        5, // Minimum text size in sp
                        100, // Maximum text size in sp
                        5, // Step size (granularity) in sp
                        TypedValue.COMPLEX_UNIT_SP);

            } else {
                // For older Android versions, you can use the AutoScaleTextView library
            }
            int index = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Enter number");

                    final EditText input = new EditText(getContext());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String enteredText = input.getText().toString();
                            button.setText(enteredText);
                            numberList.set(index, enteredText);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // Auto-focus the input and show the keyboard
                    input.requestFocus();
                    AlertDialog alertDialog = builder.show();
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                }
            });

            numberLinearLayout.addView(button);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        saveListsToFile();
        super.onPause();
    }

    private void saveListsToFile() {
        Log.d(TAG, "saveListsToFile");
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadFolder, "file.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Write namesList
            bufferedWriter.write("namesList:");
            for (String name : namesList) {
                Log.d(TAG, "save namesList:" + name );
                bufferedWriter.write(name + ",");
            }
            bufferedWriter.newLine();

            // Write selectedList
            bufferedWriter.write("selectedList:");
            for (String selected : selectedList) {
                bufferedWriter.write(selected + ",");
            }
            bufferedWriter.newLine();

            // Write numberList
            bufferedWriter.write("numberList:");
            for (String number : numberList) {
                bufferedWriter.write(number + ",");
            }
            bufferedWriter.newLine();

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListsFromFile() {
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadFolder, "file.txt");

            if (!file.exists()) {
                Log.e("loadListsFromFile", "The file does not exist");
                return;
            }

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read and parse namesList
            String line = bufferedReader.readLine();
            if (line != null && line.startsWith("namesList:")) {
                namesList = parseListFromLine(line, "namesList:");
            }

            // Read and parse selectedList
            line = bufferedReader.readLine();
            if (line != null && line.startsWith("selectedList:")) {
                selectedList = parseListFromLine(line, "selectedList:");
            }

            // Read and parse numberList
            line = bufferedReader.readLine();
            if (line != null && line.startsWith("numberList:")) {
                numberList = parseListFromLine(line, "numberList:");
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> parseListFromLine(String line, String prefix) {
        String[] parts = line.substring(prefix.length()).split(",");
        ArrayList<String> list = new ArrayList<>();
        for (String part: parts) {
            if (part != "") {
                list.add(part);
            }
        }

        if (DEBUG) {
            for (int i = 0; i < list.size(); i++) {
                Log.d(TAG, "List:" + i + " :"+ list.get(i));
            }
        }
        return list;
    }

}