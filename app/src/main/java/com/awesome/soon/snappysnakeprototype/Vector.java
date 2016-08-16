package com.awesome.soon.snappysnakeprototype;


public class Vector {
    private Point v;

    public Vector(Point from, Point to) {
        this(to.x() - from.x(), to.y() - from.y());
    }

    public Vector(float x, float y) {
        this(new Point(x, y));
    }

    private Vector(Point v) {
        this.v = v;
    }

    public Vector(double x, double y) {
        this((float)x, (float)y);
    }

    public float getX() {
        return v.x();
    }

    public float getY() {
        return v.y();
    }

    public float length() {
        return (float) Math.hypot(v.x(), v.y());
    }

    public Vector normalize() {
        return new Vector(getX() / length(), getY() / length());
    }

    public Vector multiplyBy(float a) {
        return new Vector(getX() * a, getY() * a);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + getX() +
                ", y=" + getY() +
                '}';
    }
}
