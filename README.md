# Calculator Test

Task description:
> Choice 1: Automation Exercise on automating UI component for ANZ Repayments Calculator and Borrowing Calculator
  Navigate to  ANZ Mortgage calculator - https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/. In the left-hand pane under Mortgage calculator, there are two calculators, Repayments Calculator, and Borrowing Calculator.
  
> Exercise: Create some tests that execute the Repayments Calculator and Borrowing Calculator UI component in the manner it is intended.


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
  

## Backlogs
 - Refactor Repayment Scenario to a separate type and simplify the codes.
 - Refactor visibility, fluent wait and explicit wait to abstraction type.
 - Add pairwise story test as an end-to-end story to test from borrowing calculator and verify the suggested plan with repayment calculator.