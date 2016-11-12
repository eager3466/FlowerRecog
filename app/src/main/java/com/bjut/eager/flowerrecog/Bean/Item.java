package com.bjut.eager.flowerrecog.Bean;

/**
 * Created by Eager on 2016/9/26.
 */
public class Item {

    private int mIndex;
    private float mProbability;
    private String mDescription;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public float getProbability() {
        return mProbability;
    }

    public void setProbability(float probability) {
        this.mProbability = probability;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Index: ").append(getIndex())
                .append("\nProbability: ").append(getProbability())
                .append("\nDescription: ").append(getDescription());
        return builder.toString();
    }
}
