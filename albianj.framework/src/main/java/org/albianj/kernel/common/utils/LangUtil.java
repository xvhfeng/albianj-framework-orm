/* *******************************************************************
 * Copyright (c) 1999-2001 Xerox Corporation,
 *               2002 Palo Alto Research Center, Incorporated (PARC).
 *               2018 Contributors
 * All rights reserved.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v 2.0
 * which accompanies this distribution and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt
 *
 * Contributors:
 *     Xerox/PARC     initial implementation
 * ******************************************************************/
package org.albianj.kernel.common.utils;

import org.albianj.kernel.ServRouter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class LangUtil {

    public static final String EOL = System.lineSeparator();

    public static final String JRT_FS = "jrt-fs.jar";

    private static double vmVersion;

    static {
        // http://www.oracle.com/technetwork/java/javase/versioning-naming-139433.html
        // http://openjdk.java.net/jeps/223 "New Version-String Scheme"
        // TODO: Use java.lang.Runtime class (since Java 9, now AspectJ needs Java 11+ due to JDT Core anyway)
        final String JAVA_VERSION_NOT_FOUND = "System properties appear damaged, cannot find: java.version/java.runtime.version/java.vm.version";
        try {
            String vm = System.getProperty("java.version"); // JLS 20.18.7
            if (vm == null) {
                vm = System.getProperty("java.runtime.version");
            }
            if (vm == null) {
                vm = System.getProperty("java.vm.version");
            }
            if (vm == null) {
                new RuntimeException(JAVA_VERSION_NOT_FOUND).printStackTrace(System.err);
                vmVersion = 1.5;
            } else {
                // Care about the first set of digits and second set if first digit is 1
                try {
                    List<Integer> numbers = getJavaMajorMinor(vm);
                    if (numbers.get(0) == 1) {
                        // Old school for 1.0 > 1.8
                        vmVersion = numbers.get(0) + (numbers.get(1) / 10d);
                    } else {
                        // numbers.get(0) is the major version (9 and above)
                        // Note here the number will be 9 (or 10), *not* 1.9 or 1.10
                        vmVersion = numbers.get(0);
                    }
                } catch (Throwable t) {
                    // Give up
                    vmVersion = 1.5;
                }
            }
        } catch (Throwable t) {
            new RuntimeException(JAVA_VERSION_NOT_FOUND, t).printStackTrace(System.err);
            vmVersion = 1.5;
        }
    }

    /**
     * @return the vm version (1.1, 1.2, 1.3, 1.4, etc)
     */
    public static String getVmVersionString() {
        return Double.toString(vmVersion);
    }

    public static double getVmVersion() {
        return vmVersion;
    }

    private static List<Integer> getJavaMajorMinor(String vm) {
        List<Integer> result = new ArrayList<>();
        // Can be something like '1.5', '11.0.16.1', '19+36-2238'
        StringTokenizer st = new StringTokenizer(vm.replaceFirst("[+].*", ""), ".-_");
        try {
            result.add(Integer.parseInt(st.nextToken()));
            result.add(Integer.parseInt(st.nextToken()));
        } catch (Exception e) {
            // NoSuchElementException if no more tokens
            // NumberFormatException if not a number
        }
        // Always add a default minor, just in case a caller expects it
        if (result.size() == 1)
            result.add(0);
        return result;
    }

    @Deprecated
    public static boolean is1dot3VMOrGreater() {
        return 1.3 <= vmVersion;
    }

    @Deprecated
    public static boolean is1dot4VMOrGreater() {
        return 1.4 <= vmVersion;
    }

    @Deprecated
    public static boolean is1dot5VMOrGreater() {
        return 1.5 <= vmVersion;
    }

    @Deprecated
    public static boolean is1dot6VMOrGreater() {
        return 1.6 <= vmVersion;
    }

    @Deprecated
    public static boolean is1dot7VMOrGreater() {
        return 1.7 <= vmVersion;
    }

    public static boolean is1dot8VMOrGreater() {
        return 1.8 <= vmVersion;
    }

    public static boolean is9VMOrGreater() {
        return 9 <= vmVersion;
    }

    public static boolean is10VMOrGreater() {
        return 10 <= vmVersion;
    }

    public static boolean is11VMOrGreater() {
        return 11 <= vmVersion;
    }

    public static boolean is12VMOrGreater() {
        return 12 <= vmVersion;
    }

    public static boolean is13VMOrGreater() {
        return 13 <= vmVersion;
    }

    public static boolean is14VMOrGreater() {
        return 14 <= vmVersion;
    }

    public static boolean is15VMOrGreater() {
        return 15 <= vmVersion;
    }

    public static boolean is16VMOrGreater() {
        return 16 <= vmVersion;
    }

    public static boolean is17VMOrGreater() {
        return 17 <= vmVersion;
    }

    public static boolean is18VMOrGreater() {
        return 18 <= vmVersion;
    }

    public static boolean is19VMOrGreater() {
        return 19 <= vmVersion;
    }

    public static boolean is20VMOrGreater() {
        return 20 <= vmVersion;
    }

    public static boolean is21VMOrGreater() {
        return 21 <= vmVersion;
    }

    public static boolean is22VMOrGreater() {
        return 22 <= vmVersion;
    }


    /**
     * Split string as classpath, delimited at File.pathSeparator. Entries are not trimmed, but empty entries are ignored.
     *
     * @param classpath the String to split - may be null or empty
     * @return String[] of classpath entries
     */
    public static String[] splitClasspath(String classpath) {
        if (StringsUtil.isEmpty(classpath)) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
        ArrayList<String> result = new ArrayList<>(st.countTokens());
        while (st.hasMoreTokens()) {
            String entry = st.nextToken();
            if (!StringsUtil.isEmpty(entry)) {
                result.add(entry);
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Get System property as boolean, but use default value where the system property is not set.
     *
     * @return true if value is set to true, false otherwise
     */
    public static boolean getBoolean(String propertyName, boolean defaultValue) {
        if (null != propertyName) {
            try {
                String value = System.getProperty(propertyName);
                if (null != value) {
                    return Boolean.parseBoolean(value);
                }
            } catch (Throwable t) {
                // default below
            }
        }
        return defaultValue;
    }

    /**
     * Extract options and arguments to input option list, returning remainder. The input options will be nullified if not found.
     * e.g.,
     *
     * <pre>
     * String[] options = new String[][] { new String[] { &quot;-verbose&quot; }, new String[] { &quot;-classpath&quot;, null } };
     * String[] args = extractOptions(args, options);
     * boolean verbose = null != options[0][0];
     * boolean classpath = options[1][1];
     * </pre>
     *
     * @param args    the String[] input options
     * @param options the String[][]options to find in the input args - not null for each String[] component the first subcomponent
     *                is the option itself, and there is one String subcomponent for each additional argument.
     * @return String[] of args remaining after extracting options to extracted
     */
    public static String[] extractOptions(String[] args, String[][] options) {
        if (SetUtil.isNullOrEmpty(args) || SetUtil.isNullOrEmpty(options)) {
            return args;
        }
        BitSet foundSet = new BitSet();
        String[] result = new String[args.length];
        int resultIndex = 0;
        for (int j = 0; j < args.length; j++) {
            boolean found = false;
            for (int i = 0; !found && (i < options.length); i++) {
                String[] option = options[i];
                ServRouter.throwIaxIfFalse(!SetUtil.isNullOrEmpty(option), "options");
                String sought = option[0];
                found = sought.equals(args[j]);
                if (found) {
                    foundSet.set(i);
                    int doMore = option.length - 1;
                    if (0 < doMore) {
                        final int MAX = j + doMore;
                        if (MAX >= args.length) {
                            String s = "expecting " + doMore + " args after ";
                            throw new IllegalArgumentException(s + args[j]);
                        }
                        for (int k = 1; k < option.length; k++) {
                            option[k] = args[++j];
                        }
                    }
                }
            }
            if (!found) {
                result[resultIndex++] = args[j];
            }
        }

        // unset any not found
        for (int i = 0; i < options.length; i++) {
            if (!foundSet.get(i)) {
                options[i][0] = null;
            }
        }
        // fixup remainder
        if (resultIndex < args.length) {
            String[] temp = new String[resultIndex];
            System.arraycopy(result, 0, temp, 0, resultIndex);
            args = temp;
        }

        return args;
    }


    /**
     * Extract options and arguments to input parameter list, returning
     * remainder.
     *
     * @param args         the String[] input options
     * @param validOptions the String[] options to find in the input args -
     *                     not null
     * @param optionArgs   the int[] number of arguments for each option in
     *                     validOptions
     *                     (if null, then no arguments for any option)
     * @param extracted    the List for the matched options
     * @return String[] of args remaining after extracting options to
     * extracted
     */
    public static String[] extractOptions(String[] args, String[]
            validOptions,
                                          int[] optionArgs, List extracted) {
        if (SetUtil.isNullOrEmpty(args)
                || SetUtil.isNullOrEmpty(validOptions)) {
            return args;
        }
        if (null != optionArgs) {
            if (optionArgs.length != validOptions.length) {
                throw new IllegalArgumentException("args must match options");
            }
        }
        String[] result = new String[args.length];
        int resultIndex = 0;
        for (int j = 0; j < args.length; j++) {
            boolean found = false;
            for (int i = 0; !found && (i < validOptions.length); i++) {
                String sought = validOptions[i];
                int doMore = (null == optionArgs ? 0 : optionArgs[i]);
                if (StringsUtil.isNullOrEmpty(sought)) {
                    continue;
                }
                found = sought.equals(args[j]);
                if (found) {
                    if (null != extracted) {
                        extracted.add(sought);
                    }
                    if (0 < doMore) {
                        final int MAX = j + doMore;
                        if (MAX >= args.length) {
                            String s = "expecting " + doMore + " args after ";
                            throw new IllegalArgumentException(s + args[j]);
                        }
                        if (null != extracted) {
                            while (j < MAX) {
                                extracted.add(args[++j]);
                            }
                        } else {
                            j = MAX;
                        }
                    }
                    break;
                }
            }
            if (!found) {
                result[resultIndex++] = args[j];
            }
        }
        if (resultIndex < args.length) {
            String[] temp = new String[resultIndex];
            System.arraycopy(result, 0, temp, 0, resultIndex);
            args = temp;
        }
        return args;
    }

    /**
     * @return String[] of entries in validOptions found in args
     */
    public static String[] selectOptions(String[] args, String[]
            validOptions) {
        if (SetUtil.isNullOrEmpty(args) || SetUtil.isNullOrEmpty(validOptions)) {
            return new String[0];
        }
        ArrayList result = new ArrayList();
        for (int i = 0; i < validOptions.length; i++) {
            String sought = validOptions[i];
            if (StringsUtil.isNullOrEmpty(sought)) {
                continue;
            }
            for (int j = 0; j < args.length; j++) {
                if (sought.equals(args[j])) {
                    result.add(sought);
                    break;
                }
            }
        }
        return (String[]) result.toArray(new String[0]);
    }

    /**
     * @return String[] of entries in validOptions found in args
     */
    public static String[] selectOptions(List args, String[] validOptions) {
        if (SetUtil.isNullOrEmpty(args) || SetUtil.isNullOrEmpty(validOptions)) {
            return new String[0];
        }
        ArrayList result = new ArrayList();
        for (int i = 0; i < validOptions.length; i++) {
            String sought = validOptions[i];
            if (StringsUtil.isNullOrEmpty(sought)) {
                continue;
            }
            for (Iterator iter = args.iterator(); iter.hasNext(); ) {
                String arg = (String) iter.next();
                if (sought.equals(arg)) {
                    result.add(sought);
                    break;
                }
            }
        }
        return (String[]) result.toArray(new String[0]);
    }

    /**
     * Generate variants of String[] options by creating an extra set for
     * each option that ends with "-". If none end with "-", then an
     * array equal to <code>new String[][] { options }</code> is returned;
     * if one ends with "-", then two sets are returned,
     * three causes eight sets, etc.
     *
     * @return String[][] with each option set.
     * @throws IllegalArgumentException if any option is null or empty.
     */
    public static String[][] optionVariants(String[] options) {
        if ((null == options) || (0 == options.length)) {
            return new String[][]{new String[0]};
        }
        // be nice, don't stomp input
        String[] temp = new String[options.length];
        System.arraycopy(options, 0, temp, 0, temp.length);
        options = temp;
        boolean[] dup = new boolean[options.length];
        int numDups = 0;

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            if (StringsUtil.isNullOrEmpty(option)) {
                throw new IllegalArgumentException("empty option at " + i);
            }
            if (option.endsWith("-")) {
                options[i] = option.substring(0, option.length() - 1);
                dup[i] = true;
                numDups++;
            }
        }
        final String[] NONE = new String[0];
        final int variants = exp(2, numDups);
        final String[][] result = new String[variants][];
        // variant is a bitmap wrt doing extra value when dup[k]=true
        for (int variant = 0; variant < variants; variant++) {
            ArrayList next = new ArrayList();
            int nextOption = 0;
            for (int k = 0; k < options.length; k++) {
                if (!dup[k] || (0 != (variant & (1 << (nextOption++))))) {
                    next.add(options[k]);
                }
            }
            result[variant] = (String[]) next.toArray(NONE);
        }
        return result;
    }

    public static int exp(int base, int power) { // not in Math?
        if (0 > power) {
            throw new IllegalArgumentException("negative power: " + power);
        }
        int result = 1;
        while (0 < power--) {
            result *= base;
        }
        return result;
    }

    /**
     * @return a String with the unqualified class name of the class (or "null")
     */
    public static String unqualifiedClassName(Class<?> c) {
        if (null == c) {
            return "null";
        }
        String name = c.getName();
        int loc = name.lastIndexOf(".");
        if (-1 != loc) {
            name = name.substring(1 + loc);
        }
        return name;
    }

    /**
     * @return a String with the unqualified class name of the object (or "null")
     */
    public static String unqualifiedClassName(Object o) {
        return LangUtil.unqualifiedClassName(null == o ? null : o.getClass());
    }

    /**
     * Gen classpath.
     *
     * @param bootclasspath
     * @param classpath
     * @param classesDir
     * @param outputJar
     * @return String combining classpath elements
     */
    public static String makeClasspath( // XXX dumb implementation
                                        String bootclasspath, String classpath, String classesDir, String outputJar) {
        StringBuffer sb = new StringBuffer();
        StringsUtil.addIfNotEmpty(bootclasspath, sb, File.pathSeparator);
        StringsUtil.addIfNotEmpty(classpath, sb, File.pathSeparator);
        if (!StringsUtil.addIfNotEmpty(classesDir, sb, File.pathSeparator)) {
            StringsUtil.addIfNotEmpty(outputJar, sb, File.pathSeparator);
        }
        return sb.toString();
    }


    public static String findCalledClassName(int stackIdx) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int realIdx = stackIdx + 1; //add self stack
        if (realIdx >= stackTrace.length) {
            return StringsUtil.EMPTY;
        }

        return stackTrace[realIdx].getClassName();
    }

    public static String findCalledClassNameFilter(int stackIdx, Set<String> filterClzz) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int realIdx = stackIdx + 1; //add self stack
        if (realIdx >= stackTrace.length) {
            return StringsUtil.EMPTY;
        }

        for (int i = realIdx; i < stackTrace.length; i++) {
            StackTraceElement stack = stackTrace[i];
            if (filterClzz.contains(stack.getClassName())) {
                continue;
            }
            return stack.getClassName();
        }
        return StringsUtil.EMPTY;
    }

    public static StackTraceElement findCalledStack(int stackIdx) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int realIdx = stackIdx + 1; //add self stack
        if (realIdx >= stackTrace.length) {
            return null;
        }

        return stackTrace[realIdx];
    }

    public static StackTraceElement findCalledStackFilter(int stackIdx, Set<String> filterClzz) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int realIdx = stackIdx + 1; //add self stack
        if (realIdx >= stackTrace.length) {
            return null;
        }

        for (int i = realIdx; i < stackTrace.length; i++) {
            StackTraceElement stack = stackTrace[i];
            if (filterClzz.contains(stack.getClassName())) {
                continue;
            }
            return stack;
        }
        return null;
    }

    /**
     * Create or initialize a process controller to run a process in another VM asynchronously.
     *
     * @param controller the ProcessController to initialize, if not null
     * @param classpath
     * @param mainClass
     * @param args
     * @return initialized ProcessController
     */
    public static ProcessController makeProcess(ProcessController controller, String classpath, String mainClass, String[] args) {
        File java = LangUtil.getJavaExecutable();
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add(java.getAbsolutePath());
        cmd.add("-classpath");
        cmd.add(classpath);
        cmd.add(mainClass);
        if (!SetUtil.isNullOrEmpty(args)) {
            cmd.addAll(Arrays.asList(args));
        }
        String[] command = cmd.toArray(new String[0]);
        if (null == controller) {
            controller = new ProcessController();
        }
        controller.init(command, mainClass);
        return controller;
    }

    /**
     * Create a process to run asynchronously.
     *
     * @param controller if not null, initialize this one
     * @param command    the String[] command to run
     * @param controller the ProcessControl for streams and results
     */
    public static ProcessController makeProcess( // not needed?
                                                 ProcessController controller,
                                                 String[] command,
                                                 String label) {
        if (null == controller) {
            controller = new ProcessController();
        }
        controller.init(command, label);
        return controller;
    }

    /**
     * Find java executable File path from java.home system property.
     *
     * @return File associated with the java command, or null if not found.
     */
    public static File getJavaExecutable() {
        String javaHome = null;
        File result = null;
        // java.home
        // java.class.path
        // java.ext.dirs
        try {
            javaHome = System.getProperty("java.home");
        } catch (Throwable t) {
            // ignore
        }
        if (null != javaHome) {
            File binDir = new File(javaHome, "bin");
            if (binDir.isDirectory() && binDir.canRead()) {
                String[] execs = new String[]{"java", "java.exe"};
                for (String exec : execs) {
                    result = new File(binDir, exec);
                    if (result.canRead()) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Sleep for a particular period (in milliseconds).
     *
     * @param time the long time in milliseconds to sleep
     * @return true if delay succeeded, false if interrupted 100 times
     */
    public static boolean sleep(long milliseconds) {
        if (milliseconds == 0) {
            return true;
        } else if (milliseconds < 0) {
            throw new IllegalArgumentException("negative: " + milliseconds);
        }
        return sleepUntil(milliseconds + System.currentTimeMillis());
    }

    /**
     * Sleep until a particular time.
     *
     * @param time the long time in milliseconds to sleep until
     * @return true if delay succeeded, false if interrupted 100 times
     */
    public static boolean sleepUntil(long time) {
        if (time == 0) {
            return true;
        } else if (time < 0) {
            throw new IllegalArgumentException("negative: " + time);
        }
        // final Thread thread = Thread.currentThread();
        long curTime = System.currentTimeMillis();
        for (int i = 0; (i < 100) && (curTime < time); i++) {
            try {
                Thread.sleep(time - curTime);
            } catch (InterruptedException e) {
                // ignore
            }
            curTime = System.currentTimeMillis();
        }
        return (curTime >= time);
    }

    public static String getJrtFsFilePath() {
        return getJavaHome() + File.separator + "lib" + File.separator + JRT_FS;
    }

    public static String getJavaHome() {
        return System.getProperty("java.home");
    }

    /**
     * Handle an external process asynchrously. <code>start()</code> launches a main thread to wait for the process and pipes
     * streams (in child threads) through to the corresponding streams (e.g., the process System.err to this System.err). This can
     * complete normally, by exception, or on demand by a client. Clients can implement <code>doCompleting(..)</code> to get notice
     * when the process completes.
     * <p>
     * The following sample code creates a process with a completion callback starts it, and some time later retries the process.
     *
     * <pre>
     * LangUtil.ProcessController controller = new LangUtil.ProcessController() {
     * 	protected void doCompleting(LangUtil.ProcessController.Thrown thrown, int result) {
     * 		// signal result
     *    }
     * };
     * controller.init(new String[] { &quot;java&quot;, &quot;-version&quot; }, &quot;java version&quot;);
     * controller.start();
     * // some time later...
     * // retry...
     * if (!controller.completed()) {
     * 	controller.stop();
     * 	controller.reinit();
     * 	controller.start();
     * }
     * </pre>
     *
     * <u>warning</u>: Currently this does not close the input or output streams, since doing so prevents their use later.
     */
    public static class ProcessController {
        /*
         * XXX not verified thread-safe, but should be. Known problems: - user stops (completed = true) then exception thrown from
         * destroying process (stop() expects !completed) ...
         */
        private String[] command;
        private String[] envp;
        private String label;

        private boolean init;
        private boolean started;
        private boolean completed;
        /**
         * if true, stopped by user when not completed
         */
        private boolean userStopped;

        private Process process;
        private FileUtil.Pipe errStream;
        private FileUtil.Pipe outStream;
        private FileUtil.Pipe inStream;
        private ByteArrayOutputStream errSnoop;
        private ByteArrayOutputStream outSnoop;

        private int result;
        private Thrown thrown;

        public ProcessController() {
        }

        /**
         * Permit re-running using the same command if this is not started or if completed. Can also call this when done with
         * results to release references associated with results (e.g., stack traces).
         */
        public final void reinit() {
            if (!init) {
                throw new IllegalStateException("must init(..) before reinit()");
            }
            if (started && !completed) {
                throw new IllegalStateException("not completed - do stop()");
            }
            // init everything but command and label
            started = false;
            completed = false;
            result = Integer.MIN_VALUE;
            thrown = null;
            process = null;
            errStream = null;
            outStream = null;
            inStream = null;
        }

        public final void init(String classpath, String mainClass, String[] args) {
            init(LangUtil.getJavaExecutable(), classpath, mainClass, args);
        }

        public final void init(File java, String classpath, String mainClass, String[] args) {
            ServRouter.throwIaxIfNull(java, "java");
            ServRouter.throwIaxIfNull(mainClass, "mainClass");
            ServRouter.throwIaxIfNull(args, "args");
            ArrayList<String> cmd = new ArrayList<>();
            cmd.add(java.getAbsolutePath());
            cmd.add("-classpath");
            cmd.add(classpath);
            cmd.add(mainClass);
            if (!SetUtil.isNullOrEmpty(args)) {
                cmd.addAll(Arrays.asList(args));
            }
            init(cmd.toArray(new String[0]), mainClass);
        }

        public final void init(String[] command, String label) {
            this.command = (String[]) SetUtil.safeCopy(command, new String[0]);
            if (1 > this.command.length) {
                throw new IllegalArgumentException("empty command");
            }
            this.label = StringsUtil.isEmpty(label) ? command[0] : label;
            init = true;
            reinit();
        }

        public final void setEnvp(String[] envp) {
            this.envp = (String[]) SetUtil.safeCopy(envp, new String[0]);
            if (1 > this.envp.length) {
                throw new IllegalArgumentException("empty envp");
            }
        }

        public final void setErrSnoop(ByteArrayOutputStream snoop) {
            errSnoop = snoop;
            if (null != errStream) {
                errStream.setSnoop(errSnoop);
            }
        }

        public final void setOutSnoop(ByteArrayOutputStream snoop) {
            outSnoop = snoop;
            if (null != outStream) {
                outStream.setSnoop(outSnoop);
            }
        }

        /**
         * Start running the process and pipes asynchronously.
         *
         * @return Thread started or null if unable to start thread (results available via <code>getThrown()</code>, etc.)
         */
        public final Thread start() {
            if (!init) {
                throw new IllegalStateException("not initialized");
            }
            synchronized (this) {
                if (started) {
                    throw new IllegalStateException("already started");
                }
                started = true;
            }
            try {
                process = Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                stop(e, Integer.MIN_VALUE);
                return null;
            }
            errStream = new FileUtil.Pipe(process.getErrorStream(), System.err);
            if (null != errSnoop) {
                errStream.setSnoop(errSnoop);
            }
            outStream = new FileUtil.Pipe(process.getInputStream(), System.out);
            if (null != outSnoop) {
                outStream.setSnoop(outSnoop);
            }
            inStream = new FileUtil.Pipe(System.in, process.getOutputStream());
            // start 4 threads, process & pipes for in, err, out
            Runnable processRunner = new Runnable() {
                @Override
                public void run() {
                    Throwable thrown = null;
                    int result = Integer.MIN_VALUE;
                    try {
                        // pipe threads are children
                        new Thread(errStream).start();
                        new Thread(outStream).start();
                        new Thread(inStream).start();
                        process.waitFor();
                        result = process.exitValue();
                    } catch (Throwable e) {
                        thrown = e;
                    } finally {
                        stop(thrown, result);
                    }
                }
            };
            Thread result = new Thread(processRunner, label);
            result.start();
            return result;
        }

        /**
         * Destroy any process, stop any pipes. This waits for the pipes to clear (reading until no more input is available), but
         * does not wait for the input stream for the pipe to close (i.e., not waiting for end-of-file on input stream).
         */
        public final synchronized void stop() {
            if (completed) {
                return;
            }
            userStopped = true;
            stop(null, Integer.MIN_VALUE);
        }

        public final String[] getCommand() {
            String[] toCopy = command;
            if (SetUtil.isNullOrEmpty(toCopy)) {
                return new String[0];
            }
            String[] result = new String[toCopy.length];
            System.arraycopy(toCopy, 0, result, 0, result.length);
            return result;
        }

        public final boolean completed() {
            return completed;
        }

        public final boolean started() {
            return started;
        }

        public final boolean userStopped() {
            return userStopped;
        }

        /**
         * Get any Throwable thrown. Note that the process can complete normally (with a valid return value), at the same time the
         * pipes throw exceptions, and that this may return some exceptions even if the process is not complete.
         *
         * @return null if not complete or Thrown containing exceptions thrown by the process and streams.
         */
        public final Thrown getThrown() { // cache this
            return makeThrown(null);
        }

        public final int getResult() {
            return result;
        }

        /**
         * Subclasses implement this to get synchronous notice of completion. All pipes and processes should be complete at this
         * time. To get the exceptions thrown for the pipes, use <code>getThrown()</code>. If there is an exception, the process
         * completed abruptly (including side-effects of the user halting the process). If <code>userStopped()</code> is true, then
         * some client asked that the process be destroyed using <code>stop()</code>. Otherwise, the result code should be the
         * result value returned by the process.
         *
         * @param thrown same as <code>getThrown().fromProcess</code>.
         * @param result same as <code>getResult()</code>
         */
        protected void doCompleting(Thrown thrown, int result) {
        }

        /**
         * Handle termination (on-demand, abrupt, or normal) by destroying and/or halting process and pipes.
         *
         * @param thrown ignored if null
         * @param result ignored if Integer.MIN_VALUE
         */
        private final synchronized void stop(Throwable thrown, int result) {
            if (completed) {
                throw new IllegalStateException("already completed");
            } else if (null != this.thrown) {
                throw new IllegalStateException("already set thrown: " + thrown);
            }
            // assert null == this.thrown
            this.thrown = makeThrown(thrown);
            if (null != process) {
                process.destroy();
            }
            if (null != inStream) {
                inStream.halt(false, true); // this will block if waiting
                inStream = null;
            }
            if (null != outStream) {
                outStream.halt(true, true);
                outStream = null;
            }
            if (null != errStream) {
                errStream.halt(true, true);
                errStream = null;
            }
            if (Integer.MIN_VALUE != result) {
                this.result = result;
            }
            completed = true;
            doCompleting(this.thrown, result);
        }

        /**
         * Create snapshot of Throwable's thrown.
         *
         * @param thrown ignored if null or if this.thrown is not null
         */
        private final synchronized Thrown makeThrown(Throwable processThrown) {
            if (null != thrown) {
                return thrown;
            }
            return new Thrown(processThrown, (null == outStream ? null : outStream.getThrown()), (null == errStream ? null
                    : errStream.getThrown()), (null == inStream ? null : inStream.getThrown()));
        }

        public static class Thrown {
            public final Throwable fromProcess;
            public final Throwable fromErrPipe;
            public final Throwable fromOutPipe;
            public final Throwable fromInPipe;
            /**
             * true only if some Throwable is not null
             */
            public final boolean thrown;

            private Thrown(Throwable fromProcess, Throwable fromOutPipe, Throwable fromErrPipe, Throwable fromInPipe) {
                this.fromProcess = fromProcess;
                this.fromErrPipe = fromErrPipe;
                this.fromOutPipe = fromOutPipe;
                this.fromInPipe = fromInPipe;
                thrown = ((null != fromProcess) || (null != fromInPipe) || (null != fromOutPipe) || (null != fromErrPipe));
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                append(sb, fromProcess, "process");
                append(sb, fromOutPipe, " stdout");
                append(sb, fromErrPipe, " stderr");
                append(sb, fromInPipe, "  stdin");
                if (0 == sb.length()) {
                    return "Thrown (none)";
                } else {
                    return sb.toString();
                }
            }

            private void append(StringBuilder sb, Throwable thrown, String label) {
                if (null != thrown) {
                    sb.append("from " + label + ": ");
                    sb.append(ServRouter.renderExceptionShort(thrown));
                    sb.append(LangUtil.EOL);
                }
            }
        } // class Thrown
    }

}
