package com.example.heal;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

public class GuideStepsActivity extends AppCompatActivity {
    private GestureDetectorCompat gestureDetectorCompat;
    private int[] medicineNames, descriptions, images;
    private TextView medicineName, medicineDescription, medicineIndex;
    private ImageView medicineImage;
    byte index = 0, TOTAL_MEDICINES;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitle(getResources().getStringArray(R.array.medicalProblems)[GuideController.index]);
        medicineNames = GuideController.getMedicines();
        descriptions = GuideController.getDescriptions();
        images = GuideController.getImagesId();
        TOTAL_MEDICINES = (byte) medicineNames.length;
        setContentView(R.layout.activity_guide_steps);
        gestureDetectorCompat = new GestureDetectorCompat(this, new Gesture());
        /*findViewById(R.id.onlineBuyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyOnline(view);
            }
        });*/
        medicineName = (TextView) findViewById(R.id.medicineName);
        medicineDescription = (TextView) findViewById(R.id.medicineDescription);
        medicineImage = (ImageView) findViewById(R.id.medicineImage);
        medicineIndex = (TextView) findViewById(R.id.indexView);
        fillData(index);

    }
    public boolean onTouchEvent(MotionEvent motionEvent){
        this.gestureDetectorCompat.onTouchEvent(motionEvent);
        return super.onTouchEvent(motionEvent);
    }
    void fillData(byte i){
        medicineName.setText(medicineNames[i]);
        medicineDescription.setText(descriptions[i]);
        medicineImage.setImageResource(images[i]);

        medicineIndex.setText((i+1)+"/"+TOTAL_MEDICINES);
    }
    class Gesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(e2.getX()<e1.getX()){
                if(index<TOTAL_MEDICINES-1){
                    index++;
                    fillData(index);
                }
            }
            else if(e2.getX()>e1.getX()) {
                if (index>0){
                    index--;
                    fillData(index);
                }
            }

            return true;
        }
    }
}
