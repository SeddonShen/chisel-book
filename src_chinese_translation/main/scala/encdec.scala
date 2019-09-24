import chisel3._
import chisel3.iotesters.PeekPokeTester

//- start encdec_util
//- 开始编码解码工具
import chisel3.util._
//- end
//- 结束

class EncDec extends Module {
  val io = IO(new Bundle {
    val decin = Input(UInt(2.W))
    val decout = Output(UInt(4.W))
    val encin = Input(UInt(4.W))
    val encout = Output(UInt(2.W))
  })

  val sel = io.decin

  val result = Wire(UInt(4.W))

  //- start encdec_dec
  //- 开始编码解码的解码

  result := 0.U

  switch(sel) {
    is (0.U) { result := 1.U}
    is (1.U) { result := 2.U}
    is (2.U) { result := 4.U}
    is (3.U) { result := 8.U}
  }
  //- end
  //- 结束

  //- start encdec_decbin
  //- 开始编码解码的二进制版本解码
  switch (sel) {
    is ("b00".U) { result := "b0001".U}
    is ("b01".U) { result := "b0010".U}
    is ("b10".U) { result := "b0100".U}
    is ("b11".U) { result := "b1000".U}
  }
  //- end
  //- 结束

  //- start encdec_shift
  //- 开始编码解码的移位解码
  result := 1.U << sel
  //- end
  //- 结束

  io.decout := result

  val a = io.encin
  val b = Wire(UInt(2.W))
  //- start encdec_enc
  //- 开始编码解码的编码

  b := "b00".U
  switch (a) {
    is ("b0001".U) { b := "b00".U}
    is ("b0010".U) { b := "b01".U}
    is ("b0100".U) { b := "b10".U}
    is ("b1000".U) { b := "b11".U}
  }
  //- end
  //- 结束

  io.encout := b

}

class EncDecTester(dut: EncDec) extends PeekPokeTester(dut) {

  for (i <- 0 to 3) {
    poke(dut.io.decin, i)
    step(1)
    expect(dut.io.decout, 1 << i)
  }
  for (i <- 0 to 3) {
    poke(dut.io.encin, 1 << i)
    step(1)
    expect(dut.io.encout, i)
  }
}

object EncDecTester extends App {
  chisel3.iotesters.Driver(() => new EncDec()) { c =>
    new EncDecTester(c)
  }
}

