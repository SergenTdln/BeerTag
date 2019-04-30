package application_projet4_groupe12.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Code for this class was found on StackOverflow.com
 * @author Jaydipsinh Zala
 */
public class CustomListView extends ListView {

    public CustomListView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public CustomListView(Context context){
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
