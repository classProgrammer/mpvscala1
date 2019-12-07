package exercises
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
// imports execution context for implicit passing
import scala.concurrent.ExecutionContext.Implicits.global
import javax.naming.directory.InvalidAttributeValueException

object AdvancedFuture extends App {
  // 1.3 a)
  def parallelMax1(list: List[Int], parts: Int): Future[Int] = {
    Future {
      val size = list.size
      if (size == 0) throw new IllegalArgumentException("List is empty")
      val partitionSize: Int = size / parts
      if (partitionSize == 0) throw new IllegalStateException("Invalid Partition Size")

      val lists = list.grouped(partitionSize)

      var futureList = List.empty[Future[Int]]

      lists.foreach(list => {
        val a: Future[Int] = Future {
          var max = Int.MinValue
          list.foreach(elem => if(elem > max) max = elem)
          max
        }
        futureList = a :: futureList
      })

      var max = Int.MinValue
      futureList.foreach(value =>
        value.foreach(v =>{
          if (v > max) max = v
        })
      )

      val x: Future[List[Int]] = Future.sequence(futureList)
      Await.ready(x, Duration.Inf)

      max
    }
  }
  // 1.3 b)
  def parallelMax2(list: List[Int], parts: Int): Future[Int] = {
    Future {
      val size = list.size

      if (size == 0) throw new IllegalArgumentException("List is empty")
      val partitionSize: Int = size / parts
      if (partitionSize == 0) throw new IllegalStateException("Invalid Partition Size")

      val lists = list.grouped(partitionSize)

      var futureList = List.empty[Future[Int]]

      lists.foreach(list => {
        val a: Future[Int] = Future {
          var max = Int.MinValue
          list.foreach(elem => if (elem > max) max = elem)
          max
        }
        futureList = a :: futureList
      })

      var max = Int.MinValue
      futureList.foreach(value =>
        value.foreach(v => {
          if (v > max) max = v
        })
      )

      val x: Future[List[Int]] = myFutureSequence(futureList)
      Await.ready(x, Duration.Inf)

      max
    }
  }
  // 1.3 b)
  def sequenceRecur[T](values: List[T], futures: List[Future[T]]): List[T] = {
    if (futures.isEmpty) return values

    val first = futures.head
    var v: T = null.asInstanceOf[T]
    first.foreach(value => {
      v = value
    })
    Await.ready(first, Duration.Inf)
    sequenceRecur(v :: values, futures.tail)
  }
// 1.3 b)
  def myFutureSequence[T](futures: List[Future[T]]): Future[List[T]] = {
    Future {
      sequenceRecur(List.empty[T], futures)
    }
  }

  def test(): Unit = {
    val f1 = parallelMax1(List(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5), 2)
    val f2 = parallelMax2(List(1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5), 2)
    f1.foreach(v => println(v))
    f2.foreach(v => println(v))

    Await.ready(f1, Duration.Inf)
    Await.ready(f2, Duration.Inf)

    Thread.sleep(50)

  }

  test()
}
