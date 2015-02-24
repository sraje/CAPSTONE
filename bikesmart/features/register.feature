Feature: Register feature

Scenario: As a first time user, I should be able to create a Bikesmart profile 
	Given I am on the login screen 
	When I press Sign Up
	And I enter a valid username and password into the registration fields
	Then I should be taken to the Bikesmart home screen 
	
