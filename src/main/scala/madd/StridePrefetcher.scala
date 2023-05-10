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
  val PCS = UInt(pcWidth.W)
  val ADS = UInt(addressWidth.W)
  val PDS = UInt(addressWidth.W)
}
  val count = RegInit(0.U(32.W))
  when(count >= 10240.U) {
    count := count % 10240.U
  }

  val file = Reg(Vec(10240,new list(addressWidth,pcWidth)))
  file(count).PCS := io.pc
  file(count).ADS := io.address
  file(count).PDS := 0.U
  when(count > 0.U) {
    file(count).PDS := file(count).ADS - file(count - 1.U).ADS
    when(file(count).PDS === file(count - 1.U).PDS) {
      io.prefetch_address := file(count).ADS + file(count).PDS
      io.prefetch_valid := 1.U
    }.otherwise {
      io.prefetch_valid := 0.U
    }
  }
  count := count + 1.U
}
