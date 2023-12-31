package edu.wm.cs.cs425.helloworld;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Leave_Review extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText reviewText;
    private Button submit;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    double avgRating;

    double oldRating;
    double reviewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        initializeViews();
        db = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        String food = extras.getString("food");
        String location = extras.getString("location");
        String calories = extras.getString("calories");
        double rating = (double) extras.getInt("rating");
        String diningHall = extras.getString("diningHall");
        int pic = extras.getInt("image");
        String corlocation = location.replace("/", " or ");
        Log.d(TAG, corlocation);
        TextView foodT = findViewById(R.id.Sampletxt1), locationT = findViewById(R.id.Sampletxt);
        foodT.setText(food);
        locationT.setText(corlocation);

        ImageView image = findViewById(R.id.review_pic);
        image.setImageResource(R.drawable.food);

        submit.setOnClickListener(view -> {
            String text = reviewText.getText().toString();

            Review review = new Review(rating, text, food, corlocation, calories, diningHall);
            uploadReview(review);
            finish();
        });
    }

    public void initializeViews() {
        reviewText = findViewById(R.id.inputText);
        submit = findViewById(R.id.submitButton);
    }

    public void uploadReview(Review review) {
        Map<String, Object> foodReview = new HashMap<>();
        foodReview.put("location", review.getLocation());
        Log.d("banana1", review.getDiningLocation());
        foodReview.put("food", review.getFood());
        foodReview.put("username", getName());
        foodReview.put("text", review.getText());
        foodReview.put("rating", review.getRating());
        foodReview.put("calories",review.getCalories());
        foodReview.put("diningHall" ,review.getDiningLocation());

        db.collection("Reviews")
                .document(review.getDiningLocation())
                .collection(review.getLocation())
                .document(review.getFood())
                .collection("reviews")
                .document(user.getUid())
                .set(foodReview)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "document worked");
                    }
                });

        DocumentReference rateRef = db.collection("Reviews")
                .document(review.getDiningLocation())
                .collection(review.getLocation())
                .document(review.getFood());
        rateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "document retrieved");
                    if (document.exists()){
                        if(document.contains("rating")){
                            oldRating = (double) document.get("rating");
                            Log.d(TAG, String.valueOf(oldRating));
                        } else{
                            avgRating = review.getRating();
                        }

                        if(document.contains("revCount")){
                            reviewCount = (double) document.get("revCount");
                            Log.d(TAG, "oldReviewCount");
                            Log.d(TAG, String.valueOf(reviewCount));
                        } else{
                            reviewCount = 0;
                        }
                    }
                }
                Log.d(TAG, String.valueOf(reviewCount));
                Map<String, Object> ratingStore = new HashMap<>();
                avgRating = ((oldRating*reviewCount) + review.getRating())/(reviewCount+1);
                ratingStore.put("rating", avgRating);
                ratingStore.put("revCount", reviewCount+1);
                db.collection("Reviews")
                        .document(review.getDiningLocation())
                        .collection(review.getLocation())
                        .document(review.getFood())
                        .set(ratingStore)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "rating worked");
                            }
                        });
            }
        });



        /***
        db.collection("Reviews")
                .document(review.getLocation())
                .collection(review.getFood())
                .document(user.getUid())
                .set(review.getText())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
         ***/
    }

    private String getName(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }

}