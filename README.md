# mpvscala1

## 1.1) Future Basics
```text
======== MAIN ======== (thread id=1)
non blocking? (thread id=1)
2: 1 (thread id=15)
2: 2 (thread id=15)
2: 3 (thread id=15)
2: 4 (thread id=15)
2: 5 (thread id=15)
1: 1 (thread id=15)
1: 2 (thread id=15)
1: 3 (thread id=15)
1: 4 (thread id=15)
1: 5 (thread id=15)
() (thread id=14)
compute 1: 1 (thread id=14)
compute 2: 1 (thread id=15)
blocking? (thread id=1)
compute 1: 2 (thread id=14)
compute 2: 2 (thread id=15)
compute 1: 3 (thread id=14)
compute 2: 3 (thread id=15)
compute 1: 4 (thread id=14)
compute 2: 4 (thread id=15)
compute 1: 5 (thread id=14)
compute 2: 5 (thread id=15)
version 2: sum = 100 (thread id=16)
blocking? (thread id=1)
sets = (Vector(7, 4, 7, 10, 2, 9),Vector(3, 7, 3, 5, 7, 10)) (thread id=16)
the sum of the maxima is = 20 (thread id=16)

Process finished with exit code 0
```


## 1.2) (Parallel) Quicksort
Experimenting with the thread pools brought no speedup,
Changing the threshhold brought a small speedup.
Parallel is much faster than  sequential.
```text
========== START OF smallTestProgram ==========
   sequential.ascending: List(1, 2, 3, 4, 4, 5, 7, 7, 9, 10, 12)
   sequential.: List(12, 10, 9, 7, 7, 5, 4, 4, 3, 2, 1)
   sequential.even left, odd right sorted ascending: List(2, 4, 4, 10, 12, 1, 3, 5, 7, 7, 9)
   parallel.ascending: List(1, 2, 3, 4, 4, 5, 7, 7, 9, 10, 12)
   parallel.descending: List(12, 10, 9, 7, 7, 5, 4, 4, 3, 2, 1)
   parallel.even left, odd right sorted ascending: List(2, 4, 4, 10, 12, 1, 3, 5, 7, 7, 9)
========== END OF smallTestProgram ==========
========== START OF otherTestProgram ==========
  ===== RUN of quicksort for 1000000 elements =====
      runtime: 0.4349915 sec
  ===== END OF RUN =====
  ===== START of testThreshhold =====
      parallel quicksort with th:33 was best for 1000000 elements
      with a runtime of 0.2316907 seconds
  ===== END of testThreshhold =====
  == Speedup seq/parallel = 1.8774663808258163
  ===== START of testThreadpools =====
      parallel quicksort with thread amount:14.0 was best for 1000000 elements
      with a runtime of 0.2320374 seconds
  == Speedup parallel/threadpool_optimized = 0.9985058443164766
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.312038 seconds
  == Speedup parallel/threadpool_workstealing = 0.742507963773643
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.252039 seconds
  == Speedup parallel/threadpool_cached = 0.9192652724379956
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.2559664 seconds
  == Speedup parallel/threadpool_sheduled = 0.9051605992036456
  ===== END OF testThreadpools =====
========== END OF otherTestProgram ==========
================= End of Program =================

Process finished with exit code 0
```

## 1.3) Max: Advanced Future Concepts Part 1
Max1 and Max2 deliver the same result
```text
===== Test Invalid Partition Size =====
   Exception Max1 = java.lang.IllegalStateException: Invalid Partition Size
   Exception Max2 = java.lang.IllegalStateException: Invalid Partition Size
===== END Test Invalid Partition Size =====

===== Test Empty List =====
   Exception Max1 = java.lang.IllegalArgumentException: List is empty
   Exception Max2 = java.lang.IllegalArgumentException: List is empty
===== END Test Empty List =====

===== Negative Partition Size =====
   Exception Max1 = java.lang.IllegalStateException: Invalid Partition Size
   Exception Max2 = java.lang.IllegalStateException: Invalid Partition Size
===== END Negative Partition Size =====

===== Test Null =====
   Exception Max1 = java.lang.NullPointerException
   Exception Max2 = java.lang.NullPointerException
===== END Test Null =====

===== Test Success =====
   result of Max1 = 6
   result of Max2 = 6
===== END Test Success =====

===== Valid List with 100000 Elements =====
   result of Max1 = 1000
   result of Max2 = 1000
===== END Valid List with 100000 Elements =====

Process finished with exit code 0
```

## 1.4) Retry: Advanced Future Concepts Part 2
```text
======== START test with 30 retries and Success Ratio of 1/10 ========
   Retry number 1
   Retry number 2
   Retry number 3
   Retry number 4
   Retry number 5
   Retry number 6
   Retry number 7
   Retry number 8
   Retry number 9
   RESULT = 404
======== END test ========

======== START test with 5 retries and Success Ratio of 1/100 ========
   Retry number 1
   Retry number 2
   Retry number 3
   Retry number 4
   Retry number 5
   java.lang.Exception: Computation failed too often
======== END test ========

======== START test with 100 retries and Success Ratio of 1 ========
   RESULT = 764
======== END test ========
```