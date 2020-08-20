package com.simulator.simulator.XMLLoader.System;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ComponentStructure {
    //module列表
    LinkedList<SubSystem> subSystems = new LinkedList<SubSystem>();
    //topModule存放
    MainSystem mainSystem = new MainSystem();
    //mapping from subsysName to subsystem
    Map<String,SubSystem> map = new HashMap<>();

    public Map<String, SubSystem> getMap() {
        return map;
    }

    public LinkedList<SubSystem> getSubSystems() {
        return subSystems;
    }

    public MainSystem getMainSystem() {
        return mainSystem;
    }

    public ComponentStructure(LinkedList<SubSystem> subSystems, MainSystem mainSystem) {
        this.subSystems = subSystems;
        this.mainSystem = mainSystem;
        for(SubSystem subSystem:subSystems){
            map.put(subSystem.getName(),subSystem);
        }
    }

    public void print(){
        for (int i = 0; i < subSystems.size(); i++) {
            System.out.println(subSystems.get(i));
        }
    }
}
