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

package io.github.shiruka.shiruka.natives;

/**
 * a class that contains address and its size.
 */
public final class SizedMemoryPointer {

  /**
   * the address.
   */
  private final long address;

  /**
   * the size.
   */
  private final int size;

  /**
   * ctor.
   *
   * @param address the address.
   * @param size the size.
   */
  public SizedMemoryPointer(final long address, final int size) {
    this.address = address;
    this.size = size;
  }

  /**
   * obtains the addres.
   *
   * @return the address.
   */
  public long getAddress() {
    return this.address;
  }

  /**
   * botains the size.
   *
   * @return the size.
   */
  public int getSize() {
    return this.size;
  }
}