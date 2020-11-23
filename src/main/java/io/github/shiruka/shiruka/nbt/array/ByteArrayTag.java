/*
 * MIT License
 *
 * Copyright (c) 2020 Shiru ka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.shiruka.shiruka.nbt.array;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public final class ByteArrayTag extends ArrayTagEnvelope<Byte> {

  /**
   * ctor.
   *
   * @param original the original.
   */
  public ByteArrayTag(@NotNull final Byte @NotNull [] original) {
    super(original);
  }

  @Override
  public boolean isByteArray() {
    return true;
  }

  @NotNull
  @Override
  public ByteArrayTag asByteArray() {
    return this;
  }

  @Override
  public byte id() {
    return 7;
  }

  @Override
  public void write(@NotNull final DataOutput output) throws IOException {
    final var value = ArrayUtils.toPrimitive(this.value());
    output.writeInt(value.length);
    output.write(value);
  }
}