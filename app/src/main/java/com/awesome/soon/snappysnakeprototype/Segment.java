package com.awesome.soon.snappysnakeprototype;


public class Segment {
    private Point start;
    private Point end;

    public Segment(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public float length() {
        return start.distanceTo(end);
    }

    public void moveEndPointBy(float len) {
        Vector newEndV = new Vector(start, end).normalize().multiplyBy(length() - len);
        setEnd(start.add(newEndV));
    }

    @Override
    public String toString() {
        return "Segment{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
