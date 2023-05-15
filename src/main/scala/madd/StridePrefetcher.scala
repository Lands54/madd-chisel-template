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

  class List extends Bundle {
    val PCS = UInt(pcWidth.W)
    val ADS = UInt(addressWidth.W)
    val PDS = UInt(addressWidth.W)
  }

  val count = RegInit(0.U(32.W))
  when(count >= 1024.U) {
    count := 0.U
  }

  val data_in = Wire(new List)
  data_in.ADS := io.address
  data_in.PCS := io.pc
  val file = RegInit(VecInit(Seq.fill(1024)(0.U.asTypeOf(new List))))

  file(count).PCS := io.pc
  file(count).ADS := io.address

  when(count > 0.U) {
    data_in.PDS := data_in.ADS - file(count-1.U).ADS
  }.otherwise {
    data_in.PDS := 4.U
  }

  file(count) := data_in

  when(count > 0.U) {
    when(file(count).PDS === file(count-1.U).PDS) {
      io.prefetch_address := file(count).ADS + file(count).PDS
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
