package com.by.movx.utils;

import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by
 * on 01.10.2016.
 */
public class MovableRect extends Circle {

    private double bx = 100, by = 100;
    private double maxRadius = 500;

    final Delta dragDelta = new Delta();

    public MovableRect(double radius) {

        super(radius, new Color(0, 0.5, 0.1, 0.5));

        setCenterX(radius);
        setCenterY(radius);

        setOnMousePressed(event -> {
                dragDelta.x = getCenterX() - event.getX();
                dragDelta.y = getCenterY() - event.getY();
                getScene().setCursor(Cursor.MOVE);
        });
        setOnMouseReleased(event -> getScene().setCursor(Cursor.HAND));

        setOnMouseDragged(event -> {
                double newX = event.getX() + dragDelta.x;
                if (newX > getRadius() && newX < getBx() - getRadius()) {
                    setCenterX(newX);
                }
                double newY = event.getY() + dragDelta.y;
                if (newY > getRadius() && newY < getBy() - getRadius()) {
                    setCenterY(newY);
                }

            });

        setOnMouseEntered(event -> {
                if (!event.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.HAND);
                }
        });
        setOnMouseExited(event -> {
                if (!event.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.DEFAULT);
                }
        });
    }

    public double getBx() {
        return bx;
    }

    public void setBx(double bx) {
        this.bx = bx;
    }

    public double getBy() {
        return by;
    }

    public void setBy(double by) {
        this.by = by;
    }

    private class Delta { double x, y; }

    public void radius(double radius) {
        if(radius < maxRadius) {
            setRadius(radius);
        } else {
            setRadius(maxRadius);
        }
    }

    public void setMaxRadius(double maxRadius) {
        this.maxRadius = maxRadius - 2;
    }
}
