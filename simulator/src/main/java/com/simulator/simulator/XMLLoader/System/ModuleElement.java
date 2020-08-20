package com.simulator.simulator.XMLLoader.System;

public class ModuleElement{
    int count;
    int in_num;
    String name;
    int out_num;
    String type;

    public ModuleElement(int count, int in_num, String name, int out_num, String type) {
        this.count = count;
        this.in_num = in_num;
        this.name = name;
        this.out_num = out_num;
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public int getIn_num() {
        return in_num;
    }

    public String getName() {
        return name;
    }

    public int getOut_num() {
        return out_num;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ModuleElement{" +
                "count=" + count +
                ", in_num=" + in_num +
                ", name='" + name + '\'' +
                ", out_num=" + out_num +
                ", type='" + type + '\'' +
                '}'+"\n";
    }
}
