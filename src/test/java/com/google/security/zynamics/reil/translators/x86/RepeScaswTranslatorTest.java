/*
Copyright 2014 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.google.security.zynamics.reil.translators.x86;

import static org.junit.Assert.assertEquals;

import com.google.security.zynamics.reil.OperandSize;
import com.google.security.zynamics.reil.ReilInstruction;
import com.google.security.zynamics.reil.TestHelpers;
import com.google.security.zynamics.reil.interpreter.CpuPolicyX86;
import com.google.security.zynamics.reil.interpreter.EmptyInterpreterPolicy;
import com.google.security.zynamics.reil.interpreter.Endianness;
import com.google.security.zynamics.reil.interpreter.InterpreterException;
import com.google.security.zynamics.reil.interpreter.ReilInterpreter;
import com.google.security.zynamics.reil.interpreter.ReilRegisterStatus;
import com.google.security.zynamics.reil.translators.InternalTranslationException;
import com.google.security.zynamics.reil.translators.StandardEnvironment;
import com.google.security.zynamics.reil.translators.x86.RepeTranslator;
import com.google.security.zynamics.reil.translators.x86.ScasGenerator;
import com.google.security.zynamics.zylib.disassembly.MockInstruction;
import com.google.security.zynamics.zylib.disassembly.MockOperandTree;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public class RepeScaswTranslatorTest {
  private final ReilInterpreter interpreter = new ReilInterpreter(Endianness.LITTLE_ENDIAN,
      new CpuPolicyX86(), new EmptyInterpreterPolicy());

  private final StandardEnvironment environment = new StandardEnvironment();

  private final RepeTranslator translator = new RepeTranslator(new ScasGenerator(),
      OperandSize.WORD);

  private final ArrayList<ReilInstruction> instructions = new ArrayList<ReilInstruction>();

  @Test
  public void testSearchForwardZFClear() throws InternalTranslationException, InterpreterException {
    interpreter.setRegister("eax", BigInteger.valueOf(0x12345678), OperandSize.DWORD,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("edi", BigInteger.valueOf(0x1000), OperandSize.DWORD,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("ecx", BigInteger.valueOf(0x4), OperandSize.DWORD,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("DF", BigInteger.valueOf(0), OperandSize.BYTE,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("ZF", BigInteger.valueOf(0), OperandSize.BYTE,
        ReilRegisterStatus.DEFINED);

    interpreter.getMemory().store(0x1000, 0x56781234, 4);

    final MockInstruction instruction =
        new MockInstruction("repe scasb", new ArrayList<MockOperandTree>());

    translator.translate(environment, instruction, instructions);

    interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(0x100));

    assertEquals(9, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());

    assertEquals(BigInteger.valueOf(0x12345678), interpreter.getVariableValue("eax"));
    assertEquals(BigInteger.valueOf(0x1002), interpreter.getVariableValue("edi"));

    assertEquals(BigInteger.valueOf(4L), BigInteger.valueOf(interpreter.getMemorySize()));
  }

  @Test
  public void testSearchForwardZFSet() throws InternalTranslationException, InterpreterException {
    interpreter.setRegister("eax", BigInteger.valueOf(0x12345678), OperandSize.DWORD,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("edi", BigInteger.valueOf(0x1000), OperandSize.DWORD,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("ecx", BigInteger.valueOf(0x4), OperandSize.DWORD,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("DF", BigInteger.valueOf(0), OperandSize.BYTE,
        ReilRegisterStatus.DEFINED);
    interpreter.setRegister("ZF", BigInteger.valueOf(1), OperandSize.BYTE,
        ReilRegisterStatus.DEFINED);

    interpreter.getMemory().store(0x1000, 0x56781234, 4);

    final MockInstruction instruction =
        new MockInstruction("repe scasb", new ArrayList<MockOperandTree>());

    translator.translate(environment, instruction, instructions);

    interpreter.interpret(TestHelpers.createMapping(instructions), BigInteger.valueOf(0x100));

    assertEquals(9, TestHelpers.filterNativeRegisters(interpreter.getDefinedRegisters()).size());

    assertEquals(BigInteger.valueOf(0x12345678), interpreter.getVariableValue("eax"));
    assertEquals(BigInteger.valueOf(0x1002), interpreter.getVariableValue("edi"));

    assertEquals(BigInteger.valueOf(4L), BigInteger.valueOf(interpreter.getMemorySize()));
  }
}
