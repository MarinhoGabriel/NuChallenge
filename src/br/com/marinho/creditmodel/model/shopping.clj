(ns br.com.marinho.creditmodel.model.shopping
  (:import (java.util UUID)))

(defn generate-uuid "Generates a new unique UUID used in the shopping registers."
  []
  (UUID/randomUUID))

(defn new-shopping "Creates a new shopping register"
  ([merchant category value date card]
   (new-shopping (generate-uuid) merchant category value date card))

  ([uuid merchant category value date card]
   {:shopping/id       uuid
    :shopping/merchant merchant
    :shopping/category category
    :shopping/value    value
    :shopping/date     date
    :shopping/card     card}))