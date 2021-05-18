(ns creditmodel.card-db)

(def cards {})

(defn add-card
  [card client-cpf]
  (def cards (assoc cards (keyword (str (count cards))) {
                                                         :number     (:number card)
                                                         :cvv        (:cvv card)
                                                         :validity   (:validity card)
                                                         :limit      (:limit card)
                                                         :client-cpf client-cpf})))