package application_projet4_groupe12.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.CodeGenerator;
import application_projet4_groupe12.utils.Encryption;
import application_projet4_groupe12.utils.Global;

public class GenerateFragment extends Fragment {

    private Activity mActivity;
    private Context mContext;

    private EditText inputText;
    private ImageView outputBitmap;
    private ImageButton switcher;

    private static final int TYPE_QR = 0;
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
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    Long create_time = Encryption.GetUnixTime();
                    String qr_content = s.toString() + "_5%/" + getPartnerId() + "_5%/" + create_time;
                    Log.v(Global.debug_text, "create tule at generate" + qr_content);
                    String encryptedQrCode = Encryption.encryptQrCode(qr_content);

                    generateCode(encryptedQrCode);
                } else {
                    if (TYPE == TYPE_QR) {
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
        codeGenerator.setResultListener(bitmap -> {
            output = bitmap;
            inputStr = input;
            outputBitmap.setImageBitmap(bitmap);
        });
        codeGenerator.execute();
    }

    private Long getPartnerId() {
        SQLHelper db = null;
        Long partnerId = null;
        Long shopId = null;
        try {

            db = new SQLHelper(getContext());
            partnerId = db.getPartnerIDFromUser(User.connectedUser.getId());

        } catch (IOException e) {
            Log.i(Global.debug_text, "GenerateFragment : getPartnerId " + e);
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return partnerId;
    }

}
