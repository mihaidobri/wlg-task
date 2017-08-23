# Calculator Test

[![Build Status](https://travis-ci.org/maxwu/wlg-task.svg?branch=dev)](https://travis-ci.org/maxwu/wlg-task)
[![codecov](https://codecov.io/gh/maxwu/wlg-task/branch/dev/graph/badge.svg)](https://codecov.io/gh/maxwu/wlg-task)
[![Build Status](https://travis-ci.org/maxwu/wlg-task.svg?branch=master)](https://travis-ci.org/maxwu/wlg-task)
[![codecov](https://codecov.io/gh/maxwu/wlg-task/branch/master/graph/badge.svg)](https://codecov.io/gh/maxwu/wlg-task)


Task description:
> Automation Exercise on automating UI component for ANZ Repayments Calculator and Borrowing Calculator
  Navigate to  ANZ Mortgage calculator - https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/. In the left-hand pane under Mortgage calculator, there are two calculators, Repayments Calculator, and Borrowing Calculator.
  
> Exercise: Create some tests that execute the Repayments Calculator and Borrowing Calculator UI component in the manner it is intended.

## Test Procedure

### Launch JUnit Test with Maven

The test whole suite with me.maxwu.wlg package under /test folder holds 4 suite classes. To perform a full cycle, readers can refer to below command:

```bash
> mvn clean test
```

To run a specific test case, here is a sample to refer to. 
This example command is to launch test with JUnit test method "scenario0TooltipTest" in class me.maxwu.elg.RepayCalTest.

```bash
> mvn test-complie surefire:test -Dtest=me.maxwu.wlg.RepayCalTest#scenario0TooltipTest
``` 

By the way, it is supported to use "*" to wildcard multiple tests.

```bash
> mvn clean test -Dtest=me.maxwu.wlg.Borrow*
```

Two environment variables are introduced to control browser type and headless mode. Both are case insensitive.
 - "browser": the browser type could be "chrome" or "ff"/"firefox".
 - "headless": "1" to enable headless mode, "0" to disable it.

By default, the test is performed with Chrome in regular (not in headless) mode on travis and development environment. 
However, Travis config file defines global environment headless=1 to enable headless mode on cloud.
For setting up local development environment, it is recommended to setup IDE config record to default Maven/JUnit test to headless mode. 

### Test Reports

The popular test report format is XUnit compatible XML. 
Surefire plugin summarize XUnit reports to $mvn_root/target/surefire-reports folder. This XUnit format is widely accepted as on Jenkins and other CI/CD platform.
Surefire-report plugin supports to generate HTML report as test summary.  

```bash
> mvn clean surefire-report:report
```  
Surefire HTML format report is in $mvn_root/target/site folder by default.

Code coverage report is located at $mvn_root/target/site/jacoco. 

### Project Descriptions

The Calculator Project is a sample of web automation solution as a Maven managed Java project with Java 8.
HTML format report could be triggered by `mvn surefire-report:report`.

For more details on dependencies installation, readers could find them in ".travis.yml", the YAML config for Travis.

## Work Notes
 - 20 Aug: Analysis on page structures and framework setup.
   - Add smoke test with mortgage calculators page and repayment calculator page.
   - Add common testing supports.
                     
 - 21 Aug: Refactor code and add borrowing calculator.
   - Add sunny day and rainy day mock cases.
   - Introduce ways to work around element invisible issue.
 
 - 22 Aug: Implement mock cases and fixing issues.
   - Implement borrowing calculator mock cases to test cases.
   - Fixing interaction issues with fluent wait.
   
 - 23 Aug: Migrate to travis with travis.yml. 
   - Add headless chrome and mvn test launcher command to travis cfg.
   - Add codecov metric on page object model code coverage status.

## Issues 
1.  If conditions are changed on current scenario but it is not calculated yet.
    At this time, the current scenario is duplicated. The duplicated scenario will copy the all the conditions but new scenario will automatically calculate the new results.
  

## Rest Backlogs
 - Refactor Repayment Scenario to a separate type and simplify the codes.
 - Refactor visibility, fluent wait and explicit wait to abstraction type.
 - Add pairwise story test as an end-to-end story to test from borrowing calculator and verify the suggested plan with repayment calculator.