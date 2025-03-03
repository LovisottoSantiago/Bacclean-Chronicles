package io.github.Bacclean.lwjgl3;

import org.lwjgl.system.macosx.LibC;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class StartupHelper {

    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    private StartupHelper() {
        throw new UnsupportedOperationException();
    }


    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("mac")) {
            if (osName.contains("windows")) {
                System.setProperty("java.io.tmpdir", System.getenv("ProgramData") + "/libGDX-temp");
            }
            return false;
        }

        if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
            return false;
        }

        long pid = LibC.getpid();

        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(
                    "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument.");
            return false;
        }

        ArrayList<String> jvmArgs = new ArrayList<>();
        String separator = System.getProperty("file.separator");

        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";

        if (!(new File(javaExecPath)).exists()) {
            System.err.println(
                    "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the -XstartOnFirstThread argument manually!");
            return false;
        }

        jvmArgs.add(javaExecPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));
        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) {
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                System.err.println("The main class could not be determined.");
                return false;
            }
        }
        jvmArgs.add(mainClass);

        try {
            if (!redirectOutput) {
                ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
                processBuilder.start();
            } else {
                Process process = (new ProcessBuilder(jvmArgs))
                        .redirectErrorStream(true).start();
                BufferedReader processOutput = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = processOutput.readLine()) != null) {
                    System.out.println(line);
                }

                process.waitFor();
            }
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            e.printStackTrace();
        }

        return true;
    }

    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }
}