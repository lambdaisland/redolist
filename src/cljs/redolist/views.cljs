(ns redolist.views
  (:require [re-frame.core :as re-frame :refer [subscribe]]
            [reagent.core :as r]
            [redolist.helpers :refer [class->]]))

+(defn todo-input []
  (let [title (r/atom "")]
    (fn []
      [:input#new-todo {:type "text"
                        :value @title
                        :placeholder "What needs to be done?"
                        :auto-focus true
                        :on-change #(reset! title (-> % .-target .-value))}])))


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
  (let [todos (subscribe [:todos])]
    (fn []
      [:div#todo-list
       (for [todo @todos]
         ^{:key (:id todo)} [todo-item todo])])))

(defn todos-filters []
  (let [display-type (subscribe [:display-type])]
    (fn []
      (let [selected #(if (= @display-type %) "selected" "")]
        [:ul#filters
         [:li [:a {:class (selected :all)  :href "#/"} "All"]]
         [:li [:a {:class (selected :active) :href "#/active"} "Active"]]
         [:li [:a {:class (selected :completed) :href "#/completed"} "Completed"]]]))))

(defn main-panel []
  [:div
   [:section#todoapp
    [:div
     [:header#header
      [todo-input]]
     [:section#main
      [todo-list]]
     [:footer#footer
      [todos-filters]]]]])
