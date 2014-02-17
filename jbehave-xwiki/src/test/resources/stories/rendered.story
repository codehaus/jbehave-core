Narrative:
In order to be happy
As a user
I want to use xwiki
GivenStories:/one,/two
Scenario:XWiki
Meta:@skip true
Given a param ｟blah｠
And a param ｟bleh｠
When another param ｟1.0｠
Then the result is ｟1｠
Then a pending step (PENDING)
Examples:
{transformer=mine}
|one|two|
|1|2|