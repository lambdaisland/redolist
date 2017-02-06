(ns redolist.views
  (:require [re-frame.core :as re-frame :refer [subscribe]]
            [redolist.helpers :refer [class->]]))

(defn todo-checkbox [id completed]
  [:input.toggle {:type "checkbox"
                  :checked completed}])

(defn todo-item [todo]
  (fn [{:keys [id completed title editing] :as todo}]
    [:li {:class (class-> completed "completed "
                          editing   "editing")}
     [:div.view
      [todo-checkbox id completed]
      [:label
       {:unselectable "on"
        :style {:user-select "none"
                :-moz-user-select "none"}}
       title]
      [:button.destroy {}]]]))

(defn todo-list []
  (let [todos (subscribe [:visible-todos])]
    (fn []
      [:div#todo-list
       (for [todo @todos]
         ^{:key (:id todo)} [todo-item todo])])))

(defn main-panel []
  [:div
   [:section#todoapp
    [:div
     [:section#main
      [todo-list]]]]])
