package com.example.firestore_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";

    private EditText title;
    private EditText description;
    private Button save_button;
    private Button loadContent;
    private Button updateContent;
    private TextView content;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private DocumentReference database_reference = database.document("NoteBook/Note 1");

    @Override
    protected void onStart() {
        super.onStart();
        database_reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                Toast.makeText(MainActivity.this, "Data Fetch Failed !", Toast.LENGTH_SHORT).show();
                return;
                }
                if(documentSnapshot.exists()){
                    String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);

                    content.setText("Title: " + title + "\n Description: " + description);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeWidgets();
    }

    public void initializeWidgets(){

        title = findViewById(R.id.ContentTitle);
        description = findViewById(R.id.ContentDescription);
        save_button = findViewById(R.id.button_save);
        loadContent = findViewById(R.id.button_loadContent);
        updateContent = findViewById(R.id.button_update_content);
        content = findViewById(R.id.text_content);

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_note();
            }
        });

        loadContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_content();
            }
        });

        updateContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_content();
            }
        });

    }

    public void save_note(){

        String title = this.title.getText().toString();
        String description = this.description.getText().toString();

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_TITLE, title);
        note.put(KEY_DESCRIPTION, description);

        database_reference.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note Saved !", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                     public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error !", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
            }
        });

    }

    public void load_content(){
        database_reference
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){
                            String title = documentSnapshot.getString(KEY_TITLE);
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);

                            content.setText("Title: " + title + "\n Description: " + description);
                        }else {
                            Toast.makeText(MainActivity.this, "Note does not exist ! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Note Unavailable", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void update_content(){
        String description = this.description.getText().toString();

        Map<String, Object> note  = new HashMap<>();

        note.put(KEY_DESCRIPTION, description);
    }
}
