import chisel3._
import chisel3.util._
class StridePrefetcher(val addressWidth: Int, val pcWidth: Int) extends Module {
  val io = IO(new Bundle {
    // 输入端口
    val pc = Input(UInt(pcWidth.W))
    val address = Input(UInt(addressWidth.W))
    // 输出端口
    val prefetch_address = Output(UInt(addressWidth.W))
    val prefetch_valid = Output(Bool())
  })
  // 实现Stride Prefetcher的逻辑，例如查找表格、步幅计算和预取地址生成等
  val file=Mem(1024,Mem(3,0.UInt(pcWidth.W)))
  file(io.pc)(0)=io.pc
  file(io.pc)(1)=io.address
  file(io.pc)(2)=io.address-file(io.pc-1)(1)
  // ...
  
  // 是否预取的逻辑
  io.prefetch_valid := // ...
}