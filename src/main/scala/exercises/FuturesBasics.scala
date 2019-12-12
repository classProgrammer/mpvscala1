package exercises

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
// imports execution context for implicit passing
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesBasics extends App {
  def doWork(id: Int, steps: Int): Unit = {
    for (i <- 1 to steps) {
      println(s"            $id: $i")
      if (i == 6) throw new IllegalArgumentException()
      Thread.sleep(200)
    }
  }

  def compute(id: Int, n: Int, result: Int): Int = {
    for (i <- 1 to n) {
      println(s"            compute $id: $i")
      if (i == 6) throw new IllegalArgumentException()
      Thread.sleep(200)
    }
    result
  }

  def combine(value1: Int, value2: Int): Int = {
    for (i <- 1 to 5) {
      println(s"            combine $i")
      Thread.sleep(200)
    }
    value1 + value2
  }

  def println(x: Any) = Console.println(s"$x (thread id=${Thread.currentThread.getId})")

  // 1.1 a)
  def doInParallel[U, V](block1: => U, block2: => V): Future[Unit] = {
    val f1: Future[U] = Future { block1 }
    val f2: Future[V] = Future { block2 }

    for (_ <- f1;
         _ <- f2) yield ()
  }

  // 1.1 b) for expressions
  def doInParallel[U, V](future1: Future[U], future2: Future[V]): Future[(U, V)] = {
      for (r1 <- future1;
           r2 <- future2) yield (r1, r2)
  }
  // 1.1 b) flatMap and map
  def doInParallelWithFlatMap[U, V](future1: Future[U], future2: Future[V]): Future[(U, V)] = {
    future1.flatMap(r1 => future2.map(r2 => (r1, r2)))
  }

  // 1.1 c)
  def generateRandomSet(start: Int, end: Int, upperBound: Int): Seq[Int] = {
    val generator = scala.util.Random
    (start to end).map(_ => generator.nextInt(upperBound));
  }

  def generateRandomSet(noOfElements: Int, upperBound: Int): Seq[Int] = {
    val generator = scala.util.Random
    println(noOfElements)
    println(upperBound)
    (1 to noOfElements).map(x => generator.nextInt(upperBound));
  }

  def maxWithDoInParallel(sizeOfSet: Int, upperBound: Int): Future[Int] = {
    // 1) Generate Random Set
   Future(generateRandomSet(0, sizeOfSet, upperBound))
     .flatMap(set => {
      val subsets: (Seq[Int], Seq[Int]) = set.splitAt(sizeOfSet / 2)

      println(s"subsets: $subsets") // only to check correctness
      // 3) Max of Each Half
      val left: Future[Int] = Future { subsets._1.max }
      val right: Future[Int] = Future { subsets._2.max }

      doInParallel(left, right) // 4) Combine
        .map(tuple => combine(tuple._1, tuple._2))
    })
  }

  println("======== MAIN ========")
  val version_1 = doInParallel(doWork(1, 5), doWork(2, 5))
  println("   === Version 1 Test 1")
  Await.ready(version_1, Duration.Inf);
  version_1 foreach(v => println(s"         v_1: $v"))// Unit
  version_1.failed foreach(v => println(s"         FAILED v_1: $v"))// should be empty

  println("   === Version 1 Test 2")
  val version_1_2 = doInParallel(doWork(1, 20), doWork(2, 5))
  Await.ready(version_1_2, Duration.Inf);
  version_1_2 foreach(v => println(s"         v_1_2: $v")) // should be empty
  version_1_2.failed foreach(v => println(s"         FAILED v_1_2: $v"))// should print error

  println("   === Version 2")
  val f1 = Future{ compute(1, 5, 40) }
  val f2 = Future{ compute(2, 5, 60) }
  val version_2 = doInParallel(f1, f2)

  version_2        foreach(tuple => {
    val max = combine(tuple._1, tuple._2)
    println(s"         version 2: sum = $max")
  })
  version_2.failed foreach(ex => println(s"         f2: exception : $ex"))

  Await.ready(version_2, Duration.Inf)

  println("   === Version 3")
  val f3 = Future{ compute(1, 5, 60) }
  val f4 = Future{ compute(2, 5, 40) }
  val version_3 = doInParallelWithFlatMap(f3, f4)

  version_3        foreach(tuple => {
    val max = combine(tuple._1, tuple._2)
    println(s"         version 3: sum = $max")
  })
  version_3.failed foreach(ex => println(s"         f2: exception : $ex"))
  Await.ready(version_3, Duration.Inf)

  val version_4 = maxWithDoInParallel(10, 11)
  version_4 foreach(res => println(s"         v_4 sum is = $res"))
  version_4.failed foreach(ex => println(s"         v_4 exception : $ex"))
  Await.ready(version_4, Duration.Inf)
  Thread.sleep(50)
}
