# TRE
Both the send files and the WIPs
Firstly,

	git pull

For branches:

	git switch <branch_name>

To push, remember to:
	
	git add -A
	
	git commit -m <"message">

	git push

To create new tag (tag the version appropriately, see below: 
	
	First, find your commit to tag (usually at the top) from the list
	
	git log --pretty=oneline

	Then, Take the first characters of its hash string, to tag the commit
	
	git tag -a v<X>.<Y> -m "Version description" <First characters>

	Remeber then to also push the tags, after pushing to remote
	
	git push --tags
	
X is only incremented for major releases, Y is for all others
