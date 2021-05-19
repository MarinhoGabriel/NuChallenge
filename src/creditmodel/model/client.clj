(ns creditmodel.model.client)

(defn new-client "Creates an object of a client"
  [name cpf email]
  {:client/name  name
   :client/cpf   cpf
   :client/email email})