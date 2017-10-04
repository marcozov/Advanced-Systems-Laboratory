# Advanced-Systems-Laboratory
This project is based on the Advanced Systems Laboratory course of ETH Zurich. The goal is the implementation of a middleware application that acts between clients and servers that run the memcached procol.

In particular, clients are supposed to perform usual retrieval and storage operations, which will be intercepted by the middleware application. The role of the application will be to perform data replication (for storage operations) and load balancing (for retrieval operations) on different servers.
