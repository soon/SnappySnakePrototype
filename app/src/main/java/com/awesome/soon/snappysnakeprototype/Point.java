package com.awesome.soon.snappysnakeprototype;


public class Point {
    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x() {
        return x;
    }

    public void x(float x) {
        this.x = x;
    }

    public float y() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Point add(Vector v) {
        return new Point(x + v.getX(), y + v.getY());
    }

    public float distanceTo(Point p) {
        return (float) Math.hypot(x - p.x, y - p.y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
