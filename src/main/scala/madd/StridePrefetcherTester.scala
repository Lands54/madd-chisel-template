package madd

import chisel3._
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class StridePrefetcherTester(dut:StridePrefetcher)extends PeekPokeTester(dut) {
  var acc = BigInt(0)
  var ful = BigInt(0)
  for(i <-0.U to 1024.U by 4.U)
  {
  poke(dut.io.pc, i)
  poke(dut.io.address,i)
  step(1)
  if(peek(dut.io.prefetch_address)==(i+4.U)).litValue(){
    acc = acc + 1
  }
  if(peek(dut.io.prefetch_valid)==(1.U)).litValue(){
    ful = ful + 1
  }
  }
  println("0,4,8..ACCURATE is %f%%".format(acc.toDouble/256.0))
  println("0,4,8..FULLRATE is %f%%".format(ful.toDouble/256.0))
  var acct = BigInt(0)
  var fult = BigInt(0)
  for(i <-0.U to 1024.U by 8.U)
  {
  poke(dut.io.pc, i)
  poke(dut.io.address,i)
  if(peek(dut.io.prefetch_address)==(i+8.U)).litValue(){
    acct = acct + 1
  }
  if(peek(dut.io.prefetch_valid)==(1.U).litValue()){
    fult = fult + 1
  }

  }
  println("0,8,16..ACCURATE is %f%%".format(acct.toDouble/128.0))
  println("0,8,16..FULLRATE is %f%%".format(fult.toDouble/128.0))
}
class StridePrefetcherSpec extends ChiselFlatSpec {
  private val addressWidth = 32
  private val pcWidth = 32
  private val backendName = "firrtl"

  "StridePrefetcher" should s"work correctly with $backendName backend" in {
    Driver(() => new StridePrefetcher(addressWidth, pcWidth), backendName) { dut =>
      new StridePrefetcherTester(dut)
    } should be(true)
  }
}
object StridePrefetcherTester extends App {
  chisel3.iotesters.Driver.execute(args, () => new StridePrefetcher(32, 32)) {
    c => new StridePrefetcherTester(c)
  }
}
