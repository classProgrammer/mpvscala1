package basics

import scala.util.{Failure, Success, Try}

object Monads extends App{

  def traditionalErrorHandling(): Unit = {
    println("=== Traditional ===")
    for (s <- Seq("5", "2", "x", "0")) {
      try {
        val result = 10 / Integer.parseInt(s)
        println(s"'$s' -> $result")
      } catch{
        case ex: Throwable => println(s"'$s' -> $ex")
      }
    }
  }

  def tryMonad(): Unit = {
    def toInt(s: String): Try[Int] = Try{ s .trim .toInt }
    def divide(a: Int, b: Int): Try[Int] = Try{ a / b }

    println("=== Try Monad ===")
    for (s <- Seq("5", "2", "x", "0")) {
      toInt(s).foreach(i => println(s"'$s' -> $i"))
      toInt(s).failed foreach(ex => println(s"'$s' -> $ex"))
    }
    println("=== Try Monad Success/Failure ===")
    for (s <- Seq("5", "2", "x", "0")) {
      toInt(s) match {
        case Success(i) => println(s"'$s' -> $i")
        case Failure(ex) => println(s"'$s' -> $ex")
      }
    }

    println("=== Try Monad flatMap ===")
    for (s <- Seq("5", "2", "x", "0")) {
      val r: Try[Int] = toInt(s) flatMap(i => divide(10, i))
      //println(s"result = $r")

      r match {
        case Success(i) => println(s"'$s' -> $i")
        case Failure(ex) => println(s"'$s' -> $ex")
      }
    }

    println("=== Try Monad map ===")
    for (s <- Seq("5", "2", "x", "0")) {
      val r: Try[Try[Int]] = toInt(s) map(i => divide(10, i))
      println(s"result = $r")
    }

    println("=== Try Monad flatMap foreach ===")
    for (s <- Seq("5", "2", "x", "0")) {
      val r: Try[Int] = toInt(s) flatMap(i => divide(10, i))
      //println(s"result = $r")

      r foreach(i => println(s"'$s' -> $i"))
    }

    println("=== Try Monad for expression ===")
    for (s <- Seq("5", "2", "x", "0")) {
      val r: Try[Int] =
        for(i <- toInt(s);
            q <- divide(10, i)) yield q

      r foreach(i => println(s"'$s' -> $i"))
    }

    println("=== Try Monad for expression with foreach ===")
    for (s <- Seq("5", "2", "x", "0")) {
      for(i <- toInt(s);
          q <- divide(10, i))
        println(s"'$s' -> $q")
    }
  }

  println("===== MONADs =====")
  traditionalErrorHandling()
  println()
  tryMonad()
}
