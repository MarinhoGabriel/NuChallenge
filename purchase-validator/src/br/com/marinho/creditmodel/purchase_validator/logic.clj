(ns br.com.marinho.creditmodel.purchase-validator.logic
  (:require [br.com.marinho.creditmodel.communication.producer :as producer]
            [br.com.marinho.creditmodel.communication.consumer :as consumer]
            [br.com.marinho.creditmodel.core.constants.kafka-topics :as topics])
  (:require [clojure.data.json :as json]))

; Creating the producers that are going to send messages to the topics called
; "real_purchase" and "canceled_purchase", to inform that the purchase is a valid one
; and in the case of an invalid purchase, respectively.
(def producer (producer/create-producer))

; Creating the consumer to consume the values coming from the Kafka server topic called
; "new_purchase".
(def consumer (consumer/create-consumer "br.com.marinho.creditmodel.purchasevalidator"))

(defn valid-purchase? "Function responsible for validating whether the purchase is a
  valid one. A purchase is considered valid iff its value is lesser than 4k7 and if
  there is a merchant name."
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (and
      (< (:value purchase) 4700)
      (not (nil? (:merchant purchase))))))

(defn- produce-message "Produces a message to send to the kafka client using the
  `producer`. The function has the three parameters needed to use the
  `producer/send-message!` function."
  [topic key value]
  (producer/send-message! producer topic key value))

(defn- send-successful-message! "If the return of the function `valid-purchase?`
  returns `true`, this function is called to do the procedure when it's a valid
  operation."
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message topics/VALID_PURCHASE
                     (str {:service "br.com.marinho.creditmodel.purchasevalidator"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn send-failure-message! "If the return of the function `valid-purchase?` returns
  `false`, this function is called to do the procedure when it's a invalid operation."
  [value]
  (let [purchase (json/read-str (.value value) :key-fn keyword)]
    (produce-message topics/CANCELED_PURCHASE
                     (str {:service "br.com.marinho.creditmodel.purchasevalidator"
                           :id      (:id purchase)})
                     (str (.value value)))))

(defn start-consumption "Starts the consumer agent."
  []
  (println "Starting Purchase Validator Service...")
  (consumer/consume consumer
                    [topics/NEW_PURCHASE]
                    (fn [value] (valid-purchase? value))
                    (fn [value] (send-successful-message! value))
                    (fn [value] (send-failure-message! value))))