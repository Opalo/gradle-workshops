package org.warsjawa.gradleWS.plugins;

import com.google.inject.Injector;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.warsjawa.gradleWS.guice.SampleModule;
import org.warsjawa.gradleWS.tasks.SampleTask;

import java.util.HashMap;
import java.util.Iterator;

import static com.google.inject.Guice.createInjector;
import static java.lang.System.out;

public class SamplePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        out.println("Applying: " + getClass().getCanonicalName());
        project.getTasks().create(new HashMap() {
            {
                put("name", "sampleTask");
                put("type", SampleTask.class);
            }
        });

        Injector i = createInjector(new SampleModule());
        Iterator<Task> tasks = project.getTasks().iterator();
//        while (tasks.hasNext()) {
//            Task t = tasks.next();
//            i.injectMembers(t);
//        }
    }
}
