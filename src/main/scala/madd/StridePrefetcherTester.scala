package madd

import chisel3._
import chisel3.util._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}

class StridePrefetcherTester(dut:StridePrefetcher)extends PeekPokeTester(dut) {
  var acc = BigInt(0)
  var ful = BigInt(0)
  var count = BigInt(0)
  for(i <-0.U to 40959.U by 4.U)
  {
  poke(dut.io.pc,i)
  poke(dut.io.address,i)
  if(peek(dut.io.prefetch_valid)==1){
    ful = ful + 1
    if(peek(dut.io.prefetch_address)==i+4){
    acc = acc + 1
  }
  }
  step(1)
  count = count+1
  }
  println("0,4,8..ACCURATE is %f%%(num:%d)".format((acc.toDouble/ful.toDouble)*100,count))
  println("0,4,8..FULLRATE is %f%%(num:%d)".format((ful.toDouble/count.toDouble)*100,count))
  var acct = BigInt(0)
  var fult = BigInt(0)
  var countt = BigInt(0)
  for(i <-0.U to 40959.U by 8.U)
  {
  poke(dut.io.pc, i)
  poke(dut.io.address,i)

  if(peek(dut.io.prefetch_valid)==1){
    fult = fult + 1
    if(peek(dut.io.prefetch_address)==i+8){
    acct = acct + 1
  }
  }
  step(1)
  countt = countt+1
  }
  println("0,8,16..ACCURATE is %f%%(num:%d)".format((acct.toDouble/fult.toDouble)*100,countt))
  println("0,8,16..FULLRATE is %f%%(num:%d)".format((fult.toDouble/countt.toDouble)*100,countt))
  println("————————————————————————————————")
  var total_count = count + countt
  var total_acc = acc + acct
  var total_ful = ful +fult
  println("Total Test Number:%d\nTotal ACCURATE:%f%%\nTotal FULLRATE:%f%%".format(total_count,100*total_acc.toDouble/total_count.toDouble,100*total_ful.toDouble/total_count.toDouble))
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
