package com.example.nooda.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<String> indexList = new ArrayList<>();
    private ArrayList<String> namesList = new ArrayList<>();
    private ArrayList<String> selectedList = new ArrayList<>();
    private ArrayList<String> numberList = new ArrayList<>();
    private LinearLayout indexLinearLayout;
    private LinearLayout selectedLinearLayout;
    private LinearLayout numberLinearLayout;
    private LinearLayout namesLinearLayout;
    private TextView addNewText;
    private EditText addNewEdit;
    private Button addNewBt;
    private Button delBt;
    private long startTime;
    private Handler backgroundHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        indexLinearLayout = binding.linearLayoutIndex;
        namesLinearLayout = binding.linearLayoutNames;
        selectedLinearLayout = binding.linearLayoutSelect;
        numberLinearLayout = binding.linearLayoutNumber;

        addNewText = binding.addText;
        addNewEdit = binding.addEdit;
        addNewBt = binding.addBt;
        delBt = binding.delBt;

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "SentySnowMountain.ttf");
        addNewText.setTypeface(typeface);
        addNewEdit.setTypeface(typeface);
        addNewBt.setTypeface(typeface);
        delBt.setTypeface(typeface);

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

        delBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            //button.setTextColor(Color.GREEN);
            button.setBackground(getContext().getDrawable(R.drawable.border));
            //button.setBackgroundColor(getContext().getColor(R.color.alpha));
            //button.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            button.setTypeface(typeface);
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

            int index = i;
            Button button = new Button(getContext());
            button.setText(selectedList.get(i));
            button.setTypeface(typeface);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // Start counting the time when the button is pressed
                            startTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            // Calculate the duration of the press when the button is released
                            long duration = System.currentTimeMillis() - startTime;

                            if (duration < 500) {
                                // Short press
                                onShortPress(view);
                            } else {
                                // Long press
                                onLongPress(view, index);
                            }
                            break;
                    }
                    return true; // Indicate that the event is consumed
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
           // button.setTextColor(getContext().getColor(R.color.pencil_gray));
            //button.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            button.setMaxLines(1);
            button.setTypeface(typeface);
            // Set button size
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = 200; // Height in pixels, set the desired size
            button.setLayoutParams(layoutParams);
            button.setBackgroundColor(getContext().getColor(R.color.alpha));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                button.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                button.setAutoSizeTextTypeUniformWithConfiguration(
                        5, // Minimum text size in sp
                        50, // Maximum text size in sp
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

    // Inside an activity or fragment
    private void showDialogAndRemove(String message, int removeIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);

        // Set the OK button action
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform any actions you want to execute when the OK button is clicked
                numberList.remove(removeIndex);
                selectedList.remove(removeIndex);
                updateNumberList();
                updateSelectedList();
            }
        });

        // Set the Cancel button action
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform any actions you want to execute when the Cancel button is clicked
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void onShortPress(View view) {
        // Handle short press action

    }

    private void onLongPress(View view, int removeIndex) {
        // Handle long press action
        showDialogAndRemove("確定刪除第" + (removeIndex+1) + "列?", removeIndex);
    }

    private void initHandler() {
        HandlerThread handlerThread = new HandlerThread("UpdateSelectedListThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                updateSelectedList();
            }
        };
    }

}