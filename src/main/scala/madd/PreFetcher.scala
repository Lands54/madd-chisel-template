import chisel3._
import chisel3.util._
class dataunit extends Bundle
{
  val precommand = 
}
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
  val count = RegInit(0,Uint(10.W))
  when(count>=1024)
  {
    count=count%1024
  }
  val file=Mem(1024,Mem(3,UInt(pcWidth.W))++)
  file(count)(0)=io.pc
  file(count)(1)=io.address
  when(count>0)
  {
    file(count)(2)=io.address-file(count-1)(1)
    if(file(count)(2)==file(count-1)(2))
    {
      io.prefetch_address=file(count)(1)+file(count)(2)
      io.prefetch_valid = 1
    }
    else
    {
      io.prefetch_valid = 0
    }
    
  }


  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  // ...
  
  // 是否预取的逻辑
  io.prefetch_valid := // ...
}
