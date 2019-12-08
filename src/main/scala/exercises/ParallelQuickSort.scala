package exercises
import java.util.concurrent.Executors

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
// imports execution context for implicit passing
import scala.concurrent.ExecutionContext.Implicits.global

object ParallelQuickSort extends App{
  //implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

  def quickSort[T](seq: Seq[T])
                  (implicit ord: Ordering[T]): Seq[T] = {
    if (seq.length <= 1) seq
    else {
      val pivot = seq(seq.length / 2)
      Seq.concat(quickSort(seq filter (ord.lt(_, pivot))),
        seq filter (ord.equiv(_, pivot)),
        quickSort(seq filter (ord.gt(_, pivot))))
    }
  }

  def parallelQuickSort[T](seq: Seq[T], threshHold: Int)
                          (implicit ord: Ordering[T]): Seq[T] = {
    if (seq.length <= 1) seq
    else {
      val newLenght = seq.length / 2
      val pivot = seq(newLenght)
      if (newLenght <= threshHold) {
        Seq.concat(quickSort(seq filter (ord.lt(_, pivot))),
          seq filter (ord.equiv(_, pivot)),
          quickSort(seq filter (ord.gt(_, pivot))))
      }
      else {
        val fLeft = Future{
          quickSort(seq filter (ord.lt(_, pivot)))
        }
        val fRight = Future {
          quickSort(seq filter (ord.gt(_, pivot)))
        }
        val middle = seq filter (ord.equiv(_, pivot))

        val tupleResult = FuturesBasics.doInParallel(fLeft, fRight)

        var result: Seq[T] = null
        tupleResult.map(tuple => {
          result = Seq.concat(tuple._1, middle, tuple._2)
        })
        Await.ready(tupleResult, Duration.Inf)
        result
      }
    }
  }

  def parallelQuickSort[T](seq: Seq[T], threshHold: Int, ctx: ExecutionContextExecutor)
                          (implicit ord: Ordering[T]): Seq[T] = {
    if (seq.length <= 1) seq
    else {
      val newLenght = seq.length / 2
      val pivot = seq(newLenght)
      if (newLenght <= threshHold) {
        Seq.concat(quickSort(seq filter (ord.lt(_, pivot))),
          seq filter (ord.equiv(_, pivot)),
          quickSort(seq filter (ord.gt(_, pivot))))
      }
      else {
        val fLeft = Future{
          quickSort(seq filter (ord.lt(_, pivot)))
        }(ctx)
        val fRight = Future {
          quickSort(seq filter (ord.gt(_, pivot)))
        }(ctx)
        val middle = seq filter (ord.equiv(_, pivot))

        val tupleResult = FuturesBasics.doInParallel(fLeft, fRight)

        var result: Seq[T] = null
        tupleResult.map(tuple => {
          result = Seq.concat(tuple._1, middle, tuple._2)
        })(ctx)
        Await.ready(tupleResult, Duration.Inf)
        result
      }

    }
  }

  object Ascender extends Ordering[Int] {
    def compare(left:Int, right:Int) = left compare right
  }

  object Descender extends Ordering[Int] {
    def compare(left:Int, right:Int) = right compare left
  }

  object Weirdscender extends Ordering[Int] {
    def compare(left:Int, right:Int): Int = {
      val a = left % 2
      val b = right % 2

      if (a == 0 && b != 0) -1
      else if (a != 0 && b == 0) 1
      else left compare right
    }
  }

  def time[T](block: => T, name: String): Double = {
    println(s"  ===== RUN of $name =====")
    val start = System.nanoTime()
    val result = block    // call-by-name
    val stop = System.nanoTime()
    println("      runtime: " + (stop - start) / 1000000000.0 + " sec")
    println(s"  ===== END OF RUN =====")
    (stop - start) / 1000000000.0
  }

  def time[T](block: => T): Double = {
    val start = System.nanoTime()
    block    // call-by-name
    val stop = System.nanoTime()
    (stop - start) / 1000000000.0
  }

  // 1.1)
  def smallTestProgram(): Unit = {
    println("========== START OF smallTestProgram ==========")
    val arr = Seq(1,4,7,2,3,9,7,5,10,12,4)
    // implicit
    val res1 = quickSort(arr)
    // explicit 1
    val res2 = quickSort(arr)(Descender)

    val comp: Ordering[Int] = Weirdscender
    // explicit 2
    val res3 = quickSort(arr)(comp)

    println(s"   sequential.ascending: $res1")
    println(s"   sequential.: $res2")
    println(s"   sequential.even left, odd right sorted ascending: $res3")

    // implicit
    val res4 = parallelQuickSort(arr, 2)
    // explicit 1
    val res5 = parallelQuickSort(arr, 2)(Descender)

    // explicit 2
    val res6 = parallelQuickSort(arr, 2)(comp)

    println(s"   parallel.ascending: $res4")
    println(s"   parallel.descending: $res5")
    println(s"   parallel.even left, odd right sorted ascending: $res6")
    println("========== END OF smallTestProgram ==========")
  }

  def testThreshhold[T](testSet: Seq[T], thLower: Int, thUpper: Int, orderer: Ordering[T]): (Double, Int) = {
    println(s"  ===== START of testThreshhold =====")
    val elements = testSet.size

    var bestRuntime = Double.MaxValue
    var bestLabel = ""
    var bestThreshold = Int.MinValue

    (thLower to thUpper).foreach(th => {
      val runtime: Double = time(parallelQuickSort(testSet, th)(orderer))
      if (runtime < bestRuntime) {
        bestRuntime = runtime
        bestLabel = s"      parallel quicksort with th:$th was best for $elements elements\n" +
                    s"      with a runtime of $bestRuntime seconds"
        bestThreshold = th
      }
    })
    println(bestLabel)
    println(s"  ===== END of testThreshhold =====")
    (bestRuntime, bestThreshold)
  }

  def testThreadpools[T](testSet: Seq[T], bestThreshhold: Int, threadpoolLower: Int, threadpoolUpper: Int, orderer: Ordering[T]): Double = {
    //implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
    val elements = testSet.size

    var bestRuntime = time(parallelQuickSort(testSet, bestThreshhold)(orderer))
    var bestLabel = s"      parallel quicksort with GLOBALS threadpool was best for $elements elements\n" +
      s"      with a runtime of $bestRuntime seconds"
    var bestThreadAmount = Double.MinValue

    (threadpoolLower to threadpoolUpper).foreach(threadsInPool => {
      val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(threadsInPool))

      val runtime: Double = time(parallelQuickSort(testSet, bestThreshhold, executionContext)(orderer))
      if (runtime < bestRuntime) {
        bestRuntime = runtime
        bestThreadAmount = threadsInPool
        bestLabel = s"      parallel quicksort with thread amount:$bestThreadAmount was best for $elements elements\n" +
          s"      with a runtime of $bestRuntime seconds"
      }
    })
    println(bestLabel)
    bestRuntime
  }

  def testThreadpools[T](testSet: Seq[T], bestThreshhold: Int, context: ExecutionContextExecutor, orderer: Ordering[T]): Double = {
    //implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
    val elements = testSet.size

    var bestRuntime = time(parallelQuickSort(testSet, bestThreshhold, context)(orderer))
    var bestLabel = s"      parallel quicksort with GLOBALS threadpool was best for $elements elements\n" +
      s"      with a runtime of $bestRuntime seconds"

    println(bestLabel)
    bestRuntime
  }

  def otherTestProgram(): Unit = {
    println("========== START OF otherTestProgram ==========")
    val elements = 1000000
    val upper = 100
    val testSet: Seq[Int] = FuturesBasics.generateRandomSet(elements, upper)

    val seqQsTime = time(quickSort(testSet), s"quicksort for $elements elements")

    val parallelTuple: (Double, Int) = testThreshhold(testSet, 1, 40, Ascender)

    val speedup = seqQsTime / parallelTuple._1
    println(s"  == Speedup seq/parallel = $speedup")

    println(s"  ===== START of testThreadpools =====")
    val threadpoolsBestRuntime = testThreadpools(testSet, parallelTuple._2, 1, 40, Ascender)
    val speedup2 = parallelTuple._1 / threadpoolsBestRuntime
    println(s"  == Speedup parallel/threadpool_optimized = $speedup2")

    val threadpoolsBestRuntime2 = testThreadpools(testSet, parallelTuple._2, ExecutionContext.fromExecutor(Executors.newWorkStealingPool()), Ascender)
    val speedup3 = parallelTuple._1 / threadpoolsBestRuntime2
    println(s"  == Speedup parallel/threadpool_workstealing = $speedup3")

    val threadpoolsBestRuntime3 = testThreadpools(testSet, parallelTuple._2, ExecutionContext.fromExecutor(Executors.newCachedThreadPool()), Ascender)
    val speedup4 = parallelTuple._1 / threadpoolsBestRuntime3
    println(s"  == Speedup parallel/threadpool_cached = $speedup4")

    val threadpoolsBestRuntime4 = testThreadpools(testSet, parallelTuple._2, ExecutionContext.fromExecutor(Executors.newScheduledThreadPool(20)), Ascender)
    val speedup5 = parallelTuple._1 / threadpoolsBestRuntime4
    println(s"  == Speedup parallel/threadpool_sheduled = $speedup5")
    println("  ===== END OF testThreadpools =====")

    println("========== END OF otherTestProgram ==========")
  }
  smallTestProgram()
  otherTestProgram()
  println("================= End of Program =================")
}
