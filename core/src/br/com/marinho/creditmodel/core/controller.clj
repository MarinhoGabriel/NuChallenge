(ns br.com.marinho.creditmodel.core.controller
  (:use clojure.pprint)
  (:require [br.com.marinho.creditmodel.core.model.client :as client]
            [br.com.marinho.creditmodel.core.model.card :as card]
            [br.com.marinho.creditmodel.core.model.purchase :as purchase]
            [br.com.marinho.creditmodel.core.db.database :as db]
            [br.com.marinho.creditmodel.core.db.schema :as schemata]
            [datomic.api :as datomic]))

; URI used to connect and disconnect from the database.
(def uri nil)

; Defining the connection as nil at first to redefine ir at `define-uri` function.
(def connection nil)

(defn define-uri "Defines the value of the uri used in to connect to the database."
  [uri]
  (def uri uri))

(defn start-connection "Starts the connection with the database"
  []
  (def connection (db/open-connection uri)))

(defn start-controller "Function responsible for deleting the old database and create a
  new connection to start a new application."
  []
  ;; Creating the schemata
  (schemata/create-schemata connection))

(defn drop-database "Drops the database."
  []
  (db/drop-database uri))

(defn- create-client "Creates a client using the created model defined at
  `br.com.marinho.creditmodel.core.model.client`. The function receives the minus (-)
  sign as a visual indicator that this is a private function."
  [name cpf email]
  (client/new-client name cpf email))

(defn save-client! "Saves a client in databae using the `connection` object passed as
  parameter. Inside the function, the `create-client` privte function is called to
  store the result value, representing the new client, in a symbol inside of a `let`
  and, after that, this value is used to store a new register in database."
  [name cpf email]
  (let [client (create-client name cpf email)]
    (datomic/transact connection [client])))

(defn get-clients "Returns all the clients stores in the database using a map structure,
  with `pull` command."
  []
  (datomic/q '[:find (pull ?e [:client/name :client/email :client/cpf {:client/card [*]}])
               :where [?e :client/name]] (datomic/db connection)))

(defn- save-card! "Saves a card in the database using the model created at
  `br.com.marinho.creditmodel.core.model.card`. The function receives the minus (-)
  sign as a visual indicator that this is a private function. The function returns the
  inserted object."
  [card]
  @(datomic/transact connection [card]))

(defn add-card-to-client! "Adds a card to the client, using the CPF.
  The function creates a new card using the `card/new-card` function and stores it in
  database inside a threading first execution. This threading first gives us the id of
  the insertion that was made in database to use this id as the value for the client's
  card. The last operation the function does is transacting the update in the client.

  *Important to say that, in Datomic, the value is associated to the key the same as the
  key is associated to the value. That means that inside of the object `:client/card`,
  there is a client and so on."
  [client-cpf]
  (let [card (card/new-card)]
    (let [saved-card-id (-> (save-card! card) :tempids vals first)
          client (datomic/q '[:find ?e
                              :in $ ?client-cpf
                              :keys id
                              :where [?e :client/cpf ?client-cpf]]
                            (datomic/db connection) client-cpf)
          client-id (:id (get client 0))]
      (datomic/transact connection [[:db/add client-id :client/card saved-card-id]]))))

(defn card-by-client "Returns the card given the client CPF."
  [client-cpf]
  (datomic/q '[:find ?card
               :in $ ?client-cpf
               :keys id
               :where [?e :client/cpf ?client-cpf]
               [?e :client/card ?card]]
             (datomic/db connection) client-cpf))

(defn card-by-number "Returns a card given its number."
  [number]
  (datomic/q '[:find (pull ?e [*])
               :in $ ?number
               :where [?e :card/number ?number]]
             (datomic/db connection) number))

(defn save-purchase! "Saves a new purchase register in database.
  At first, the function get the card id from the client who made the purchase, that is
  represented by the `client-cpf` parameter. After getting the card id, the function
  just creates a new purchase object and saves it in database."
  [merchant category value date client-cpf]
  (if-not (not-any? nil? [merchant category value date client-cpf])
    (throw (NullPointerException. "Fields cannot be nil."))
    (do
      (let [card-id (:id (get (card-by-client client-cpf) 0))]
        (if (nil? card-id)
          (throw (NullPointerException. "Client has not a card."))
          (datomic/transact connection [(purchase/new-purchase merchant category value
                                                               date card-id)]))))))

(defn get-purchase "Returns the purchase list with all information about the purchase,
  including the card number and the clients' name and cpf."
  []
  (datomic/q '[:find ?merchant ?category ?price ?date ?number ?name ?cpf
               :keys purchase/merchant purchase/category purchase/price purchase/date card/number client/name client/cpf
               :where [?e :purchase/merchant ?merchant]
               [?e :purchase/category ?category]
               [?e :purchase/value ?price]
               [?e :purchase/date ?date]
               [?e :purchase/card ?card]
               [?card :card/number ?number]
               [?client :client/card ?card]
               [?client :client/name ?name]
               [?client :client/cpf ?cpf]] (datomic/db connection)))

(defn purchases-by-client? "Returns all purchases made by a client with the CPF value
  equals to `client-cpf` parameter. Differently from the method above, this method does
  not return the clients' name and CPF because we're talking about all purchases made
  by JUST ONE client."
  [client-cpf]
  (datomic/q '[:find ?merchant ?category ?price ?date ?number
               :in $ ?client-cpf
               :keys purchase/merchant purchase/category purchase/price purchase/date card/number
               :where [?e :purchase/merchant ?merchant]
               [?e :purchase/category ?category]
               [?e :purchase/value ?price]
               [?e :purchase/date ?date]
               [?e :purchase/card ?card]
               [?card :card/number ?number]
               [?client :client/cpf ?client-cpf]
               [?client :client/card ?card]] (datomic/db connection) client-cpf))

(defn max-purchase-count-by-client? "Returns the name of the name of the client who has
  the higher number of purchases saved in the database.
  The method used a threading last to:
    1. get the map of `total_purchases` by `client_name`
    2. getting the higher key
    3. get the value of the map element with that key."
  []
  (->> (datomic/q '[:find (count ?purchase) ?client-name
                    :keys total client
                    :where [?card :card/number]
                    [?purchase :purchase/card ?card]
                    [?client :client/card ?card]
                    [?client :client/name ?client-name]] (datomic/db connection))
       (apply max-key :total)
       (:client)
       (println "The client who made more purchases was")))

(defn most-valued-purchase? "Returns the name of the client who made the most valued
  purchase."
  []
  (->> (datomic/q '[:find ?name
                    :where [(q '[:find (max ?value)
                                 :where [_ :purchase/value ?value]]
                               $) [[?value]]]
                    [?e :purchase/value ?value]
                    [?e :purchase/card ?number]
                    [?client :client/card ?number]
                    [?client :client/name ?name]] (datomic/db connection))
       ffirst
       (println "Most valued purchase made by")))

(defn client-with-no-purchase? "Returns the name of the client who does not made any
  purchases."
  []
  (let [client (->> (datomic/q '[:find ?name
                                 :where [?card :card/number]
                                 [?client :client/card ?card]
                                 [?client :client/name ?name]
                                 (not-join [?card]
                                           [?purchase :purchase/card ?card])
                                 ], (datomic/db connection))
                    ffirst)]
    (if client
      (str (str client) " hasn't made any purchase.")
      (str "All clients have made at least one purchase."))))