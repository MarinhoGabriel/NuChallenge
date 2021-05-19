(ns creditmodel.model.shopping)

(defn new-shopping "Creates a new shopping register"
  [merchant category value date card]
  {:shopping/merchant merchant
   :shopping/category category
   :shopping/value    value
   :shopping/date     date
   :shopping/card     card})