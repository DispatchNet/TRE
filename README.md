# TRE
Both the send files and the WIPs
Firstly,

	git fetch

Alternatively, to pull the repository:

	git pull <link>

For branches:

	git switch <branch_name>

To push, remember to:
	
	git add -A
	
	git commit -m <"message">

	git push

To create new tag (tag the version appropriately, see below: 
	
	git tag -a vX.Y -m "Version description"

X is only incremented for major releases, Y is for all others

	git push --tags
