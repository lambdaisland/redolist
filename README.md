# redolist

A [re-frame](https://github.com/Day8/re-frame) implementation of the popular TodoMVC app, as seen on [Lambda Island](http://lambdaisland.com/)

- [Episode 20. re-frame Subscriptions](https://lambdaisland.com/episodes/re-frame-subscriptions)
- Episode 25. re-frame Events and Effects (upcoming)

## Development Mode

### Run application:

This app uses [Component](https://github.com/stuartsierra/component) and
[reloaded.repl](https://github.com/juxt/reloaded.repl). Start a REPL and type
`(go)` to boot up the system. This will compile start Figwheel, and compile
Garden to CSS.

```
lein repl
user=> (go)
```

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

You can switch to a browser-connected ClojureScript REPL with `(cljs-repl)`.

## Emacs / CIDER

If you're using CIDER, open `project.clj`. You will be asked to agree to the
variables set through `.dir-locals.el`. After that you can execute
`cider-jack-in-clojurescript` (C-c M-J), to boot the system and get both a
Clojure and ClojureScript REPL buffer.

## Production Build

To compile ClojureScript to Javascript, and Garden to CSS:

```
lein clean
lein garden once
lein cljsbuild once min
```
