(ns redolist.db)

(def default-db
  {:todos [{:id (random-uuid)
            :title "Donate to charity"
            :completed false}]})
