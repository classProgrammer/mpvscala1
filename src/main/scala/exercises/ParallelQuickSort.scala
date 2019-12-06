package exercises

object ParallelQuickSort extends App{
  def quickSort[T](seq: Seq[T])(implicit ord: Ordering[T]): Seq[T] = {
    if (seq.length <= 1) seq
    else {
      val pivot = seq(seq.length / 2)
      Seq.concat(quickSort(seq filter (ord.lt(_, pivot))),
        seq filter (ord.equiv(_, pivot)),
        quickSort(seq filter (ord.gt(_, pivot))))
    }
  }

  object Ascender extends Ordering[Int] {
    def compare(left:Int, right:Int) = left compare right
  }

  object Descender extends Ordering[Int] {
    def compare(left:Int, right:Int) = right compare left
  }


  def smallTestProgram(): Unit = {
    val arr = Seq(1,4,7,2,3,9,7,5,10,12)
    val res1 = quickSort(arr)(Ascender)
    val res2 = quickSort(arr)(Descender)

    println(s"ascending: $res1")
    println(s"descending: $res2")
  }

  smallTestProgram()

}
