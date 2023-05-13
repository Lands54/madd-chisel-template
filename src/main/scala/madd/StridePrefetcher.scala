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
  var PCS = UInt(pcWidth.W)
  var ADS = UInt(addressWidth.W)
  var PDS = UInt(addressWidth.W)
}
  val count = RegInit(0.U(32.W))
  when(count >= 1024.U) {
    count := 0.U
  }
  var file = Mem(1024,new list(addressWidth,pcWidth))
  val newData = new list(addressWidth, pcWidth)
  newData.PCS := io.pc
  newData.ADS := io.address
  newData.PDS := 0.U
  file.update(count,newDate)
  when(count > 0.U) {
    newDate.PDS := file.read(count).ADS - file.read(count - 1.U).ADS
    file.update(count,newDate)
    when(file.read(count).PDS === file.read(count - 1.U).PDS) {
      io.prefetch_address := file.read(count).ADS + file.read(count).PDS
      io.prefetch_valid := 1.U
    }.otherwise {
      io.prefetch_address := file(count).ADS + file(count).PDS
      io.prefetch_valid := 0.U
    }
  }.otherwise {
    io.prefetch_address := 4.U
    io.prefetch_valid := 1.U
  }
  count := count + 1.U
}
