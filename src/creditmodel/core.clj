(ns creditmodel.core
  (:import (java.text SimpleDateFormat))
  (:require
    [creditmodel.controller :as controller]))

; Date format used to creating shopping registers.
(def date-format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss"))

(defn run "Runs the application doing all its functionalities"
  []
  ;; Starting the controller to create the schemata and open the connection
  (controller/start-controller)

  ;; Inserting clients in database
  (controller/save-client "Gabriel Marinho" "00000000000" "gabriel.marinho@gmail.com")
  (controller/save-client "Vitoria G." "11111111111" "vitoria.g@gmail.com")
  (controller/save-client "Yasmin S." "22222222222" "yasmin.s@gmail.com")

  ;; Adding cards to the clients
  (controller/add-card-to-client "00000000000")
  (controller/add-card-to-client "11111111111")
  (controller/add-card-to-client "22222222222")

  ;; Adding shopping
  (controller/save-shopping "McDonald's" "Food" 43.25 (.parse date-format "2021-03-19 21:05:33") "00000000000")
  (controller/save-shopping "Nike" "Clothing" 899.99 (.parse date-format "2021-02-17 04:50:00") "00000000000")
  (controller/save-shopping "Nike" "Clothing" 1299.90 (.parse date-format "2021-05-12 17:18:04") "00000000000")
  (controller/save-shopping "Nike" "Clothing" 749.99 (.parse date-format "2021-02-19 06:54:46") "00000000000")
  (controller/save-shopping "Spotify" "Streaming" 9.90 (.parse date-format "2021-01-07 10:04:03") "00000000000")
  (controller/save-shopping "KFC" "Food" 35.00 (.parse date-format "2021-03-05 17:22:54") "00000000000"))

; Running the application
(run)