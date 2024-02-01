package org.etsntesla.gava.utils;

public class StackTraceMessage {

    public static String message(String msg){
        return trace()+" Poruka o gresci: "+msg;
    }

    public static String message(){
        return trace();
    }

    private static String trace(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        StackTraceElement callerTrace = stackTrace[2];
        String className = callerTrace.getClassName();
        String methodName = callerTrace.getMethodName();
        int lineNumber = callerTrace.getLineNumber();

        return  ">>>gava.Preference "+className+": in method "+methodName+" on line "+lineNumber;
    }

}
