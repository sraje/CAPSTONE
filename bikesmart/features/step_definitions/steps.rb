## Test Step Definitions

## Starting Points
Given(/^I have logged into Bikesmart$/) do
	element_exists("EditText id:'latitude_text'")
end

Given(/^I am on the home screen$/) do
	element_exists("Button id:'logout_button'")
end

Given(/^I am on the login screen$/) do
	element_exists("Button id:'signup_button'")
end

When(/^I press Get Location$/) do
	touch("Button id:'location_button'")
end

## Verification Steps
Then(/^I should be taken back to the Bikesmart login screen$/) do
	steps%{I am on the login screen}
end

Then(/^I should be taken to the Bikesmart home screen$/) do
	steps%{I am on the home screen}
end

Then(/^I should be presented with the bike's current location$/) do
end

## Actions Steps
When(/^I enter valid credentials and press login$/) do
end

When(/^I press logout$/) do
	touch("Button id:'logout_button'")
end

When(/^I press Sign Up$/) do
	touch("Button id:'signup_button'")
end

When(/^I enter a valid username and password$/) do
end

