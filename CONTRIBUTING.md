# CONTRIBUTING

OpenPool project welcomes new contributors.  This document will guide you
through the process.

### FORK

Fork the project [on GitHub](https://github.com/openpool/openpool-core) 
and check out your copy.  For keeping code quarities of our project, we separate 
devel(opment) branch from master branch, so you HAVE TO checkout local branch 
from "devel" and send pull request into it.  Also we recommend you to make 
your own topic branch (like develSpike) for managing your commit.
```
$ git clone git@github.com:YOURNAME/openpool-core.git
$ cd openpool-core
$ git remote add openpool git@github.com:openpool/openpool-core.git
$ git checkout -b devel remotes/openpool/devel
$ git checkout -b develSpike
```

### COMMIT

Writing good commit logs is important.  Our recommendation is to make commits
as meaningful small change, and gather it before pushing.
```
$ git commit -m "short comment"
```

### REBASE

Use `git rebase` (not `git merge`) to sync your work from time to time.

```
$ git checkout -B devel remotes/openpool/devel
$ git rebase devel develSpike
```

### TEST

At least, run ant before you push project codes. Confirm all builds run correctly.
```
$ cd resources
$ ant resources/build.xml
$ cd ../
```

Please, do not submit patches that fail check.

### PUSH
```
$ git push origin -f develSpike
```

Go to https://github.com/YOURNAME/openpool-core and select your topic branch.
Click the 'Pull Request' button, select "devel" as a base branch and fill out the form.  
Pull requests are usually reviewed within a few days.








