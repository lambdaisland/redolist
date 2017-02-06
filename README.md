# redolist

A [re-frame](https://github.com/Day8/re-frame) implementation of the popular TodoMVC app, as seen on [Lambda Island](http://lambdaisland.com/)

- [Episode 20. re-frame Subscriptions](https://lambdaisland.com/episodes/re-frame-subscriptions)
- Episode 25. re-frame Events and Effects (upcoming)

## Development Mode

### Install TodoMVC styles

```
npm install
```

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
