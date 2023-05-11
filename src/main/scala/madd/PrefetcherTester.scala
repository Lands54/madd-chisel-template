package madd

import chisel3._
import chisel3.iotesters.PeekPokeTester
import chisel3.util._

class PrefetcherTester(dut:StridePrefetcher)extends PeekPokeTester(dut) {
  var acc = 0.U
  var ful = 0.U
  for(i <-0.U to 1024.U by 4.U)
  {
  poke(dut.io.pc, i)
  poke(dut.io.address,i)
  step(1)
  when(peek(dut.io.prefetch_address)==BigInt(i.litValue()+4.U)){
    acc := acc + 1.U
  }
  when(peek(dut.io.prefetch_valid)==BigInt(i.litValue()+4.U)){
    ful := ful + 1.U
  }
  }
  println("0,4,8..ACCURATE%.f%",acc.toDouble / 256.0)
  println("0,4,8..FULLRATE%.f%",ful.toDouble / 256.0)
  acc := 0.U
  ful := 0.U
  for(i <-0.U to 1024.U by 8.U)
  {
  poke(dut.io.pc, i)
  poke(dut.io.address,i)
  step(1)
  when(peek(dut.io.prefetch_address)==i+8.U){
    acc := acc + 1.U
  }
  when(peek(dut.io.prefetch_valid)==1.U){
    ful := ful + 1.U
  }
  }
  println("0,8,16..ACCURATE%.f%",acc.toDouble / 128.0)
  println("0,8,16..FULLRATE%.f%",ful.toDouble / 128.0)
}

object PrefetcherTester extends App {
  chisel3.iotesters.Driver(() => new StridePrefetcher(32, 64) { dut =>
    new PrefetcherTester(dut)}
  )
}
