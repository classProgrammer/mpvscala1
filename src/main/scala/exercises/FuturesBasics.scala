package exercises


import basics.Futures.combine

import scala.collection.View.Collect
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
// imports execution context for implicit passing
import scala.concurrent.ExecutionContext.Implicits.global

object FuturesBasics extends App {
  def doWork(id: Int, steps: Int): Unit = {
    for (i <- 1 to steps) {
      println(s"$id: $i")
      if (i == 6) throw new IllegalArgumentException()
      Thread.sleep(200)
    }
  }

  def compute(id: Int, n: Int, result: Int): Int = {
    for (i <- 1 to n) {
      println(s"compute $id: $i")
      if (i == 6) throw new IllegalArgumentException()
      Thread.sleep(200)
    }
    result
  }

  def combine(value1: Int, value2: Int): Int = {
    for (i <- 1 to 5) {
      println(s"combine $i")
      Thread.sleep(200)
    }
    value1 + value2
  }

  def println(x: Any) = Console.println(s"$x (thread id=${Thread.currentThread.getId})")

  // 1.1 a)
  def doInParallel[U, V](block1: => U, block2: => V): Future[Unit] = {
    val f: Future[Unit] = Future {
      val f1: Future[U] = Future { block1 }
      val f2: Future[V] = Future { block2 }

      Await.ready(f1, Duration.Inf);
      Await.ready(f2, Duration.Inf);
    }
    f
  }

  // 1.1 b) for expressions
  def doInParallel[U, V](future1: Future[U], future2: Future[V]): Future[(U, V)] = {
      for (r1 <- future1;
           r2 <- future2) yield (r1, r2)
  }
  // 1.1 b) flatMap and map
  def doInParallelWithFlatMap[U, V](future1: Future[U], future2: Future[V]): Future[(U, V)] = {
    val f: Future[(U, V)] = future1.flatMap(r1 => future2.map(r2 => (r1, r2)))
    f
  }

  // 1.1 c)
  def generateRandomSet(start: Int, end: Int, upperBound: Int): Seq[Int] = {
    val rand = scala.util.Random
    (start to end).map(x => rand.nextInt(upperBound));
  }

  def getMax(collection: Seq[Int]): Int = {
    var max = Int.MinValue
    for (elem <- collection) {
      if (elem > max) max = elem
    }
    max
  }

  def maxWithDoInParallel(sizeOfSet: Int, upperBound: Int): Future[Int] = {
    // 1) Generate Random Set
    val f1: Future[Seq[Int]] = Future{
      generateRandomSet(0, sizeOfSet/2, upperBound)
    }
    val f2: Future[Seq[Int]] = Future{
      generateRandomSet(sizeOfSet/2, sizeOfSet, upperBound)
    }
    // 2) "Split" in 2 Parts
    val sets: Future[(Seq[Int], Seq[Int])] = doInParallel(f1, f2)

    val s: Future[(Int, Int)] = sets.flatMap(r => {
      println(s"sets = $r")
      val joined = r._1 ++ r._2 // join them just to slit them again is pretty useless but it matches the given description
      val subsets: (Seq[Int], Seq[Int]) = joined.splitAt(sizeOfSet / 2)

      // 3) Max of Each
      val f1: Future[Int] = Future { getMax(subsets._1) }
      val f2: Future[Int] = Future { getMax(subsets._2) }

      val f: Future[(Int, Int)] = doInParallel(f1, f2)
      f
    })

    // 4) Add Result
    val sum: Future[Int] = s.map(r => r._1 + r._2)
    sum
  }

  println("======== MAIN ========")
  val version_1 = doInParallel(doWork(1, 5), doWork(2, 5))
  println("non blocking?")
  Await.ready(version_1, Duration.Inf);
  version_1 foreach println // Unit
  version_1.failed foreach println // should be empty

  val f1 = Future{ compute(1, 5, 40) }
  val f2 = Future{ compute(2, 5, 60) }
  val version_2 = doInParallel(f1, f2)

  version_2        foreach(tuple => {
    val sum = tuple._1 + tuple._2
    println(s"version 2: sum = $sum")
  })
  version_2.failed foreach(ex => println(s"f2: exception : $ex"))
  println("blocking?")
  Await.ready(version_2, Duration.Inf)

  val example1 = maxWithDoInParallel(10, 11)
  println("blocking?")
  Await.ready(example1, Duration.Inf)
  example1 foreach(res => println(s"the sum of the maximums is = $res"))
}
