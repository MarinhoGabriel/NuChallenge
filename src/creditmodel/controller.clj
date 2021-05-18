(ns creditmodel.controller
  (:require [creditmodel.model.client :as client]
            [datomic.api :as datomic]))

(defn- create-client "Creates a client using the created model defined at `creditmodel.model.client`.
  The function receives the minus (-) sign as a visual indicator that this is a private function."
  [name cpf email]
  (client/new-client name cpf email))

(defn save-client "Saves a client in databae using the `connection` object passed as parameter.
  Inside the function, the `create-client` privte function is called to store the result value,
  representing the new client, in a symbol inside of a `let` and, after that, this value is
  used to store a new register in database."
  [name cpf email connection]
  (let [client (create-client name cpf email)]
    (datomic/transact connection [client])))