package org.warsjawa.gradleWS.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.warsjawa.gradleWS.guice.Printer;

import javax.inject.Inject;

import static java.lang.System.out;

public class SampleTask extends DefaultTask {

    @Inject
    public Printer printer;

    @TaskAction
    public void run() {
        out.println("Running: " + getClass().getCanonicalName());
        printer.print();
    }

    @Override
    public String getName() {
        return "sampleTask";
    }

    @Override
    public String getDescription() {
        return "sampleTask description";
    }

    @Override
    public String getGroup() {
        return "sampleTask group";
    }
}
