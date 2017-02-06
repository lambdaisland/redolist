(ns redolist.db)

(def default-db
  {:todos {1 {:id 1
              :title "Save the world"
              :completed false}}
   :display-type :all})
