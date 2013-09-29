package org.warsjawa.gradleWS.guice;

public class StdPrinter implements Printer {

    @Override
    public void print() {
        System.out.println("This is " + getClass().getSimpleName());
    }
}
