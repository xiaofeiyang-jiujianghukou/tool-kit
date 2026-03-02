package com.vision.tool.kit.test;

import java.util.ArrayList;
import java.util.List;

public class TestAI {
    public static void main(String[] args) {
        int a = 10;
        int b = 1;
        int c = a / b;
        Integer d = 2;
        int e = a / d;
        System.out.println( 10 / d);
        System.out.println(c);
        System.out.println(e);
        List<String> list = null;
        addSomething("haha", list);
    }
    private static void addSomething(String str, List<String> list) {
        list.add(str);
    }
}
