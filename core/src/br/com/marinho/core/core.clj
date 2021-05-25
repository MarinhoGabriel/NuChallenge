(ns br.com.marinho.core.core
  (:use clojure.pprint)
  (:import (java.text SimpleDateFormat))
  (:require
    [br.com.marinho.core.controller :as controller]))

; Date format used to creating purchase registers.
(def date-format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss"))

(defn run "Runs the application doing all its functionalities"
  []
  ;; Starting the controller to create the schemata and open the connection
  (controller/start-controller)

  ;; Inserting clients in database
  (controller/save-client! "Gabriel Marinho" "00000000000" "gabriel.marinho@gmail.com")
  (controller/save-client! "Vitoria G." "11111111111" "vitoria.g@gmail.com")
  (controller/save-client! "Yasmin S." "22222222222" "yasmin.s@gmail.com")

  ;; Printing the saved clients
  (println "\nClients without card")
  (pprint (controller/get-clients))

  ;; Adding cards to the clients
  (controller/add-card-to-client! "00000000000")
  (controller/add-card-to-client! "11111111111")
  (controller/add-card-to-client! "22222222222")

  ;; Printing the saved clients after adding their cards
  (println "\nClients with the added cards")
  (pprint (controller/get-clients))

  ;; Adding purchases
  (controller/save-purchase! "McDonald's" "Food" 43.25 (.parse date-format "2021-03-19 21:05:33") "00000000000")
  (controller/save-purchase! "Nike" "Clothing" 899.99 (.parse date-format "2021-02-17 04:50:00") "00000000000")
  (controller/save-purchase! "Nike" "Clothing" 1299.90 (.parse date-format "2021-05-12 17:18:04") "00000000000")
  (controller/save-purchase! "Nike" "Clothing" 749.99 (.parse date-format "2021-02-19 06:54:46") "00000000000")
  (controller/save-purchase! "Spotify" "Streaming" 9.90 (.parse date-format "2021-01-07 10:04:03") "00000000000")
  (controller/save-purchase! "KFC" "Food" 35.00 (.parse date-format "2021-03-05 17:22:54") "00000000000")

  (controller/save-purchase! "Swedish Fish & Chips" "Food" 62.94 (.parse date-format "2021-01-10 10:47:09") "11111111111")
  (controller/save-purchase! "Adidas" "Clothing" 164.40 (.parse date-format "2021-01-04 00:30:14") "11111111111")
  (controller/save-purchase! "Au Vieux Grec" "Food" 226.60 (.parse date-format "2021-04-21 14:32:57") "11111111111")
  (controller/save-purchase! "Adidas" "Clothing" 233.12 (.parse date-format "2021-01-05 02:56:21") "11111111111")
  (controller/save-purchase! "Puma" "Clothing" 100.55 (.parse date-format "2021-04-12 03:48:20") "11111111111")
  (controller/save-purchase! "Zoes Juice" "Food" 149.23 (.parse date-format "2021-03-27 08:30:40") "11111111111")
  (controller/save-purchase! "Puma" "Clothing" 241.04 (.parse date-format "2021-03-13 10:08:17") "11111111111")
  (controller/save-purchase! "Doodle, Mr. Doughnut" "Food" 149.51 (.parse date-format "2021-05-15 11:53:43") "11111111111")

  (controller/save-purchase! "iPlace" "Electronics" 49.84 (.parse date-format "2021-04-13 13:46:28") "22222222222")
  (controller/save-purchase! "Adidas" "Clothing" 42.24 (.parse date-format "2021-03-03 10:11:14") "22222222222")
  (controller/save-purchase! "Spotify" "Streaming" 116.09 (.parse date-format "2021-03-17 16:29:08") "22222222222")
  (controller/save-purchase! "Hering" "Clothing" 89.27 (.parse date-format "2021-01-12 07:44:28") "22222222222")
  (controller/save-purchase! "Abebe Skin" "Health care" 110.69 (.parse date-format "2021-03-31 09:11:53") "22222222222")
  (controller/save-purchase! "Netflix" "Streaming" 212.46 (.parse date-format "2021-04-30 02:19:17") "22222222222")
  (controller/save-purchase! "Parme" "Food" 9999.9 (.parse date-format "2021-02-01 17:07:38") "22222222222")

  (println "\nAll purchase")
  (pprint (controller/get-purchase))

  (println "\nPurchases by client 'Gabriel Marinho'")
  (pprint (controller/purchases-by-client? "00000000000"))

  (println "\nPurchases by client 'Vitoria G.'")
  (pprint (controller/purchases-by-client? "11111111111"))

  (println "\nPurchases by client 'Yasmin S.'")
  (pprint (controller/purchases-by-client? "22222222222"))

  (controller/most-valued-purchase?)
  (controller/max-purchase-count-by-client?)
  (println (controller/client-with-no-purchase?)))

; Running the application
(run)