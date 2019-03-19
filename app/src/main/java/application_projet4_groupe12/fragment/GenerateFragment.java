package application_projet4_groupe12.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import application_projet4_groupe12.R;
import application_projet4_groupe12.utils.CodeGenerator;
import application_projet4_groupe12.utils.Encryption;

public class GenerateFragment extends Fragment {

    private Activity mActivity;
    private Context mContext;

    private EditText inputText;
    private ImageView outputBitmap;
    private ImageButton switcher;

    private static final int TYPE_QR = 0, TYPE_BAR = 1;
    private static int TYPE = TYPE_QR;

    private Bitmap output;
    private String inputStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qrgenerate, container, false);


        initView(rootView);
        initFunctionality();
        initListener();

        return rootView;
    }

    private void initVar() {
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();


    }

    private void initView(View rootView) {
        inputText = rootView.findViewById(R.id.inputText);
        outputBitmap = rootView.findViewById(R.id.outputBitmap);

    }

    private void initFunctionality() {


    }

    private void initListener() {
        inputText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    String encryptedQrCode = Encryption.encryptQrCode(s.toString());
                    generateCode(encryptedQrCode);
                } else {
                    if(TYPE == TYPE_QR) {
                        outputBitmap.setImageResource(R.drawable.qr_placeholder);
                    } else {
                        outputBitmap.setImageResource(R.drawable.ic_bar_placeholder);
                    }
                }
            }
        });
    }

    private void generateCode(final String input) {
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.generateQRFor(input);
        codeGenerator.setResultListener(new CodeGenerator.ResultListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                //((BitmapDrawable)outputBitmap.getDrawable()).getBitmap().recycle();
                output = bitmap;
                inputStr = input;
                outputBitmap.setImageBitmap(bitmap);

            }
        });
        codeGenerator.execute();
    }

}
