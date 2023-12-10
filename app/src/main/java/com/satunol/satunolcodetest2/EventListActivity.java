package com.satunol.satunolcodetest2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.satunol.satunolcodetest2.Adapter.EventAdapter;
import com.satunol.satunolcodetest2.Model.Events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EventListActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase fd;
    DatabaseReference drEvents;
    ShapeableImageView profilePict;
    MaterialTextView nameTv, emailTv;
    MaterialButton btnSignOut, btnSave;
    TextInputEditText inputTitle, inputDescription;
    TextView inputDate, inputStart, inputEnd;
    GoogleSignInClient googleSignInClient;
    LinearLayout formLinearLayout;
    RecyclerView eventRv;
    ArrayList<Events> listEvent;
    EventAdapter eventAdapter;

//    private GoogleAccountCredential credential;

    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int hour, minute;
    String startTime, endTime;
    Date start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_event_list);

        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        profilePict = findViewById(R.id.profilePicture);
        btnSignOut = findViewById(R.id.btnSignOut);

        formLinearLayout = findViewById(R.id.form_ll);

        inputTitle = findViewById(R.id.input_title);
        inputDescription = findViewById(R.id.input_description);
        inputDate = findViewById(R.id.input_date);
        inputStart = findViewById(R.id.start_time);
        inputEnd = findViewById(R.id.end_time);
        btnSave = findViewById(R.id.button_save_event);
        eventRv = findViewById(R.id.event_rv);

        firebaseAuth = FirebaseAuth.getInstance();
        fd = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        drEvents = fd.getReference("Users/" + firebaseAuth.getUid()).child("Events");

        if (firebaseUser != null) {
            nameTv.setText(firebaseUser.getDisplayName());
            emailTv.setText(firebaseUser.getEmail());
            Glide.with(EventListActivity.this).load(firebaseUser.getPhotoUrl()).circleCrop().into(profilePict);
        }

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(EventListActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        btnSignOut.setOnClickListener(v -> googleSignInClient.signOut().addOnCompleteListener(task -> {
            // Check condition
            if (task.isSuccessful()) {
                // When task is successful sign out from firebase
                firebaseAuth.signOut();
                // Display Toast
                Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                // Finish activity
                finish();
            }
        }));

//        formLinearLayout.setVisibility(View.GONE);

        inputDate.setOnClickListener(this::inputDateHandler);
        inputStart.setOnClickListener(this::inputStartHandler);
        inputEnd.setOnClickListener(this::inputEndHandler);
        btnSave.setOnClickListener(v -> saveEventHandler());

        getEvents();

    }


    private void saveEventHandler() {
        if (!isComplete()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
        } else {
            storeToDatabase();
        }
    }

    private void storeToDatabase() {
        Events events = new Events();
        events.setDate(inputDate.getText().toString());
        events.setStart(inputStart.getText().toString());
        events.setEnd(inputEnd.getText().toString());
        events.setTitle(Objects.requireNonNull(inputTitle.getText()).toString());
        events.setDescription(Objects.requireNonNull(inputDescription.getText()).toString());

        // Push a new event to the "Events" node
        String eventId = drEvents.push().getKey();

        // Set the values of the event to the "Events" node
        assert eventId != null;
        drEvents.push().setValue(events).addOnCompleteListener(Task -> {
            Toast.makeText(this, "Stored successfully.", Toast.LENGTH_SHORT).show();
            inputTitle.setText("");inputDescription.setText("");inputDate.setText("Date");inputStart.setText("Start");inputEnd.setText("End");
        }).addOnFailureListener(Task -> Toast.makeText(this, "Store failed.", Toast.LENGTH_SHORT).show());

    }

    private boolean isComplete() {
        return isEditTextFilled(inputTitle) &&
                isEditTextFilled(inputDescription) &&
                isTextFilled(inputDate) &&
                isTextFilled(inputStart) &&
                isTextFilled(inputEnd);
    }

    private boolean isEditTextFilled(TextInputEditText editText) {
        // Check if the given EditText has a non-empty value
        return Objects.requireNonNull(editText.getText()).toString().trim().length() > 0;
    }

    private boolean isTextFilled(TextView textView) {
        // Check if the given EditText has a non-empty value
        return Objects.requireNonNull(textView.getText()).toString().trim().length() > 0;
    }

    public void inputDateHandler(View v) {
        // on below line we are getting the instance of our calendar.
        final Calendar c = Calendar.getInstance();

        // on below line we are getting our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.
                        inputDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                },
                // on below line we are passing year, month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to display our date picker dialog.
        datePickerDialog.show();
    }

    private void inputStartHandler(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            minute = selectedMinute;
            inputStart.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            startTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            if (inputStart.length() != 0 && inputEnd.length() != 0) {
                convertStartEnd();
            }
        };

        // int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void inputEndHandler(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            minute = selectedMinute;
            inputEnd.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            endTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

            if (inputStart.length() != 0 && inputEnd.length() != 0) {
                convertStartEnd();
            }
        };

        // int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void convertStartEnd() {
        String dtStart = startTime;
        String dtEnd = endTime;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            start = format.parse(dtStart);
            end = format.parse(dtEnd);
            assert end != null;
            if (end.before(start)) { //Same way you can check with after() method also.
                inputEnd.setText(""); inputStart.setText("");
                Toast.makeText(this, "Invalid end time", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("valerie", "true");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void getEvents() {
        DatabaseReference eventDr = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(firebaseAuth.getUid())).child("Events");
        eventDr.orderByChild("Date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listEvent = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Events events = dataSnapshot.getValue(Events.class);
                        listEvent.add(events);
                        Log.d("valerie", events.getTitle() + " | " + events.getDescription() + " | " + events.getDate() + " | " + events.getStart() + " | " + events.getEnd());
                    }
                    EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), listEvent);
                    eventRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    eventRv.setAdapter(eventAdapter);
                } else {
                    Log.d("valerie", "data doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}