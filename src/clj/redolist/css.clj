(ns redolist.css
  (:require [garden-watcher.def :refer [defstyles]]))

(defstyles screen
  [:#notifications {:position "absolute"
                    :bottom "0"
                    :right "50px"}]
  [:.notification {:width "350px"
                   :padding "1em"
                   :margin-bottom "1em"
                   :background-color "rgba(220, 220, 250, 0.5)"
                   :font-weight "bold"
                   :box-shadow "0 2px 4px 0 rgba(0, 0, 0, 0.2), 0 5px 10px 0 rgba(0, 0, 0, 0.1)"
                   :font-size "120%"}]


  [:.notification-enter {:opacity 0.01}]

  [:.notification-enter.notification-enter-active {:opacity 1
                                                   :transition "opacity 500ms ease-in"}]

  [:.notification-leave {:opacity 1}]

  [:.notification-leave.notification-leave-active {:opacity 0.01
                                                   :transition "opacity 300ms ease-in"}])
