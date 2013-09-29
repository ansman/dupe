DUPE - Distributed Standup Enabler
===
Virtual, distributed stand-ups. You enter what you'll be working on today and can add new tasks, comments and notes as you go. You can see what your co-workers are working on right now.

Technologies used
===
 * Clojure
 * ring
 * http-kit
 * compojure
 * clojurescript
 * shoreleave
 * dommy
 * clojurescript.test
 * Ubuntu
 * MySQL
 * Supervisor
 * Nginx
 * Github API for OAuth

How to run
===
Server
---
```
lein run
```

Client
---
```
lein cljsbuild once development
open public/index.html
```

**Note:** Authing does not work locally yet.

Features
===
 * GitHub authentication
 * Creating a new schedule
 * Marking tasks as done
 * Adding unplanned tasks

TODO
===
 * Comments
 * Viewing other peoples' schedule
