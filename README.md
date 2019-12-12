# mpvscala1

## 1.1) Future Basics
```text
======== MAIN ======== (thread id=1)
======== MAIN ======== (thread id=1)
   === Version 1 Test 1 (thread id=1)
            2: 1 (thread id=14)
            1: 1 (thread id=15)
            2: 2 (thread id=14)
            1: 2 (thread id=15)
            2: 3 (thread id=14)
            1: 3 (thread id=15)
            1: 4 (thread id=15)
            2: 4 (thread id=14)
            1: 5 (thread id=15)
            2: 5 (thread id=14)
         v_1: () (thread id=15)
   === Version 1 Test 2 (thread id=1)
            2: 1 (thread id=15)
            1: 1 (thread id=14)
            1: 2 (thread id=14)
            2: 2 (thread id=15)
            1: 3 (thread id=14)
            2: 3 (thread id=15)
            1: 4 (thread id=14)
            2: 4 (thread id=15)
            2: 5 (thread id=15)
            1: 5 (thread id=14)
            1: 6 (thread id=14)
   === Version 2 (thread id=1)
         FAILED v_1_2: java.lang.IllegalArgumentException (thread id=14)
            compute 1: 1 (thread id=14)
            compute 2: 1 (thread id=15)
            compute 1: 2 (thread id=14)
            compute 2: 2 (thread id=15)
            compute 1: 3 (thread id=14)
            compute 2: 3 (thread id=15)
            compute 1: 4 (thread id=14)
            compute 2: 4 (thread id=15)
            compute 1: 5 (thread id=14)
            compute 2: 5 (thread id=15)
   === Version 3 (thread id=1)
            compute 1: 1 (thread id=15)
            combine 1 (thread id=16)
            compute 2: 1 (thread id=14)
            compute 1: 2 (thread id=15)
            compute 2: 2 (thread id=14)
            combine 2 (thread id=16)
            compute 1: 3 (thread id=15)
            compute 2: 3 (thread id=14)
            combine 3 (thread id=16)
            compute 1: 4 (thread id=15)
            compute 2: 4 (thread id=14)
            combine 4 (thread id=16)
            compute 1: 5 (thread id=15)
            combine 5 (thread id=16)
            compute 2: 5 (thread id=14)
         version 2: sum = 100 (thread id=16)
            combine 1 (thread id=16)
subsets: (Vector(5, 1, 6, 1, 5),Vector(1, 8, 9, 6, 8, 10)) (thread id=15)
            combine 1 (thread id=15)
            combine 2 (thread id=16)
            combine 2 (thread id=15)
            combine 3 (thread id=16)
            combine 3 (thread id=15)
            combine 4 (thread id=16)
            combine 4 (thread id=15)
            combine 5 (thread id=16)
            combine 5 (thread id=15)
         version 3: sum = 100 (thread id=16)
         v_4 sum is = 16 (thread id=15)

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
   res4: parallel.ascending: List(1, 2, 3, 4, 4, 5, 7, 7, 9, 10, 12)
   res5: parallel.ascending: List(12, 10, 9, 7, 7, 5, 4, 4, 3, 2, 1)
   res6: parallel.ascending: List(2, 4, 4, 10, 12, 1, 3, 5, 7, 7, 9)
========== END OF smallTestProgram ==========
========== START OF otherTestProgram ==========
1000000 (thread id=1)
100 (thread id=1)
  ===== RUN of quicksort for 1000000 elements =====
      runtime: 0.7011969 sec
  ===== END OF RUN =====
  ===== START of testThreshhold =====
      parallel quicksort with th:32 was best for 1000000 elements
      with a runtime of 0.2507634 seconds
  ===== END of testThreshhold =====
  == Speedup seq/parallel = 2.7962489741325887
  ===== START of testThreadpools =====
      parallel quicksort with thread amount:40.0 was best for 1000000 elements
      with a runtime of 0.2513541 seconds
  == Speedup parallel/threadpool_optimized = 0.9976499289249708
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.2689144 seconds
  == Speedup parallel/threadpool_workstealing = 0.9325026848692373
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.2602031 seconds
  == Speedup parallel/threadpool_cached = 0.9637218003936157
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.255897 seconds
  == Speedup parallel/threadpool_sheduled = 0.979938803502972
  ===== END OF testThreadpools =====
========== END OF otherTestProgram ==========
================= End of Program =================

Process finished with exit code 0
```

## 1.3) Max: Advanced Future Concepts Part 1
Max1 and Max2 deliver the same result
```text
===== Test Null =====
   Exception Max1 = java.lang.NullPointerException
   Exception Max2 = java.lang.NullPointerException
===== END Test Null =====

===== Negative Partition Size =====
   Exception Max1 = java.lang.IllegalStateException: Invalid Partition Size
   Exception Max2 = java.lang.IllegalStateException: Invalid Partition Size
===== END Negative Partition Size =====

===== Test Empty List =====
   Exception Max1 = java.lang.IllegalArgumentException: List is empty
   Exception Max2 = java.lang.IllegalArgumentException: List is empty
===== END Test Empty List =====

===== Test Invalid Partition Size =====
   Exception Max1 = java.lang.IllegalStateException: Invalid Partition Size
   Exception Max2 = java.lang.IllegalStateException: Invalid Partition Size
===== END Test Invalid Partition Size =====

===== Test Success =====
   result of Max1 = 6
   result of Max2 = 6
===== END Test Success =====

===== Valid List with 30.000.000 Elements =====
   result of Max1 = 1000
   result of Max2 = 1000
===== END Valid List with 30.000.000 Elements =====

Process finished with exit code 0
```

## 1.4) Retry: Advanced Future Concepts Part 2
```text
======== START test with 30 retries and Success Ratio of 1/10 ========
   Retries left 30
   Retries left 29
   Retries left 28
   Retries left 27
   Retries left 26
   Retries left 25
   Retries left 24
   Retries left 23
   Retries left 22
   Retries left 21
   Retries left 20
   Retries left 19
   Retries left 18
   Retries left 17
   Retries left 16
   Retries left 15
   Retries left 14
   Retries left 13
   Retries left 12
   Retries left 11
   Retries left 10
   RESULT = 382
======== END test ========

======== START test with 5 retries and Success Ratio of 1/100 ========
   Retries left 5
   Retries left 4
   Retries left 3
   Retries left 2
   Retries left 1
   java.lang.Exception: Computation failed too often
======== END test ========

======== START test with 100 retries and Success Ratio of 1 ========
   RESULT = 10
======== END test ========

Process finished with exit code 0
```