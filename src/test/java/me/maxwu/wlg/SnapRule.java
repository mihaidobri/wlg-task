package me.maxwu.wlg;

import me.maxwu.wlg.models.ISnapable;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * JUnit Rule to take screenshot on failure.
 */
public class SnapRule implements MethodRule {
    Logger logger = LoggerFactory.getLogger(SnapRule.class.getName());

    ISnapable pageSnaper;

    public void setSnapable(ISnapable pageSnaper) {
        this.pageSnaper = pageSnaper;
    }

    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o){
        return new Statement() {
            String name = null;

            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } catch (Throwable te) {
                    name = frameworkMethod.getName();
                    logger.debug("Taking screenshot for case " + name);

                    captureScreenShot(name);

                    // Rethrow the exception to JUnit
                    throw te;
                }
            }

            public void captureScreenShot(String caseName) throws IOException {
                pageSnaper.saveScreenShot(caseName);
            }
        };
    }
}
