package com.example.gpsk1.qrcode;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog extends Dialog{
    private Context context;

    public CustomDialog(Context context) {
        super(context);
        //this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final String text) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final TextView txt = (TextView) dlg.findViewById(R.id.txt);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        txt.setText(text);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //해당 url로 이동
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
                //intent.setPackage("com.android.chrome");
                //startActivity(intent);
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //메인화면
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();

            }
        });
    }
}
