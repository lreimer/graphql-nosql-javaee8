# GraphQL  Java EE

Sometimes relational object mapping using JPA is not a good fit for your data model
and you may want to use a NoSQL data store. While there is no dedicated API, you
can easily retrofit the functionality by using CDI, JSON-P or JSON-B in combination
with the native driver like the Mongo DB Java client.
