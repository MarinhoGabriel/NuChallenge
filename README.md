# creditmodel.clj

A Clojure package used to simulate e credit card system model with clients cards.
The package counts with Datomic database to store the data and Cloure as
the main language.<br>
The package is able to create clients, add cards to the created clients and check
the purchases made by the clients. Also, the package can block a card from a client
depending on some situation.

## Usage

```clojure
;; in collection under :dependencies key
[br.com.marinho/creditmodel "0.1.0"]
```

## References
https://cursos.alura.com.br/course/clojure-introducao-a-programacao-funcional <br>
https://cursos.alura.com.br/course/clojure-colecoes-no-dia-a-dia <br>
https://cursos.alura.com.br/course/introducao-ao-datomic <br>
https://cursos.alura.com.br/course/datomic-identidade-e-queries