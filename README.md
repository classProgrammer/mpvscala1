# mpvscala1

## 1.1)
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
the sum of the maximums is = 20 (thread id=16)

Process finished with exit code 0


## 1.2)
Experimenting with the thread pools brought no speedup,
Changing the threshhold brought a small speedup.
Parallel is much faster than  sequential.
========== START OF otherTestProgram ==========
  ===== RUN of quicksort for 1000000 elements =====
      runtime: 0.6125874 sec
  ===== END OF RUN =====
  ===== START of testThreshhold =====
      parallel quicksort with th:36 was best for 1000000 elements
      with a runtime of 0.2163749 seconds
  ===== END of testThreshhold =====
  == Speedup seq/parallel = 2.8311389167597536
  ===== START of testThreadpools =====
      parallel quicksort with thread amount:38.0 was best for 1000000 elements
      with a runtime of 0.2164517 seconds
  == Speedup parallel/threadpool_optimized = 0.9996451864318923
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.2258383 seconds
  == Speedup parallel/threadpool_workstealing = 0.9580965673227261
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.2171477 seconds
  == Speedup parallel/threadpool_cached = 0.9964411320037008
      parallel quicksort with GLOBALS threadpool was best for 1000000 elements
      with a runtime of 0.2267563 seconds
  == Speedup parallel/threadpool_sheduled = 0.9542178100454101
  ===== END OF testThreadpools =====
========== END OF otherTestProgram ==========

## 1.3)
Max1 and Max2 deliver the same result

6
6

Process finished with exit code 0

## 1.4)
blocking?
Retry number 1
Retry number 2
Retry number 3
Retry number 4
Retry number 5
java.lang.Exception: Computation failed too often

Process finished with exit code 0

blocking?
Retry number 1
Retry number 2
Retry number 3
Retry number 4
Retry number 5
Retry number 6
Retry number 7
RESULT = 479

Process finished with exit code 0