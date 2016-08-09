package com.mygdx.etf.desktop;

import java.lang.instrument.Instrumentation;

public class ObjectSizeFetcher {
    public static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}
