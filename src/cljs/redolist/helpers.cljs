(ns redolist.helpers
  (:require [clojure.string :as str]))

;; this should really be a macro but it's fine for now
(defn class-> [& pairs]
  (->> pairs
       (partition 2)
       (map (fn [[pred klz]]
              (if pred klz)))
       (filter identity)
       (str/join " ")))
