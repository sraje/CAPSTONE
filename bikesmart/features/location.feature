Feature: Location services

Scenario: As a developer, I should be able to get location data from Bikesmart

Given I have logged into Bikesmart
When I press Get Location
Then I should be presented with the bike's current location
