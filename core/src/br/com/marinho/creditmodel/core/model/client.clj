(ns br.com.marinho.creditmodel.core.model.client
  (:import (java.util UUID)))

(defn generate-uuid "Generates a new unique UUID used in the client registers."
  []
  (UUID/randomUUID))

(defn new-client "Creates an object of a client. Both functions create a new client, but the second one
  receives an uuid fro outside."
  ([name cpf email]
   (new-client (generate-uuid) name cpf email))

  ([uuid name cpf email]
   {:client/id    uuid
    :client/name  name
    :client/cpf   cpf
    :client/email email}))