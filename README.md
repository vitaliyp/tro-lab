tro-lab
=======

Lab works for Distributed Computing course

## Lab1
The goal here was to make some arithmetical computing on a number of remote machines using java Sokets.
I tried to write this application in such way that computing clients would not need updating when task changes. This was achieved by sending .class file across the network and loading it with a custom ClassLoader on the client side. No measures was taken to ensure security.
