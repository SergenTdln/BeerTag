package application_projet4_groupe12.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


public class CodeGenerator extends AsyncTask<Void, Void, Bitmap>{

    public static final int QR_DIMENSION = 1080;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private ResultListener resultListener;
    private String input;

    public void generateQRFor(String input) {
        this.input = input;
    }


    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            return createQRCode(this.input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(resultListener != null) {
            resultListener.onResult(bitmap);
        }
    }

    public interface ResultListener {
        void onResult(Bitmap bitmap);
    }

    private Bitmap createQRCode(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, QR_DIMENSION, QR_DIMENSION, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, QR_DIMENSION, 0, 0, w, h);
        return bitmap;
    }


}
