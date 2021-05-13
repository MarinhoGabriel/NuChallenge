(ns creditmodel.shopping-db
  (:require [creditmodel.client-db :as client]))

(def shopping {})

(defn add-shopping
  [card-number date value merchant category]
  (def shopping (assoc shopping (keyword (str (count shopping))) {
                                                                  :card-number card-number
                                                                  :date        date
                                                                  :value       value
                                                                  :merchant    merchant
                                                                  :category    category})))

(defn shopping-list
  [client-cpf]
  (let [client (client/get-client-by-cpf client-cpf)]
    (filter #(= (:card-number (get % 1)) (get-in (get client 1) [:card :number])) shopping)))

(defn shopping-by-category
  []
  (group-by :category shopping))

(add-shopping 1234 "10/02" 129 "Mc" "food")
(add-shopping 1234 "10/02" 129 "Bk" "food")
(add-shopping 1234 "11/11" 129 "Hering" "clothing")
(add-shopping 1234 "9/09" 222 "Nike" "clothing")
(add-shopping 1234 "4/05" 30 "Adidas" "clothing")
(add-shopping 1234 "1/02" 129 "Mc" "food")
;(println shopping)

(defn shopping-map
  [[_ value]]
  {:category (:category value) :value (:value value)})
git remote add origin https://github.com/MarinhoGabriel/NuChallenge.git
(defn group-shopping
  []
  (group-by :category (map shopping-map shopping)))

(defn reduce-shopping
  [e1, e2]
  {:category (:category e1) :value (+ (:value e1) (:value e2))})

(println (map #((reduce reduce-shopping %) %) (vals (group-shopping))))