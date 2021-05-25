(defproject core "0.1.0"
  :description "The core module of the credit-model project, with all common files used by the modules."
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.datomic/datomic-pro "1.0.6269"]]
  :repl-options {:init-ns br.com.marinho.core.core})