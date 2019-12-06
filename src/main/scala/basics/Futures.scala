package basics

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
// imports execution context for implicit passing
import scala.concurrent.ExecutionContext.Implicits.global
object Futures extends App {

  def println(x: Any) = Console.println(s"$x (thread id=${Thread.currentThread.getId})")

  def doWork(id: Int, steps: Int): Unit = {
    for (i <- 1 to steps) {
      println(s"$id: $i")
      if (i == 6) throw new IllegalArgumentException()
      Thread.sleep(200)
    }
  }

  def compute(id : Int, n: Int, result : Int) : Int = {
    for (i <- 1 to n) {
      println(s"compute $id: $i")
      if (i == 6) throw new IllegalArgumentException()
      Thread.sleep(200)
    }
    result
  }

  def combine(value1 : Int, value2 : Int) : Int = {
    for (i <- 1 to 5) {
      println(s"combine $i")
      Thread.sleep(200)
    }
    value1 + value2
  }

  def sequentialInvocation(): Unit =  {
    doWork(1, 5)
    doWork(2, 5)
  }

  def simpleFutures(): Unit = {
    val f1 = Future{
      doWork(1, 5)
    }
    println("f1 registered")
    val f2 = Future{
      doWork(2, 5)
    }
    println("f2 registered")

    Thread.sleep(1500)
  }

  def futuresWithCallback(): Unit = {
    val f1: Future[Int] = Future{
      compute(1, 5, 10)
    }

    println("f1 registered")
    f1 foreach(r => println(s"f1: result = $r"))
    println("f1 callback registered")

    val f2: Future[Int] = Future{
      compute(2, 7, 10)
    }

    println("f2 registered")
    f2        foreach(r => println(s"f2: result = $r"))
    f2.failed foreach(ex => println(s"f2: exception : $ex"))
    println("f2 callback registered")

    Await.ready(f1, Duration.Inf); // only in main program to wait for future completion
    Await.ready(f2, Duration.Inf);
    Thread.sleep(50) // wait for print to finish
  }

  // map => value not a future itself
  // flatMap => value itself is a future

  def futureComposition(): Unit = {
    val f1: Future[Int] = Future{
      compute(1, 5, 60)
    }
    val f2: Future[Int] = Future{ compute(2, 5, 40) }

    //val result1: Future[Int] = f1 flatMap {
    //  r1 => f2 map {
    //    r2 => combine(r1, r2)
    // }
    //}


    val result2: Future[Int] =
      for(r1 <- f1;
          r2 <- f2) yield combine(r1 ,r2)

    // DON'T DO THAT, f1 starts and after it finishes f2 is started => not async
    //val result2: Future[Int] =
    //  for(r1 <- Future{compute(1, 5, 60)};
    //      r2 <- Future{ compute(2, 5, 40) }) yield combine(r1 ,r2)

    // the foreach automatically the registered callback on success
    // with prefix failed access failed entries
    //result1 foreach(sum => println(s"sum = $sum"))
    result2 foreach(sum => println(s"sum = $sum"))

    //Await.ready(result1, Duration.Inf);
    Await.ready(result2, Duration.Inf);
    Thread.sleep(50)
  }

  // example: doInParallel(doWork(...), doWork(...))
  def doInParallel_v1[U, V](block1: => Unit, block2: => Unit): Future[Unit] = {
    val f1: Future[Unit] = Future{ block1 }

    val f2: Future[Unit] = Future{ block2 }

    Future{
      Await.ready(f1, Duration.Inf);
      Await.ready(f2, Duration.Inf);
    }
  }

  // use pair
  //def doInParallel_v2[U, V](block1: U, block2: V): Future[(U, V)] = {
  //  Future{

  //  }
  //}


  def sequenceFutures(): Unit = {

  }

  println(s"availableProcessors=${Runtime.getRuntime.availableProcessors}")

  println("==== sequentialInvocation ====")
  //sequentialInvocation()

  println("\n==== simpleFutures ====")
  //simpleFutures()

  println("\n==== futuresWithCallback ====")
  //futuresWithCallback()

  println("\n==== futureComposition ====")
  //futureComposition()

  val f = doInParallel_v1(doWork(1, 5), doWork(2, 5))

  print("does it block")
  Await.ready(f, Duration.Inf);
}
