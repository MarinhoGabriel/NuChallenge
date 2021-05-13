(ns creditmodel.client-db
  (:require [creditmodel.card-db :as card]))

(def clients {})

(defn add-client
  "Function responsible for adding new clients to the clients array."
  [name email]
  (def clients (assoc clients (keyword (str (count clients))) {
                                                               :name  name
                                                               :cpf   (str 0 (str (count clients)))
                                                               :email email
                                                               :card  nil})))

(defn get-clients
  "Function responsible for printing the information of all clients saved."
  []
  (println clients))

(defn get-client-by-name
  "Returns a client given the name."
  [name]
  (nth (filter #(= (:name (get % 1)) name) clients) 0))

(defn get-client-by-cpf
  "Returns a client given the CPF."
  [cpf]
  (nth (filter #(= (:cpf (get % 1)) cpf) clients) 0))

(defn get-card-by-client-cpf
  "Returns a client card given the CPF."
  [cpf]
  (get (nth (filter #(= (:cpf %) cpf) (vals clients)) 0) :card))

(defn add-card
  "Adds a card to a client, passing the CPF"
  [client-cpf]
  (let [client (get-client-by-cpf client-cpf)]
    (let [updated-client (assoc (get client 1) :card {
                                                      :number   (str (long (rand 10000000000000000)))
                                                      :cvv      (str (long (rand 1000)))
                                                      :validity (str (+ 1 (rand-int 12)) "/" (+ 1 (rand-int 24)))
                                                      :limit    200})]
      (def clients (assoc clients (get client 0) updated-client))
      (card/add-card (:card updated-client) (:cpf updated-client)))))