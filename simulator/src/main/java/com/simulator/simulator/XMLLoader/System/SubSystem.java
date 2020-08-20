package com.simulator.simulator.XMLLoader.System;

import java.util.LinkedList;

//<element count="1" in_num="3" name="BUS" out_num="2" type="AXIBus"/>

public class SubSystem {
    LinkedList<ModuleElement> elements = new LinkedList<>();
    public String name;

    public LinkedList<ModuleElement> getElements() {
        return elements;
    }

    public String getName() {
        return name;
    }

    public SubSystem() {
    }

    public SubSystem(LinkedList<ModuleElement> elements, String name) {
        this.elements = elements;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubSystem:" + "\n" +
                "elements=" + elements + "\n" +
                ", name='" + name + '\'' + "\n" +
                '}';
    }
}
