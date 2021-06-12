(ns br.com.marinho.creditmodel.limit-checker.logic
  (:require [br.com.marinho.creditmodel.communication.producer :as producer]
            [br.com.marinho.creditmodel.communication.consumer :as consumer]
            [br.com.marinho.creditmodel.core.constants.kafka-topics :as topics]
            [clojure.data.json :as json]
            [br.com.marinho.creditmodel.core.controller :as controller]))

; Creating the producers that are going to send messages to the topics called
; "available_limit_or_balance" and "canceled_purchase", to inform that the purchase was
; confirmed in the case of not confirmed, respectively.
(def producer (producer/create-producer))

; Creating the consumer to consume the values coming from the Kafka server topic called
; "valid_purchase".
(def consumer (consumer/create-consumer "br.com.marinho.creditmodel.limitchecker"))

(defn- has-limit? "Function responsible for checking is the received message,
  representing a new purchase, has limit to conclude the operation. If it does, the
  purchase is made and, if doesn't, the purchase is canceled."
  [value]
  (when (nil? controller/uri)
    (controller/define-uri "datomic:dev://localhost:4334/creditmodel")
    (controller/start-connection))
  (let [purchase (json/read-str (.value value) :key-fn keyword)
        card (ffirst (controller/card-by-number (:card purchase)))]
    (>= (:card/limit card) (:value purchase))))

(defn- produce-message "Produces a message to send to the kafka client using the
  `producer`. The function has the three parameters needed to use the
  `producer/send-message!` function."
  [topic key value]
  (producer/send-message! producer topic key value))

(defn- send-successful-message! "If the return of the function `confirm?` returns
 `true`, this function is called to do the procedure when there is available limit."
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message topics/AVAILABLE_LIMIT
                     (str {:service "br.com.marinho.creditmodel.limitchecker"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn send-failure-message! "If the return of the function `confirm?` returns
  `false`, this function is called to do the procedure when there is no available limit."
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message topics/CANCELED_PURCHASE
                     (str {:service "br.com.marinho.creditmodel.limitchecker"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn start-consumption "Starts the consumer agent."
  []
  (println "Starting Limit Checker Service...")
  (consumer/consume consumer
                    [topics/PURCHASE_CONFIRMED]
                    (fn [value] (has-limit? value))
                    (fn [value] (send-successful-message! value))
                    (fn [value] (send-failure-message! value))))