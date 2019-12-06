package basics

import scala.runtime.RichInt

object FunctionalProgramming extends App {
  println("hello from scala")
  val numbers: Seq[Int] = Seq(1, 2, 3)
  val numbers2: Seq[Int] = 1 to 5
  val numbers3: Seq[Int] = new RichInt(1).to(5)

  println("-------------------")
  numbers.foreach (println(_))
  println("-------------------")
  numbers foreach println
  println("-------------------")
  numbers.foreach(i => println(i))
  println("-------------------")
  for (i <- numbers) {
    println(i)
  }
  println("-------------------")
  numbers map(i => i*i) foreach println

  println("-------------------")
  numbers map{i => i*i} foreach println

  println("-------------------")
  numbers map{i => i*i} filter(i => i %2 == 1) foreach println

  println("-------------------")
  numbers map{i => i*i} filter{_ %2 == 1} foreach println

  println("-------- reduce -----------")
  val sum1: Int = numbers reduce((s, i) => s+i*i)
  println(s"sum1=$sum1")

  // Seq.empty[Int] reduce((s, i) => s+i) // error
  println("-------- foldLeft -----------")
  val sum2: Int = numbers.foldLeft(0)((s,i) => s + i * i)
  println(s"sum2=$sum2")

  println("-------- foldLeft in Detail -----------")
  val res = (1 to 3).foldLeft("0")((s,i) => s"f($s, $i)")
  println(s"res of foldLeft=$res")

  println("-------- foldRight in Detail -----------")
  val res2 = (1 to 3).foldRight("0")((s,i) => s"f($s, $i)")
  println(s"res of foldRight=$res2")

  println("--------- currying ----------")
  val f: ((Int, Int) => Int) => Int = numbers.foldLeft(0)
  val sum3 = f((s,i) => s + i*i)
  println(s"sum3=$sum3")
}
