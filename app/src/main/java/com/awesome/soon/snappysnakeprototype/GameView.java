package com.awesome.soon.snappysnakeprototype;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView {
    private List<Segment> segments = new ArrayList<>();
    private List<Bonus> bonuses = new ArrayList<>();
    private List<Point> eatenBonusesLocations = new ArrayList<>();
    private Vector direction = new Vector(1, 1).normalize();

    private final Random rnd = new Random();

    private static final float SPEED = 15;
    private static float BONUS_RADIUS = 10;
    private static final float BONUS_ADD_LENGTH = 10;
    private static Paint BONUS_PAINT = new Paint();
    private static Paint SEGMENT_PAINT = new Paint();

    private final Object sync = new Object();

    static {
        BONUS_PAINT.setColor(Color.RED);
        SEGMENT_PAINT.setColor(Color.WHITE);
    }

    public GameView(Context context) {
        super(context);
        restartGame();
        setWillNotDraw(false);
    }

    public void restartGame() {
        segments = new ArrayList<>();
        bonuses = new ArrayList<>();
        eatenBonusesLocations = new ArrayList<>();

        segments.add(new Segment(new Point(100, 100), new Point(10, 10)));
        bonuses.add(generateBonus());
        direction = new Vector(1, 1).normalize();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        synchronized (sync) {
            super.dispatchDraw(canvas);

            for (Bonus bonus : bonuses) {
                drawBonus(canvas, bonus);
            }

            for (Segment segment : segments) {
                drawSegment(canvas, segment);
            }
        }
    }

    public void runOneStep() {
        if (eatsItself()) {
            restartGame();
            return;
        }

        updateSegments();

        updateBonuses();
        invalidate();
    }


    // see http://e-maxx.ru/algo/segments_intersection_checking
    float area(Point a, Point b, Point c) {
        return (b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x());
    }

    boolean intersect_1(float a, float b, float c, float d) {
        if (a > b) {
            float x = a;
            a = b;
            b = x;
        }
        if (c > d) {
            float x = c;
            c = d;
            d = x;
        }

        return Math.max(a, c) < Math.min(b, d);
    }

    boolean intersect(Segment s1, Segment s2) {
        return intersect_1(s1.getStart().x(), s1.getEnd().x(), s2.getStart().x(), s2.getEnd().x()) &&
                intersect_1(s1.getStart().y(), s1.getEnd().y(), s2.getStart().y(), s2.getEnd().y()) &&
                area(s1.getStart(), s1.getEnd(), s2.getStart()) * area(s1.getStart(), s1.getEnd(), s2.getEnd()) < 0 &&
                area(s2.getStart(), s2.getEnd(), s1.getStart()) * area(s2.getStart(), s2.getEnd(), s1.getEnd()) < 0;
    }

    private boolean eatsItself() {
        for(int i = 1; i < segments.size(); ++i) {
            if(intersect(headSegment(), segments.get(i))) {
                return true;
            }
        }

        return false;
    }

    public void setDirectionTo(Point p) {
        synchronized (sync) {
            direction = new Vector(head(), p).normalize();
            segments.add(0, new Segment(head(), head()));
        }
    }

    private void addHeadSegment(Point head) {
        segments.add(0, new Segment(head, head));
    }

    private void updateSegments() {
        headSegment().setStart(head().add(direction.multiplyBy(SPEED)));
        if(movesOutOfBounds()) {
            addHeadSegment(getOppositeLocation(head()));
        }

        float lenToRemove = direction.multiplyBy(SPEED).length();

        while (lenToRemove > tailSegment().length()) {
            lenToRemove -= tailSegment().length();
            eraseTailSegment();
        }

        tailSegment().moveEndPointBy(lenToRemove);

        for (int i = eatenBonusesLocations.size() - 1; i >= 0; i--) {
            if(tailSegment().getEnd().distanceTo(eatenBonusesLocations.get(i)) < BONUS_RADIUS) {
                eatenBonusesLocations.remove(i);
                tailSegment().moveEndPointBy(-BONUS_ADD_LENGTH);
            }
        }

    }

    private Point getOppositeLocation(Point point) {
        float x = point.x() > maxX()
                ? minX()
                : point.x() < minX()
                ? maxX()
                : point.x();

        float y = point.y() > maxY()
                ? minY()
                : point.y() < minY()
                ? maxY()
                : point.y();

        return new Point(x, y);
    }

    private boolean movesOutOfBounds() {
        return head().x() > maxX() || head().x() < minX() ||
        head().y() > maxY() || head().y() < minY();
    }

    private float minY() {
        return 0;
    }

    private float maxY() {
        return getHeight();
    }

    private float maxX() {
        return getWidth();
    }

    private float minX() {
        return 0;
    }

    private void updateBonuses() {
        for (int i = 0; i < bonuses.size(); ++i) {
            tryEatBonus(i);
        }
    }

    private void tryEatBonus(int i) {
        if (snakeEats(bonuses.get(i))) {
            eatenBonusesLocations.add(bonuses.get(i).getLocation());
            bonuses.set(i, generateBonus());
        }
    }

    private Bonus generateBonus() {
        if (getWidth() == 0) {
            return new Bonus(new Point(rnd.nextFloat() * 500, rnd.nextFloat() * 500));
        }

        return new Bonus(new Point(rnd.nextFloat() * getWidth(), rnd.nextFloat() * getHeight()));
    }

    private boolean snakeEats(Bonus bonus) {
        return head().distanceTo(bonus.getLocation()) < BONUS_RADIUS;
    }

    private void eraseTailSegment() {
        segments.remove(segments.size() - 1);
    }

    private Segment tailSegment() {
        return segments.get(segments.size() - 1);
    }

    private Segment headSegment() {
        return segments.get(0);
    }

    private Point head() {
        return headSegment().getStart();
    }

    private void drawSegment(Canvas canvas, Segment segment) {
        canvas.drawLine(
                segment.getStart().x(), segment.getStart().y(),
                segment.getEnd().x(), segment.getEnd().y(),
                SEGMENT_PAINT);
    }

    private void drawBonus(Canvas canvas, Bonus bonus) {
        canvas.drawCircle(bonus.getLocation().x(), bonus.getLocation().y(), BONUS_RADIUS, BONUS_PAINT);
    }
}
