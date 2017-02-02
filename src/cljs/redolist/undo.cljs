(ns redolist.undo
  (:require [re-frame.core :as re-frame]))

(def undo-stack (atom ()))

(def undoable
  (re-frame/->interceptor
   :id :undoable
   :before (fn [context]
             (swap! undo-stack #(conj % (get-in context [:coeffects :db])))
             context)))

(defn undo-handler [db-now _]
  (let [db-undo (first @undo-stack)]
    (swap! undo-stack next)
    (if (nil? db-undo)
      db-now
      db-undo)))
