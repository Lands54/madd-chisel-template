package madd

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class StridePrefetcher(val addressWidth: Int, val pcWidth: Int) extends Module {
  val io = IO(new Bundle {
    val pc = Input(UInt(pcWidth.W))
    val address = Input(UInt(addressWidth.W))
    val prefetch_address = Output(UInt(addressWidth.W))
    val prefetch_valid = Output(UInt(1.W))
  })
class list(val addressWidth: Int, val pcWidth: Int) extends Bundle{
  val PCS = RegInit(0.U(pcWidth.W))
  val ADS = RegInit(0.U(addressWidth.W))
  val PDS = RegInit(0.U(addressWidth.W))

}
  val count = RegInit(0.U(32.W))
  when(count >= 1024.U) {
    count := 0.U
  }
  var data_in = RegInit(Wire(new list(addressWidth,pcWidth)))
  data_in.ADS <= io.address
  data_in.PCS <= io.pc
  var file = RegInit(Wire(Vec(1024,new list(addressWidth,pcWidth))))
  file(count).PCS <= io.pc
  file(count).ADS <= io.address
  when(count > 0.U) {
    data_in.PDS <= data_in.ADS - file(count-1.U).ADS
  }.otherwise {
    data_in.PDS <= 4.U
  }
  file(count) := data_in
  when(count>0.U){
  when(file(count).PDS===file(count-1.U).PDS){
  io.prefetch_address := file(count).ADS+file(count).PDS
  io.prefetch_valid := 1.U
  }.otherwise{
  io.prefetch_address := file(count).ADS+file(count).PDS
  io.prefetch_valid := 0.U
  }
  }.otherwise{
  io.prefetch_address := 4.U
  io.prefetch_valid := 1.U
  }
  }
