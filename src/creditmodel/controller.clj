(ns creditmodel.controller
  (:use clojure.pprint)
  (:require [creditmodel.model.client :as client]
            [creditmodel.model.card :as card]
            [creditmodel.model.shopping :as shopping]
            [creditmodel.db.database :as db]
            [creditmodel.db.schema :as schemata]
            [datomic.api :as datomic]))

; Defining the connection as nil at first to redefine ir at `start-controller` method
(def connection nil)

(defn start-controller "Function responsible for deleting the old database and create a new
  connection to start a new application."
  []
  ; Dropping database if exists
  (db/drop-database)

  ; Database connecton
  (def connection (db/open-connection))

  ;; Creating the schemata
  (schemata/create-schemata connection))

(defn- create-client "Creates a client using the created model defined at `creditmodel.model.client`.
  The function receives the minus (-) sign as a visual indicator that this is a private function."
  [name cpf email]
  (client/new-client name cpf email))

(defn save-client "Saves a client in databae using the `connection` object passed as parameter.
  Inside the function, the `create-client` privte function is called to store the result value,
  representing the new client, in a symbol inside of a `let` and, after that, this value is
  used to store a new register in database."
  [name cpf email]
  (let [client (create-client name cpf email)]
    (datomic/transact connection [client])))

(defn- save-card "Saves a card in the database using the model created at `creditmodel.model.card`.
  The function receives the minus (-) sign as a visual indicator that this is a private function.
  The function returns the inserted object."
  [card]
  @(datomic/transact connection [card]))

(defn add-card-to-client "Adds a card to the client, using the CPF.
  The function creates a new card using the `card/new-card` function and stores it in database inside
  a threading first execution. This threading first gives us the id of the insertion that was made in
  database to use this id as the value for the client's card.
  The last operation the function does is transacting the update in the client.

  *Important to say that, in Datomic, the value is associated to the key the same as the key is
  associated to the value. That means that inside of the object `:client/card`, there is a client
  and so on."
  [client-cpf]
  (let [card (card/new-card)]
    (let [saved-card-id (-> (save-card card) :tempids vals first)
          client (datomic/q '[:find ?e
                              :in $ ?client-cpf
                              :where [?e :client/cpf ?client-cpf]]
                            (datomic/db connection) client-cpf)
          client-id (get (first client) 0)]
      (datomic/transact connection [[:db/add client-id :client/card saved-card-id]]))))

(defn card-by-client "Returns the card given the client CPF."
  [client-cpf]
  (datomic/q '[:find ?card
               :in $ ?client-cpf
               :where [?e :client/cpf ?client-cpf]
               [?e :client/card ?card]]
             (datomic/db connection) client-cpf))

(defn save-shopping "Saves a new shopping register in database"
  [merchant category value date client-cpf]
  (let [card-id (get (first (card-by-client client-cpf)) 0)]
    (datomic/transact connection [(shopping/new-shopping merchant category value date card-id)])))