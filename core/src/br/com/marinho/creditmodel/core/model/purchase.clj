(ns br.com.marinho.creditmodel.core.model.purchase
  (:import (java.util UUID)))

(defn generate-uuid "Generates a new unique UUID used in the purchase registers."
  []
  (UUID/randomUUID))

(defn new-purchase "Creates a new purchase register"
  ([merchant category value date card]
   (new-purchase (generate-uuid) merchant category value date card))

  ([uuid merchant category value date card]
   {:purchase/id       uuid
    :purchase/merchant merchant
    :purchase/category category
    :purchase/value    value
    :purchase/date     date
    :purchase/card     card}))