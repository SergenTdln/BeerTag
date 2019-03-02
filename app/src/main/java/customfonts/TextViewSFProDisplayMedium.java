package
        customfonts;

import
        android.content.Context;
import
        android.graphics.Typeface;
import
        android.util.AttributeSet;
import
        android.widget.TextView;

public class TextViewSFProDisplayMedium extends android.support.v7.widget.AppCompatTextView {

    public TextViewSFProDisplayMedium(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init();
    }

    public TextViewSFProDisplayMedium(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();

    }

    public TextViewSFProDisplayMedium(Context context) {

        super(context);
        init();

    }

    private void init() {

        if
        (!isInEditMode()) {

            setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/NeoSansPro_Medium.ttf"));

        }
    }
}
