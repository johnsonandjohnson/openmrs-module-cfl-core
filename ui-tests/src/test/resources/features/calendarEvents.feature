Feature: Patient's calendar events
  As a user I should be able to see proper messages on the calendar

  @WithPatients
  Scenario: View today's Adherence report daily on the calendar
    When I navigate to patient "[PATIENT] [ONE]" messages
    And element having xpath "//h3[contains(text(),'Calendar Overview')]" should be present
    And I wait and click on element having xpath "//button[contains(text(),'Manage messages')]"
    And element having xpath "//h2[contains(text(),'Manage messages')]" should be present
    And I pick best contact time for 2 minutes in the future
    And I wait and click on element having xpath "//button[contains(text(),'Save')]"
    And I click on element having xpath "//a[contains(@href,'Adherence report daily')]"
    And I click on element having xpath "(//div[contains(@id,'Adherence report daily')]//input[@name='Call'])[1]"
    And I ensure today is checked as a weekday of delivering messages
    And I click on element having xpath "//button[contains(text(),'Save')]"
    And I refresh page
    Then today's calendar tile contains "Adherence report daily"
