package exercises

import java.util.concurrent.TimeUnit

import akka.actor.Scheduler

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.duration.{FiniteDuration, Duration}
// imports execution context for implicit passing
import scala.concurrent.ExecutionContext.Implicits.global

object AdvancedFuture2 extends App {
  val system = akka.actor.ActorSystem()
  implicit val scheduler = system.scheduler

  

  def retryAsync[T](computation: => T, delay: FiniteDuration, retries: Int)
                   (implicit ec: ExecutionContext, s: Scheduler): Future[T] = {
    val a: Future[T] = Future {
      val default: T = null.asInstanceOf[T]
      var res: T = default
      var p = Promise[T]()
      var i = 0

      val f = Future {
        computation
      }(ec)
      p completeWith f

      // for n tries
      while (res == default && i <= retries) {
        println(s"Retry number $i")

        p.future.foreach(x => res = x)(ec)
        Await.ready(p.future, Duration.Inf)
        i += 1

        if (res == default) {
          p = Promise[T]()
          scheduler.scheduleOnce(delay) {
            val f = Future {
              computation
            }(ec)
            p completeWith f
          }(ec)
        }
      }
      system.terminate()
      if (res != default) {
        println("success")
        res
      }
      else {
        println("failed")
        throw new Exception("Computation failed too often")
      }
    }(ec)
    a
  }

  val generator = scala.util.Random

  def produce(): Int = {
    if (generator.nextInt(10) == 1) 1
    else throw new Exception("Some Exception")
  }

  def main() = {
    val a: Future[Int] = retryAsync(produce, Duration.create(500, TimeUnit.MILLISECONDS) , 12)
    println("blocking?")
    a.foreach(x => println(s"RESULT = $x"))
    a.failed.foreach(ex => println(ex))
    Await.ready(a, Duration.Inf)
    Thread.sleep(50)
  }

  main()
}
