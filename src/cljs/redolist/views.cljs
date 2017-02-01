(ns redolist.views
  (:require [re-frame.core :as re-frame :refer [dispatch subscribe]]
            [reagent.core :as r :refer [adapt-react-class]]
            [redolist.helpers :refer [class->]]))

(def css-transition-group
  (adapt-react-class js/React.addons.CSSTransitionGroup))

(defn <sub [sub]
  (deref (subscribe sub)))

(defn >evt [event]
  (dispatch event))

(def enter-key-code 13)

(defn when-enter [key-event callback]
  (let [key-pressed (.-which key-event)]
    (when (= key-pressed enter-key-code)
      (callback))))

(defn todos-toggle []
  [:span
   [:input#toggle-all {:type "checkbox"
                       :checked (<sub [:todos/all-complete?])
                       :on-change #(>evt [:todos/toggle-all])}]
   [:label {:for "toggle-all"} "Mark all as complete"]])

(defn todo-input []
  (let [title (r/atom "")]
    (fn []
      [:input#new-todo {:type "text"
                        :value @title
                        :placeholder "What needs to be done?"
                        :auto-focus true
                        :on-change #(reset! title (-> % .-target .-value))
                        :on-key-down #(when-enter %
                                        (fn []
                                          (>evt [:todos/add @title])
                                          (reset! title "")))}])))

(defn todo-checkbox [id completed]
  [:input.toggle {:type "checkbox"
                  :checked completed
                  :on-change #(dispatch [:todos/toggle id])}])

(defn todo-edit [todo]
  (let [title (r/atom (:title todo))]
    (fn [{:keys [id] :as todo}]
      (let [dispatch-update #(>evt [:todos/update id {:title @title
                                                      :editing false}])]
        [:input.edit {:type "text"
                      :value @title
                      :on-change #(reset! title (-> % .-target .-value))
                      :on-blur dispatch-update
                      :on-key-down #(when-enter % dispatch-update)
                      :auto-focus true}]))))

(defn todo-item [todo]
  (fn [{:keys [id completed title editing] :as todo}]
    [:li {:class (class-> completed "completed "
                          editing   "editing")}
     [:div.view
      [todo-checkbox id completed]
      [:label
       {:on-double-click #(>evt [:todos/update id {:editing true}])
        :unselectable "on"
        :style {:user-select "none"
                :-moz-user-select "none"}}
       title]
      [:button.destroy {:on-click #(>evt [:todos/remove id])}]]
     (if editing
       [todo-edit todo])]))

(defn todos-list []
  [:ul#todo-list
   (for [todo (<sub [:todos/visible])]
     ^{:key (:id todo)} [todo-item todo])])



(defn todos-count []
  (let [active-count (<sub [:todos/active-count])]
    [:span#todo-count
     [:strong active-count]
     (if (= 1 active-count) " item " " items ") "left"]))

(defn todos-filters []
  (let [display-type (<sub [:display-type])]
    [:ul#filters
     [:li [:a {:class (class-> (= display-type :all) "selected")  :href "#/"} "All"]]
     [:li [:a {:class (class-> (= display-type :active) "selected") :href "#/active"} "Active"]]
     [:li [:a {:class (class-> (= display-type :completed) "selected") :href "#/completed"} "Completed"]]]))

(defmulti notif-msg identity)

(defmethod notif-msg :todos/added
  [_ {:keys [id title]}]
  (str "Added todo: " title))

(defn notification [n]
  [:div.notification
   (apply notif-msg n)])

(defn notifications []
  [:div#notifications
   [css-transition-group {:transitionName "notification"
                          :transitionEnterTimeout 500
                          :transitionLeaveTimeout 300}
    (for [n (<sub [:notifications])]
      ^{:key (pr-str n)} [notification n])]])

(defn main-panel []
  [:div
   [:section#todoapp
    [:header#header
     [todo-input]]
    (if-not (<sub [:todos/empty?])
      [:div
       [:section#main
        [todos-toggle]
        [todos-list]]
       [:footer#footer
        [todos-count]
        [todos-filters]]])]
   [notifications]])
