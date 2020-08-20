package com.simulator.simulator.XMLLoader.System;

import java.util.LinkedList;
import java.util.List;


public class MainSystem extends SubSystem {

    public List<Component> getComponentList() {
        return componentList;
    }

    List<Component> componentList = new LinkedList<Component>();

    public MainSystem() {
    }

    public MainSystem(List<Component> componentList) {
        this.componentList = componentList;
    }

    public MainSystem(LinkedList<ModuleElement> elements, String name, List<Component> componentList) {
        super(elements, name);
        this.componentList = componentList;
    }

    @Override
    public String toString() {
        return "MainSystem{" +
                "componentList=" + componentList +
                ", elements=" + elements +
                ", name='" + name + '\'' +
                '}';
    }
}
