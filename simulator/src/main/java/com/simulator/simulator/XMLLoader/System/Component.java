package com.simulator.simulator.XMLLoader.System;

public class Component{
    String name;
    String type;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Component(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Component{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
