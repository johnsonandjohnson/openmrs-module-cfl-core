Feature: Patient's calendar events
  As a user I should be able to see proper messages on the calendar

  @WithPatients
  Scenario: View future Visits reminder on the calendar
    Given I log in
    Then I navigate to patient "[PATIENT] [ONE]" dashboard
    #todo: continue the scenario
    Then I close browser
