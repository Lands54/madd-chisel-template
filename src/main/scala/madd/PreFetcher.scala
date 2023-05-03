import chisel3._
import chisel3.util._

class StridePrefetcher(val addressWidth: Int, val pcWidth: Int) extends Module {
  val io = IO(new Bundle {
    val pc = Input(UInt(pcWidth.W))
    val address = Input(UInt(addressWidth.W))
    val prefetch_address = Output(UInt(addressWidth.W))
    val prefetch_valid = Output(Bool())
  })

  val count = RegInit(0.U(10.W))
  when(count >= 1024.U) {
    count := count % 1024.U
  }

  val file = Mem(1024, Vec(3, UInt(pcWidth.W)))
  file(count)(0) := io.pc
  file(count)(1) := io.address
  when(count > 0.U) {
    file(count)(2) := io.address - file(count - 1.U)(1)
    when(file(count)(2) === file(count - 1.U)(2)) {
      io.prefetch_address := file(count)(1) + file(count)(2)
      io.prefetch_valid := true.B
    }.otherwise {
      io.prefetch_valid := false.B
    }
  }
}
