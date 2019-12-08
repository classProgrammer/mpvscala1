package exercises

import java.util.concurrent.TimeUnit

import akka.actor.Scheduler

import scala.annotation.tailrec
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.duration.{Duration, FiniteDuration}


object AdvancedFuture2 extends App {
  val system = akka.actor.ActorSystem()
  implicit val scheduler = system.scheduler
  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  def waitFor[T] (future: Future[T]): Unit = {
    Await.ready(future, Duration.Inf)
  }

  def getValue[T](p: Promise[T], computation: => T, delay: FiniteDuration, retries: Int)(ec: ExecutionContext, s: Scheduler): Unit = {
    Future {
      if (retries == 0) p failure new Exception("Computation failed too often")
      val f = Future { computation }(ec)
      f.foreach(x => p success x)(ec)
      f.failed.foreach(_ => {
          s.scheduleOnce(delay) {
            println(s"   Retries left $retries")
            getValue(p, computation, delay, retries - 1)(ec, s)
          }(ec)
        }
      )(ec)
    }(ec)
  }

  def retryAsync[T](computation: => T, delay: FiniteDuration, retries: Int)
                   (implicit ec: ExecutionContext, s: Scheduler): Future[T] = {
    val p = Promise[T]()
    getValue(p, computation, delay, retries)(ec, s)
    p.future
  }

  val generator = scala.util.Random

  def produce1outta10(): Int = {
    Thread.sleep(250)
    if (generator.nextInt(10) == 1) generator.nextInt(1000)
    else throw new Exception("Some Exception")
  }

  def produce1outta100(): Int = {
    Thread.sleep(250)
    if (generator.nextInt(100) == 1) generator.nextInt(1000)
    else throw new Exception("Some Exception")
  }

  def produceAlways(): Int = {
    generator.nextInt(1000)
  }

  def test[T](retries: Int, producer: => T, probability: String): Unit = {
    val a: Future[T] = retryAsync(producer, Duration.create(250, TimeUnit.MILLISECONDS), retries)
    println(s"======== START test with $retries retries and Success Ratio of $probability ========")
    a.foreach(x => println(s"   RESULT = $x"))
    a.failed.foreach(ex => println(s"   $ex"))
    Await.ready(a, Duration.Inf)
    Thread.sleep(50)
    println(s"======== END test ========\n")
  }

  test(30, produce1outta10, "1/10")
  test(5, produce1outta100, "1/100")
  test(100, produceAlways, "1")

  Await.ready(system.terminate(), Duration.Inf)
}
