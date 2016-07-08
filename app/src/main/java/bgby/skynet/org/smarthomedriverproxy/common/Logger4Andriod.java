package bgby.skynet.org.smarthomedriverproxy.common;

import android.util.Log;

import org.skynet.bgby.driverutils.ILogger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Clariones on 6/1/2016.
 */
public class Logger4Andriod implements ILogger {
    protected static final Map<Level, LoggerWorker> levelMap = new HashMap<Level, LoggerWorker>();
    private static String shorterTag(String tag){
        int length = tag.length();
        if (length < 23){
            return tag;
        }
        int pos = tag.lastIndexOf('.');
        if (pos > 0 ){
            tag = tag.substring(pos+1);
        }
        length = tag.length();
        if (length < 23){
            return tag;
        }
        return tag.substring(length-23);
    }
    static {
        levelMap.put(Level.CONFIG, new InfoLogger());
        levelMap.put(Level.FINE, new DebugLogger());
        levelMap.put(Level.FINER, new VerboseLogger());
        levelMap.put(Level.FINEST, new VerboseLogger());
        levelMap.put(Level.INFO, new InfoLogger());
        levelMap.put(Level.WARNING, new WarnLogger());
        levelMap.put(Level.SEVERE, new ErrLogger());
    }
    @Override
    public void log(Level level, String tag, String msg) {
        LoggerWorker lg= levelMap.get(level);
        if (lg == null){
            lg = levelMap.get(Level.SEVERE);
        }
        tag = shorterTag(tag);
        if (lg.isLoggable(tag)){
            lg.log(tag, msg);
        }
    }

    @Override
    public void log(Level level, String tag, String msg, Object param) {
        LoggerWorker lg= levelMap.get(level);
        if (lg == null){
            lg = levelMap.get(Level.SEVERE);
        }
        tag = shorterTag(tag);
        if (lg.isLoggable(tag)){
            lg.log(tag, msg, param);
        }
    }

    @Override
    public void log(Level level, String tag, String msg, Object[] params) {
        LoggerWorker lg= levelMap.get(level);
        if (lg == null){
            lg = levelMap.get(Level.SEVERE);
        }
        tag = shorterTag(tag);
        if (lg.isLoggable(tag)){
            lg.log(tag, msg, params);
        }
    }

    @Override
    public void log(Level level, String tag, String msg, Throwable throwable) {
        LoggerWorker lg= levelMap.get(level);
        if (lg == null){
            lg = levelMap.get(Level.SEVERE);
        }
        tag = shorterTag(tag);
        if (lg.isLoggable(tag)){
            lg.log(tag, msg, throwable);
        }
    }

    public interface LoggerWorker {
        boolean isLoggable(String tag);
        void log(String tag, String msg);
        void log(String tag, String msg, Object param);
        void log(String tag, String msg, Object[] params);
        void log(String tag, String msg, Throwable throwable);
    }

    static class InfoLogger  implements  LoggerWorker {

        @Override
        public boolean isLoggable(String tag) {
            return Log.isLoggable(tag, Log.INFO);
        }

        @Override
        public void log(String tag, String msg) {
            Log.i(tag, msg);
        }

        @Override
        public void log(String tag, String msg, Object param) {
            Log.i(tag, MessageFormat.format(msg, param));
        }

        @Override
        public void log(String tag, String msg, Object[] params) {
            Log.i(tag, MessageFormat.format(msg, params));
        }

        @Override
        public void log(String tag, String msg, Throwable throwable) {
            Log.i(tag, msg, throwable);
        }
    }

    static class DebugLogger  implements  LoggerWorker {

        @Override
        public boolean isLoggable(String tag) {
            return Log.isLoggable(tag, Log.DEBUG);
        }

        @Override
        public void log(String tag, String msg) {
            Log.d(tag, msg);
        }

        @Override
        public void log(String tag, String msg, Object param) {
            Log.d(tag, MessageFormat.format(msg, param));
        }

        @Override
        public void log(String tag, String msg, Object[] params) {
            Log.d(tag, MessageFormat.format(msg, params));
        }

        @Override
        public void log(String tag, String msg, Throwable throwable) {
            Log.d(tag, msg, throwable);
        }
    }

    static class VerboseLogger  implements  LoggerWorker {

        @Override
        public boolean isLoggable(String tag) {
            return Log.isLoggable(tag, Log.VERBOSE);
        }

        @Override
        public void log(String tag, String msg) {
            Log.v(tag, msg);
        }

        @Override
        public void log(String tag, String msg, Object param) {
            Log.v(tag, MessageFormat.format(msg, param));
        }

        @Override
        public void log(String tag, String msg, Object[] params) {
            Log.d(tag, MessageFormat.format(msg, params));
        }

        @Override
        public void log(String tag, String msg, Throwable throwable) {
            Log.v(tag, msg, throwable);
        }
    }

    static class WarnLogger  implements  LoggerWorker {

        @Override
        public boolean isLoggable(String tag) {
            return Log.isLoggable(tag, Log.WARN);
        }

        @Override
        public void log(String tag, String msg) {
            Log.w(tag, msg);
        }

        @Override
        public void log(String tag, String msg, Object param) {
            Log.w(tag, MessageFormat.format(msg, param));
        }

        @Override
        public void log(String tag, String msg, Object[] params) {
            Log.w(tag, MessageFormat.format(msg, params));
        }

        @Override
        public void log(String tag, String msg, Throwable throwable) {
            Log.w(tag, msg, throwable);
        }
    }

    static class ErrLogger  implements  LoggerWorker {

        @Override
        public boolean isLoggable(String tag) {
            return Log.isLoggable(tag, Log.ERROR);
        }

        @Override
        public void log(String tag, String msg) {
            Log.e(tag, msg);
        }

        @Override
        public void log(String tag, String msg, Object param) {
            Log.e(tag, MessageFormat.format(msg, param));
        }

        @Override
        public void log(String tag, String msg, Object[] params) {
            Log.e(tag, MessageFormat.format(msg, params));
        }

        @Override
        public void log(String tag, String msg, Throwable throwable) {
            Log.e(tag, msg, throwable);
        }
    }
}
