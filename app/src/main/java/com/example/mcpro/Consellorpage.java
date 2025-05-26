package com.example.mcpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Consellorpage extends AppCompatActivity {
    CheckBox Anxiety;
    CheckBox Addiction;
    CheckBox Abuse;
    CheckBox Academics;
    CheckBox Depression;
    CheckBox General;
    CheckBox GriefLoss;
    Button sub;

    ArrayList<String>issues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consellorpage);
        //FINDING ID
        issues = new ArrayList<>();
        Anxiety=findViewById(R.id.AnxietyID);
        Addiction=findViewById(R.id.AddictionID);
        Abuse=findViewById(R.id.AbuseID);
        Academics=findViewById(R.id.AcademicID);
        Depression=findViewById(R.id.DeprissionID);
        General=findViewById(R.id.GeneralID);
        GriefLoss=findViewById(R.id.Grief_LossID);
        sub=findViewById(R.id.consellorsubbtn);


        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issues = new ArrayList<>();
                //CHECKING STATE
                Boolean checkAnxietyState=Anxiety.isChecked();
                Boolean checkAddiction=Addiction.isChecked();
                Boolean checkAbuseState=Abuse.isChecked();
                Boolean checkAcademicsState=Academics.isChecked();
                Boolean checkDepressionState=Depression.isChecked();
                Boolean checkGeneralState=General.isChecked();
                Boolean checkGriefLossState=GriefLoss.isChecked();
//                Intent i=new Intent(getApplicationContext(), ChatActivity.class);
//                startActivity(i);
                if(checkAnxietyState){
                    issues.add( Anxiety.getText().toString());
                }
                if(checkAddiction){
                    issues.add( Addiction.getText().toString());
                }
                if(checkAbuseState){
                    issues.add( Abuse.getText().toString());
                }
                if(checkAcademicsState){
                    issues.add( Academics.getText().toString());
                }
                if(checkDepressionState){
                    issues.add( Depression.getText().toString());
                }
                if(checkGeneralState){
                    issues.add( General.getText().toString());
                }
                if(checkGriefLossState){
                    issues.add(GriefLoss.getText().toString());
                }
                System.out.println(issues);

            }
        });
    }
}