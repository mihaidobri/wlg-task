# Calculator Test

[![Build Status](https://travis-ci.org/maxwu/wlg-task.svg?branch=dev)](https://travis-ci.org/maxwu/wlg-task)
[![codecov](https://codecov.io/gh/maxwu/wlg-task/branch/dev/graph/badge.svg)](https://codecov.io/gh/maxwu/wlg-task)
[![Build Status](https://travis-ci.org/maxwu/wlg-task.svg?branch=master)](https://travis-ci.org/maxwu/wlg-task)
[![codecov](https://codecov.io/gh/maxwu/wlg-task/branch/master/graph/badge.svg)](https://codecov.io/gh/maxwu/wlg-task)


Task description:
> Automation Exercise on automating UI component for ANZ Repayments Calculator and Borrowing Calculator
  Navigate to  ANZ Mortgage calculator - https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/. In the left-hand pane under Mortgage calculator, there are two calculators, Repayments Calculator, and Borrowing Calculator.
  
> Exercise: Create some tests that execute the Repayments Calculator and Borrowing Calculator UI component in the manner it is intended.

## Test Execution

The test whole suite with me.maxwu.wlg package under /test folder holds 4 suite classes. To perform a full cycle, readers can refer to below command:

```bash
> mvn clean test
```

To run a specific test case, here is a sample to refer to. 
This example command is to launch test with JUnit test method "scenario0TooltipTest" in class me.maxwu.elg.RepayCalTest.

```bash
> mvn test-complie surefire:test -Dtest=me.maxwu.wlg.RepayCalTest#scenario0TooltipTest
``` 

Two environment variables are introduced to control browser type and headless mode. Both are case insensitive.
 - "browser": the browser type could be "chrome" or "ff"/"firefox".
 - "headless": "1" to enable headless mode, "0" to disable it.

By default, the test is performed with Chrome in regular (not in headless) mode on travis and development environment. However, Travis config file defines global environment headless=1 to enable headless mode on cloud.
For setting up local development environment, it is recommended to setup IDE config record to default Maven/JUnit test to headless mode. 
 

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

## Issues 
1.  If conditions are changed on current scenario but it is not calculated yet.
    At this time, the current scenario is duplicated. The duplicated scenario will copy the all the conditions but new scenario will automatically calculate the new results.
  

## Rest Backlogs
 - Refactor Repayment Scenario to a separate type and simplify the codes.
 - Refactor visibility, fluent wait and explicit wait to abstraction type.
 - Add pairwise story test as an end-to-end story to test from borrowing calculator and verify the suggested plan with repayment calculator.