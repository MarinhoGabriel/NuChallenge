(ns creditmodel.db.schema
  (:use clojure.pprint)
  (:require [datomic.api :as datomic]))

; Schema to represent a client on database.
(def client-schema [
                    {:db/ident       :client/name
                     :db/valueType   :db.type/string
                     :db/cardinality :db.cardinality/one
                     :db/doc         "The name of the client"}
                    {:db/ident       :client/cpf
                     :db/valueType   :db.type/string
                     :db/cardinality :db.cardinality/one
                     :db/doc         "The CPF of the client"}
                    {:db/ident       :client/email
                     :db/valueType   :db.type/string
                     :db/cardinality :db.cardinality/one
                     :db/doc         "The email of the client"}
                    {:db/ident       :client/card
                     :db/valueType   :db.type/ref
                     :db/cardinality :db.cardinality/one
                     :db/doc         "The client's card reference"}])

; Schema to represent a card on database.
(def card-schema [
                  {:db/ident       :card/number
                   :db/valueType   :db.type/long
                   :db/cardinality :db.cardinality/one
                   :db/doc         "The card number"}
                  {:db/ident       :card/cvv
                   :db/valueType   :db.type/long
                   :db/cardinality :db.cardinality/one
                   :db/doc         "The verification code number of the card"}
                  {:db/ident       :card/validity
                   :db/valueType   :db.type/instant
                   :db/cardinality :db.cardinality/one
                   :db/doc         "The card validity"}
                  {:db/ident       :card/limit
                   :db/valueType   :db.type/double
                   :db/cardinality :db.cardinality/one
                   :db/doc         "The limit value for that credit card"}])

(defn create-schemata "Saves the created schemata in database"
  [connection]
  (datomic/transact connection client-schema)
  (datomic/transact connection card-schema))