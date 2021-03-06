package com.iflytek.nursing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.iflytek.medicalsdk_nursing.NursingListener;
import com.iflytek.medicalsdk_nursing.NursingSpeecher;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;

public class MainActivity extends AppCompatActivity {

    private Button recordButton;
    private NursingSpeecher nursingSpeecher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String patientStr = "";
        //传递患者数据
        IFlyNursing.getInstance().savePatientInfo(patientStr);

        String documentStr ="";
        IFlyNursing.getInstance().saveDocumentInfo(documentStr);

        String documentDetailStr = "";
        IFlyNursing.getInstance().saveDocumentDetailInfo(documentDetailStr);

        String optionStr = "";
        IFlyNursing.getInstance().saveOptionInfo(optionStr);


        nursingSpeecher = new NursingSpeecher(this);
        nursingSpeecher.setNursingListener(new NursingListener() {
            @Override
            public void onStartListener(boolean success) {

            }

            @Override
            public void onDataSavedListener(String result) {

            }
        });
        recordButton = (Button) findViewById(R.id.main_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nursingSpeecher.startRecord();
            }
        });
    }
}
