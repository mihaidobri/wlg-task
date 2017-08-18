package me.maxwu.wlg;

import me.maxwu.wlg.pages.Snapable;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SnapRule implements MethodRule {
    Logger logger = LoggerFactory.getLogger(SnapRule.class.getName());

    Snapable pageSnaper;

    public void setSnapable(Snapable pageSnaper) {
        this.pageSnaper = pageSnaper;
    }

    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o){
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } catch (Throwable te) {
                    captureScreenShot();
                    // rethrow to JUnit
                    throw te;
                }
            }

            public void captureScreenShot() throws IOException {
                String name = frameworkMethod.getName();
                logger.info(">> Case " + name + " fails.");
                pageSnaper.saveScreenShot(name);
            }
        };
    }
}
