(ns creditmodel.core
  (:require
    [creditmodel.client-db :as c.cdb]
    [creditmodel.shopping-db :as c.sdb]
    [creditmodel.card-db :as c.cadb]))

; Creating and printing the clients
(c.cdb/add-client "Gabriel" "gabriel@gmail.com")
(c.cdb/add-client "Alves" "alvesl@gmail.com")
(c.cdb/add-client "Marinho" "marinho@gmail.com")
(c.cdb/get-clients)
(c.cdb/add-card "01")
(c.cdb/add-card "02")

(println c.cadb/cards)

(c.sdb/add-shopping (get (c.cdb/get-card-by-client-cpf "01") :number) "10/02" 129 "Mc" "food")
(c.sdb/add-shopping (get (c.cdb/get-card-by-client-cpf "02") :number) "10/02" 129 "Bk" "food")
(c.sdb/add-shopping (get (c.cdb/get-card-by-client-cpf "02") :number) "11/11" 129 "Hering" "clothing")
(c.sdb/add-shopping (get (c.cdb/get-card-by-client-cpf "02") :number) "9/09" 222 "Nike" "clothing")
(c.sdb/add-shopping (get (c.cdb/get-card-by-client-cpf "00") :number) "4/05" 30 "Adidas" "clothing")
(c.sdb/add-shopping (get (c.cdb/get-card-by-client-cpf "00") :number) "1/02" 129 "Mc" "food")
(println (c.sdb/shopping-list "01"))

(println (c.sdb/shopping-by-category "food"))