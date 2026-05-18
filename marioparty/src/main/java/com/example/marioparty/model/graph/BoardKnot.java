package com.example.marioparty.model.graph;

import com.example.marioparty.model.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public final class BoardKnot {

    private final int id;
    private final double x;
    private final double y;

    private Field.Type fieldType;
    private final List<Integer> targetKnotIds = new ArrayList<>();

    public BoardKnot(int id, double x, double y, Field.Type fieldType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.fieldType = fieldType;
    }


    public void setFieldType(Field.Type fieldType) {
        this.fieldType = fieldType;
    }

    public void addTargetKnotId(int targetKnotId) {
        targetKnotIds.add(targetKnotId);
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Field.Type getFieldType() {
        return fieldType;
    }


    public List<Integer> getTargetKnotIds() {
        return Collections.unmodifiableList(targetKnotIds);
    }
}
