# Bin-Packing-Problem
Bin Packing Problem (BPP) is one of the well known problems in the Operation Research literature and
spans broad applications from manufacturing to cloud storage. There are different versions of BPP. In this version
there are list of n items, with weights w1; :::; wn and a set of bins S1; S2; ::: with the same capacity C, the aim
is to assign all items into minimum number of bins so that total weight of items in each bin does not exceed C. I
implemented the BPP solution in java.

Also I add another objective function to BPP. The second objective function is to
minimize the total deviation over capacity. To do this I relaxed the capacity constraint of the
classical BPP, and allow deviations over capacity. In other words, I allow to assign items to bins even if
the bin usage exceeds C.
