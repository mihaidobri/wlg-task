package me.maxwu.wlg;

import me.maxwu.wlg.log.ColorStr;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Watcher to monitor case results.
 * It overrides start, failed and succeeded interfaces to hook logs.
 *
 * INFO: Actions could be hooked in this class for statistics works.
 */
class ResultWatcher extends TestWatcher {
    static Logger logger = LoggerFactory.getLogger(ResultWatcher.class.getName());

    @Override
    public void failed(Throwable e, Description description) {
        logger.info(ColorStr.red("☂ ☹ Case " + description.getMethodName() + " fails\n"));
    }

    @Override
    public void succeeded(Description description) {
        logger.info(ColorStr.green("☀ ☺ Case " + description.getMethodName() + " succeeds\n"));
    }

    @Override
    protected void starting(Description description) {
        logger.info("\n-------------------------------------------------------------------------------------");
        logger.info(ColorStr.blue("♫ ☞ Case " + description.getMethodName() + " starts"));
    }

}
