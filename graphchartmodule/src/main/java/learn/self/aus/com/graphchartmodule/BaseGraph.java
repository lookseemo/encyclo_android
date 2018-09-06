package learn.self.aus.com.graphchartmodule;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Jack on 2/6/2018.
 */

public abstract class BaseGraph extends View {

    protected enum GraphType{
        BarChart,
        LineChart,
        CubicLineChart
    }
    public BaseGraph(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public BaseGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public BaseGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public BaseGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
    }

    @Override
    protected abstract void onMeasure(int widthMeasureSpec, int heightMeasureSpec);

    @Override
    protected abstract void onDraw(Canvas canvas);

    public abstract void setData(ArrayList<LinePoint> dataList);
    public abstract ArrayList<LinePoint> getData();
    public abstract void appendData(ArrayList<LinePoint> entry);
    public abstract void removeData(LinePoint entry, int index);
    public abstract void setGraphType(GraphType graphType);
    public abstract GraphType getGraphType();
}
