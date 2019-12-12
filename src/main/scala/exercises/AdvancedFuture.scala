package exercises
import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
// imports execution context for implicit passing
import scala.concurrent.ExecutionContext.Implicits.global

object AdvancedFuture extends App {
  // 1.3 a)
  def parallelMax1(list: List[Int], parts: Int): Future[Int] = {
    Future {
      val size = list.size
      if (size == 0) throw new IllegalArgumentException("List is empty")
      val partitionSize: Int = size / parts
      if (partitionSize < 1) throw new IllegalStateException("Invalid Partition Size")
      partitionSize
    }.flatMap(partitionSize =>
      Future.sequence(list.grouped(partitionSize)
        .map(list => { Future { list.max }}))
        .map(list => list.max)
    )
  }
  // 1.3 b)
  def parallelMax2(list: List[Int], parts: Int): Future[Int] = {
    Future {
      val size = list.size
      if (size == 0) throw new IllegalArgumentException("List is empty")
      val partitionSize: Int = size / parts
      if (partitionSize < 1) throw new IllegalStateException("Invalid Partition Size")
      partitionSize
    }.flatMap(partitionSize =>
      myFutureSequence(list.grouped(partitionSize)
        .map(list => { Future { list.max }}).toList)
        .map(x => x.max))
  }

  // 1.3 b)
  def sequenceRecur[T](values: List[T], futures: List[Future[T]]): Future[List[T]] = {
    if (futures.isEmpty) return Future{ values }
    futures.head.failed.foreach(ex => throw ex)
    futures.head.flatMap(curr => {
      sequenceRecur(values.appended(curr), futures.tail)
    })
  }

// 1.3 b)
  def myFutureSequence[T](futures: List[Future[T]]): Future[List[T]] = {
    sequenceRecur(List.empty[T], futures)
  }

  def generateRandomList(noOfElements: Int, upperBound: Int): List[Int] = {
    val rand = scala.util.Random
    (1 to noOfElements).map(x => rand.nextInt(upperBound)).toList
  }

  def test(): Unit = {

    def registerHandlers[T](f1: Future[T], f2: Future[T], name: String) = {
      for(r1 <- f1;
          r2 <- f2) {
        println(s"===== $name =====\n" +
          s"   result of Max1 = $r1\n" +
          s"   result of Max2 = $r2\n" +
          s"===== END $name =====\n")
      }
      for(ex1 <- f1.failed;
          ex2 <- f2.failed) {
        println(s"===== $name =====\n" +
          s"   Exception Max1 = $ex1\n" +
          s"   Exception Max2 = $ex2\n" +
          s"===== END $name =====\n")
      }
    }
    val randList = generateRandomList(30000000, 1001)

    val f11 = parallelMax1(randList, 10)
    val f12 = parallelMax2(randList, 10)
    registerHandlers(f11, f12, "Valid List with 30.000.000 Elements")

    val f1 = parallelMax1(List(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5), 2)
    val f2 = parallelMax2(List(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5), 2)
    registerHandlers(f1, f2, "Test Success")

    val f3 = parallelMax1(null, 2)
    val f4 = parallelMax2(null, 2)
    registerHandlers(f3, f4, "Test Null")

    val f5 = parallelMax1(List.empty, 2)
    val f6 = parallelMax2(List.empty, 2)
    registerHandlers(f5, f6, "Test Empty List")

    val f7 = parallelMax1(List(1,2,3), 200)
    val f8 = parallelMax2(List(1,2,3), 200)
    registerHandlers(f7, f8, "Test Invalid Partition Size")

    val f9 = parallelMax1(List(1,2,3), -1)
    val f10 = parallelMax2(List(1,2,3), -1)
    registerHandlers(f9, f10, "Negative Partition Size")

    val futureList: Future[List[Int]] = Future.sequence(
      List(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12)
    )
    Await.ready(futureList, Duration.Inf)
  }

  test()
  Thread.sleep(1000)
}
