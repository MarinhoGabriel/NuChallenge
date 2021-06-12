(ns br.com.marinho.creditmodel.core.constants.kafka-topics)

;; The following file contains the Kafka topic used by consumers and producers to
;; communicate with each other throughout the system.
;;
;; The relation between the services and the topics is given by:
;; - new_purchase
;;     - listened by: none
;;     - written by: terminal
;; - valid_purchase
;;     - listened by: app-confirming
;;     - written by: purchase-validator
;; - canceled_purchase
;;     - listened by: logger
;;     - written by: all services
;; - user_confirmation
;;     - listened by: inside app-confirming
;;     - written by: terminal
;; - purchase_confirmed
;;     - listened by: limit-checker
;;     - written by: app-confirming
;; - available_limit
;;     - listened by: purchaser
;;     - written by: limit-checker

; Topic consumed by the purchase-validator service to identify a new purchase to start
; the operation.
(def NEW_PURCHASE "new_purchase")

; Topic consumed by the app-confirming service to check whether the purchase is a valid
; one or not.
(def VALID_PURCHASE "valid_purchase")

; The following topic receives messages from every service when there is a problem in
; the purchase operation. In other words, every service writes in this topic.
(def CANCELED_PURCHASE "canceled_purchase")

; Topic listened by the function responsible for checking if the purchase was confirmed
; inside app-confirming service.
(def USER_CONFIRMATION "user_confirmation")

; Topic consumed by the limit-checker service, indicating that the purchase was
; confirmed to continue the flow and check if there is an available limit to conclude
; the operation.
(def PURCHASE_CONFIRMED "purchase_confirmed")

; Topic consumed by the purchaser service, indicating that there is an available limit
; to conclude the purchase.
(def AVAILABLE_LIMIT "available_limit")

;; Topic produced by the purchaser service to indicate that the purchase was concluded.
(def PURCHASE_CONCLUDED "purchase_concluded")