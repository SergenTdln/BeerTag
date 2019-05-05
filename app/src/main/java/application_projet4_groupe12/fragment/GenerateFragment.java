package application_projet4_groupe12.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application_projet4_groupe12.R;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.Address;
import application_projet4_groupe12.entities.Partner;
import application_projet4_groupe12.entities.Shop;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.utils.CodeGenerator;
import application_projet4_groupe12.utils.Encryption;
import application_projet4_groupe12.utils.Global;

public class GenerateFragment extends Fragment {

    private Context mContext;

    private EditText inputText;
    private ImageView outputBitmap;
    private Spinner spinner;

    Partner currPartner;

    private static final int TYPE_QR = 0;
    private static int TYPE = TYPE_QR;

    private Bitmap output;
    private String inputStr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVar();

        currPartner = User.connectedUser.getAdministratedPartner(mContext);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qrgenerate, container, false);

        initView(rootView);
        initFunctionality();
        initListener();

        return rootView;
    }

    private void initVar() {
        Activity mActivity = getActivity();
        if(mActivity!=null) {
            mContext = mActivity.getApplicationContext();
        }
    }

    private void initView(View rootView) {
        inputText = rootView.findViewById(R.id.inputText);
        outputBitmap = rootView.findViewById(R.id.outputBitmap);
        spinner = (Spinner) rootView.findViewById(R.id.fragment_generate_spinner);
        fillSpinner(spinner, mContext);
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
                long shopID = getShopId();
                if(shopID!=-1) {
                    spinner.setBackgroundResource(0); //Remove the potential red border
                    if (s.length() != 0) {
                        Long create_time = Encryption.GetUnixTime();
                        String qr_content = s.toString() + "_5%/" + shopID + "_5%/" + create_time;
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
                } else {
                    spinner.setBackgroundResource(R.drawable.border_error);
                    Toast.makeText(mContext, "Please select a shop first", Toast.LENGTH_SHORT).show();
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

    private long getShopId() {
        String selectedInSpinner = (String) spinner.getSelectedItem();
        if(selectedInSpinner!=null && !selectedInSpinner.equals("") && !selectedInSpinner.equals(getString(R.string.generate_fragment_spinner_default))){
            return Long.parseLong(selectedInSpinner);
        } else {
            return -1;
        }
    }

    private void fillSpinner(Spinner spinner, Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            List<Long> shopsIDs = db.getAllShopsIDs(currPartner.getId());
            List<String> shopsIDsAsStrings= new ArrayList<String>();
            shopsIDsAsStrings.add(0, getString(R.string.generate_fragment_spinner_default));
            for (Long id : shopsIDs) {
                shopsIDsAsStrings.add(String.valueOf(id));
            }

            ArrayAdapter adapter = new ArrayAdapter<>(mContext, R.layout.spinner_adapter_plain_text, shopsIDsAsStrings);
            spinner.setAdapter(adapter);
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(c, "An error occurred; we could not update the content of the drop-down menu", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }
}
