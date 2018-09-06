package learn.self.aus.com.graphchartmodule;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;

public class LineGraph extends BaseGraph {

    private static final int LINE_VER_PADDING = 100;
    private static final int HALF_NUM_LINES = 4;
    private static final int TEXT_PADDING = 15;
    private static final int MAX_LINE_ARRAY = 40;

    private ArrayList<LinePoint> entries; // Total Data
    private ArrayList<LinePoint> posEntries; // pos points
    private ArrayList<LinePoint> negEntries; // neg points
    private ArrayList<LinePoint> linePoints; // ticks
    private int gridLineColor;
    private int textColour;
    private int positiveLineColor;
    private int negativeLineColor;
    private int posUnderLineColor;
    private int negUnderLineColor;
    private int backgroundColor;
    private LinearGradient posGradient;
    private LinearGradient negGradient;

    private Paint mPaint;
    private Path posPath;
    private Path negPath;

    private int lineSpacing;

    private LinePoint baseLinePoint;

    public LineGraph(Context context) {
        super(context);
        setWillNotDraw(false);
        this.init();
    }

    public LineGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LineGraph);
        this.gridLineColor = typedArray.getColor(R.styleable.LineGraph_grid_line_color, Color
                .WHITE);
        this.textColour = typedArray.getInt(R.styleable.LineGraph_text_colour,
                Color.WHITE);
        this.positiveLineColor = typedArray.getInt(R.styleable.LineGraph_positive_line_color,
                Color.BLUE);
        this.negativeLineColor = typedArray.getInt(R.styleable.LineGraph_negative_line_color,
                Color.RED);
        this.posUnderLineColor = typedArray.getColor(R.styleable
                .LineGraph_positive_under_line_color, Color.BLUE);
        this.negUnderLineColor = typedArray.getInt(R.styleable
                .LineGraph_negative_under_line_color, Color.RED);
        this.backgroundColor = typedArray.getColor(R.styleable.LineGraph_background_graph_color,
                Color.LTGRAY);
        typedArray.recycle();
        this.init();
    }

    public LineGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LineGraph);
        this.gridLineColor = typedArray.getColor(R.styleable.LineGraph_grid_line_color, Color
                .WHITE);
        this.textColour = typedArray.getInt(R.styleable.LineGraph_text_colour,
                Color.WHITE);
        this.positiveLineColor = typedArray.getInt(R.styleable.LineGraph_positive_line_color,
                Color.BLUE);
        this.negativeLineColor = typedArray.getInt(R.styleable.LineGraph_negative_line_color,
                Color.RED);
        this.posUnderLineColor = typedArray.getColor(R.styleable
                .LineGraph_positive_under_line_color, Color.BLUE);
        this.negUnderLineColor = typedArray.getInt(R.styleable
                .LineGraph_negative_under_line_color, Color.RED);
        this.backgroundColor = typedArray.getColor(R.styleable.LineGraph_background_graph_color,
                Color.LTGRAY);
        typedArray.recycle();
        this.init();
    }

    public LineGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LineGraph);
        this.gridLineColor = typedArray.getColor(R.styleable.LineGraph_grid_line_color, Color
                .WHITE);
        this.textColour = typedArray.getInt(R.styleable.LineGraph_text_colour,
                Color.WHITE);
        this.positiveLineColor = typedArray.getInt(R.styleable.LineGraph_positive_line_color,
                Color.BLUE);
        this.negativeLineColor = typedArray.getInt(R.styleable.LineGraph_negative_line_color,
                Color.RED);
        this.posUnderLineColor = typedArray.getColor(R.styleable
                .LineGraph_positive_under_line_color, Color.BLUE);
        this.negUnderLineColor = typedArray.getInt(R.styleable
                .LineGraph_negative_under_line_color, Color.RED);
        this.backgroundColor = typedArray.getColor(R.styleable.LineGraph_background_graph_color,
                Color.LTGRAY);
        typedArray.recycle();
        this.init();
    }

    private void init() {
        mPaint = new Paint();
        posPath = new Path();
        negPath = new Path();
        //LINE POINT
        baseLinePoint = new LinePoint();
        lineSpacing = 0;
        linePoints = new ArrayList<>();
        entries = new ArrayList<>();
        posEntries = new ArrayList<>();
        negEntries = new ArrayList<>();

        for (int i = 0; i < (HALF_NUM_LINES * 2); i++) {
            LinePoint linePoint = new LinePoint();
            linePoints.add(linePoint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(backgroundColor);
        canvas.drawRect(canvas.getClipBounds(), mPaint);
        //calculate graph area
        int maxGraphWidth = canvas.getClipBounds().right;
        int maxGraphHeight = canvas.getClipBounds().bottom - LINE_VER_PADDING;
        int minGraphWidth = canvas.getClipBounds().left;
        int minGraphHeight = canvas.getClipBounds().top + LINE_VER_PADDING;
        int middleRect = minGraphHeight + ((maxGraphHeight - minGraphHeight) / 2);

        //calculate and draw base line
        baseLinePoint.setStartX(minGraphWidth);
        baseLinePoint.setStartY(middleRect);
        baseLinePoint.setEndX(maxGraphWidth);
        baseLinePoint.setEndY(middleRect);
        if (posGradient == null) {
            //FILL COLOUR
            posGradient = new LinearGradient(0, 0, 0, baseLinePoint.startY,
                    new int[]{getResources().getColor(R.color
                            .pos_line_fill_90), getResources().getColor(R.color
                            .pos_line_fill_75), posUnderLineColor},
                    new float[]{0.3f, 0.65f, 1f},
                    Shader.TileMode.MIRROR);
        }
        if (negGradient == null) {
            negGradient = new LinearGradient(0, 0, 0, baseLinePoint.startY,
                    new int[]{negUnderLineColor, getResources().getColor(R.color
                            .neg_line_fill_85), negativeLineColor},
                    new float[]{0.3f, 0.65f, 1f}, Shader.TileMode.MIRROR);
        }

        mPaint.setColor(this.gridLineColor);
        mPaint.setStrokeWidth(3);
        canvas.drawLine(baseLinePoint.getStartX(), baseLinePoint.getStartY(), baseLinePoint.getEndX(),
                baseLinePoint.getEndY(), mPaint);

        //calculate spacing
        lineSpacing = (baseLinePoint.startY - minGraphHeight) / (HALF_NUM_LINES);
        int labelSize = getResources().getDimensionPixelSize(R.dimen.graph_vertical_label_size);

        //draw horizontal ticks
        for (int i = 0; i < linePoints.size(); i++) {
            mPaint.reset();
            mPaint.setStrokeWidth(3);
            mPaint.setColor(this.gridLineColor);
            LinePoint linePoint = linePoints.get(i);
            linePoint.startX = baseLinePoint.startX;
            linePoint.endX = baseLinePoint.endX;
            String label;
            if (i < HALF_NUM_LINES) {
                linePoint.startY = baseLinePoint.startY - ((i + 1) * lineSpacing);
                label = ((i + 1) * 25) + "%";
            } else {
                linePoint.startY = baseLinePoint.startY + ((i + 1 - HALF_NUM_LINES) * lineSpacing);
                label = "-" + ((i + 1 - HALF_NUM_LINES) * 25) + "%";
            }

            linePoint.endY = linePoint.startY;
            canvas.drawLine(linePoint.getStartX(), linePoint.getStartY(), linePoint.getEndX(),
                    linePoint.getEndY(), mPaint);
            //draw label
            mPaint.setTextSize(labelSize);
            mPaint.setColor(textColour);
            canvas.drawText(label, (linePoint.startX + TEXT_PADDING), (linePoint.startY + labelSize), mPaint);
        }

        posPath.reset();
        negPath.reset();
        if(entries.size() <= MAX_LINE_ARRAY) {
            posPath.moveTo(baseLinePoint.startX, baseLinePoint.startY);
            negPath.moveTo(baseLinePoint.startX, baseLinePoint.startY);
        }else{
            // We need 3 points off the screen, to retain the slope between each curve, and
            // baseline point for filling colours
            int posfirstPointIdx = (entries.size() - MAX_LINE_ARRAY - 2);
            int negFirstPointIdx = (entries.size() - MAX_LINE_ARRAY - 2) + 1;
            LinePoint posStartingPoint = entries.get(posfirstPointIdx);
            LinePoint negStartingPoint = entries.get(negFirstPointIdx);

            int posSecondPoint = entries.size() - MAX_LINE_ARRAY;
            int negSecondPoint = entries.size() - MAX_LINE_ARRAY + 1;
            LinePoint posSecStartingPoint = entries.get(posSecondPoint);
            LinePoint negSecStartingPoint = entries.get(negSecondPoint);

            if(posStartingPoint.startY == 0)
                posStartingPoint.startY = (int) calculateHeights((float)posStartingPoint.value,
                        minGraphHeight, false);
            if(negStartingPoint.startY == 0)
                negStartingPoint.startY = (int) calculateHeights((float)negStartingPoint.value,
                        minGraphHeight, false);
            if(posSecStartingPoint.startY == 0)
                posSecStartingPoint.startY = (int) calculateHeights((float)posSecStartingPoint.value,
                        minGraphHeight, false);
            if(negSecStartingPoint.startY == 0)
                negSecStartingPoint.startY = (int) calculateHeights((float)negSecStartingPoint.value,
                        minGraphHeight, false);

            //get the path to the point, quad type is enough to handle.
            posPath.moveTo(baseLinePoint.startX - ((maxGraphWidth / MAX_LINE_ARRAY)*2),
                      baseLinePoint.startY);
            negPath.moveTo(baseLinePoint.startX - ((maxGraphWidth/MAX_LINE_ARRAY)*2),
                      baseLinePoint.startY);
            posPath.quadTo(baseLinePoint.startX - ((maxGraphWidth / MAX_LINE_ARRAY)),
                    posStartingPoint.startY, 0 , posSecStartingPoint.startY);
            negPath.quadTo(baseLinePoint.startX - ((maxGraphWidth / MAX_LINE_ARRAY)),
                    negStartingPoint.startY, 0, negSecStartingPoint.startY);

        }

        //separate the pos from neg points
        posEntries.clear();
        negEntries.clear();
        int index = entries.size() - MAX_LINE_ARRAY;
        if(index < 0)
            index = 0;
        for (int i = index; i < entries.size(); i++) {
            if (entries.get(i).value > 0) {
                posEntries.add(entries.get(i));
            } else if (entries.get(i).value < 0) {
                negEntries.add(entries.get(i));
//            } else {
//                if (i == entries.size() - 1) { // positive always added first
//                    negEntries.add(entries.get(i));
//                } else {
//                    posEntries.add(entries.get(i));
//                }
            }
        }

        //populate Points
        if (posEntries.size() > 0) {
            for (int i = 0; i < posEntries.size(); i++) {
                populatePointData(posEntries.get(i), i, posEntries.size(), maxGraphWidth,
                        minGraphHeight);
            }
        }
        if (negEntries.size() > 0) {
            for (int i = 0; i < negEntries.size(); i++) {
                populatePointData(negEntries.get(i), i, negEntries.size(), maxGraphWidth,
                        minGraphHeight);
            }
        }
        // populate paths
        populatePath(posPath, posEntries);
        populatePath(negPath, negEntries);


        // we fill the graph first
        mPaint.setShader(posGradient);

        canvas.drawPath(posPath, mPaint);
        //then the line
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);
        mPaint.setColor(positiveLineColor);

        canvas.drawPath(posPath, mPaint);

        //negative
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(negUnderLineColor);
        mPaint.setShader(negGradient);

        canvas.drawPath(negPath, mPaint);
        //then the line
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(7);
        mPaint.setColor(negativeLineColor);
        canvas.drawPath(negPath, mPaint);
        mPaint.setShader(null);
    }

    @Override
    public void setData(ArrayList<LinePoint> dataList) {
        entries = dataList;
        this.invalidate();
    }

    @Override
    public ArrayList<LinePoint> getData() {
        return entries;
    }

    @Override
    public void appendData(ArrayList<LinePoint> entry) {
        entries.addAll(entry);
        this.invalidate();
    }

    @Override
    public void removeData(LinePoint entry, int index) {

    }

    @Override
    public void setGraphType(GraphType graphType) {
    }

    @Override
    public GraphType getGraphType() {
        return GraphType.LineChart;
    }

    /**
     * A little method to calculate the height of the line.
     * initially the quad line is lower than the straight line so
     * offset will be + or - line spacing.
     * the formula to calculate the offset is: (value * line spacing) / the upper drawn
     * percentage line.
     * so if the value is < 25 then upper percentage line is 25, hence the spacing calculated
     * from based line to "25%" line and so on.
     *
     * @param value           percentage
     * @param halfGraphHeight the height from the base line to the last ticks of the graph
     * @param quadLine        if the line is quad rather than cubic
     * @return heights of the line
     */
    private float calculateHeights(float value, int halfGraphHeight, boolean quadLine) {
        float calculatedHeight;
        float absoluteValue = value;
        if (absoluteValue == 100 && !quadLine) {
            absoluteValue = absoluteValue - 1;
        } else if (absoluteValue == -100 && !quadLine) {
            absoluteValue = absoluteValue + 1;
        } else if (absoluteValue == 0 && !quadLine) {
            absoluteValue = absoluteValue - 1;
        } else if (absoluteValue == 0 && !quadLine) {
            absoluteValue = absoluteValue + 1;
        }
        absoluteValue    = Math.abs(absoluteValue);
        calculatedHeight = baseLinePoint.startY - (((absoluteValue * (baseLinePoint.startY -
                halfGraphHeight)) / 100));
        if (quadLine) {
            int multiplier;
            int percentageLine;

            if (absoluteValue > 0 && absoluteValue <= 25) {
                multiplier = lineSpacing;
                percentageLine = 25;
            } else if (absoluteValue > 25 && absoluteValue <= 50) {
                multiplier = lineSpacing * 2;
                percentageLine = 50;
            } else if (absoluteValue > 50 && absoluteValue <= 75) {
                multiplier = lineSpacing * 3;
                percentageLine = 75;
            } else if (absoluteValue <= 100) {
                multiplier = lineSpacing * 4;
                percentageLine = 100;
            } else {
                return baseLinePoint.startY;
            }
            float offset = (absoluteValue * multiplier) / percentageLine;
            if (value > 0) {
                calculatedHeight = calculatedHeight - offset;
            } else {
                calculatedHeight = baseLinePoint.startY + (baseLinePoint.startY - (calculatedHeight -
                        offset));
            }
        }
        if (value < 0 && !quadLine)
            calculatedHeight = baseLinePoint.startY + (baseLinePoint.startY - calculatedHeight);
        return calculatedHeight;
    }

    private void populatePointData(LinePoint linePoint, int index, int size, int maxGraphWidth, int
            minGraphHeight) {
        float height = calculateHeights((float) linePoint.value, minGraphHeight, entries.size()
                == 2);
        int widthSpacing = maxGraphWidth / size;
        linePoint.startX = baseLinePoint.startX + (widthSpacing * (index + 1));
        linePoint.startY = (int) height;
    }

    private void populatePath(Path path, ArrayList<LinePoint> pointList) {

        for (int i = 0; i < pointList.size(); i++) {
            //First calculate point dx and dy
            LinePoint linePoint = pointList.get(i);
            if (pointList.size() == 1) { // we want the next line to be baseline if only 1 point
                linePoint.dx = 0;
                linePoint.dy = baseLinePoint.startY - linePoint.startY;
                path.quadTo(linePoint.startX / 2, linePoint.startY, baseLinePoint.endX,
                        baseLinePoint.startY);
            } else {
                if (i == 0) {
                    LinePoint next = pointList.get(i + 1);
                    linePoint.dx = ((next.startX - linePoint.startX) / 8);
                    linePoint.dy = ((next.startY - linePoint.startY) / 8);
                } else if (i == pointList.size() - 1) {
                    LinePoint prev = pointList.get(i - 1);
                    linePoint.dx = ((linePoint.startX - prev.startX) / 8);
                    linePoint.dy = ((linePoint.startY - prev.startY) / 8);
                } else {
                    LinePoint next = pointList.get(i + 1);
                    LinePoint prev = pointList.get(i - 1);
                    linePoint.dx = ((next.startX - prev.startX) / 8);
                    linePoint.dy = ((next.startY - prev.startY) / 8);
                }
            }
//            if (linePoint.value >= 95 || linePoint.value <= -95)
//                linePoint.dy = 3;
        }
        if (pointList.size() > 1) {
            for (int i = 0; i < pointList.size(); i++) {
                LinePoint point = pointList.get(i);
                if (i == 0 && i != pointList.size() - 1) {
                    LinePoint next = pointList.get(i + 1);
                    path.cubicTo(point.startX + point.dx, point.startY, next.startX - next.dx,
                            next.startY, next.startX, next.startY);
                } else if (i == pointList.size() - 1) {
                    path.cubicTo(point.startX + point.dx, point.startY + point.dy, baseLinePoint.endX,
                            baseLinePoint.startY, baseLinePoint.endX, baseLinePoint.startY);
                } else {
                    LinePoint next = pointList.get(i + 1);
                    path.cubicTo(point.startX + point.dx, point.startY + point.dy, next.startX - next.dx,
                            next.startY - next.dy, next.startX, next.startY);
                }
            }
        }
    }
}
