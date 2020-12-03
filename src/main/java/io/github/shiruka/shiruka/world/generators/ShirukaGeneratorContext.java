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

package io.github.shiruka.shiruka.world.generators;

import io.github.shiruka.api.base.Material;
import io.github.shiruka.api.world.generators.GeneratorContext;
import io.github.shiruka.shiruka.misc.CdlUnchecked;
import io.github.shiruka.shiruka.world.anvil.AnvilChunkSection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;

/**
 * an implementation for {@link GeneratorContext}.
 */
public final class ShirukaGeneratorContext implements GeneratorContext {

  /**
   * the container for running generator tasks in this context.
   */
  private final Executor container;

  /**
   * the count of threads active for termination used for termination signalling.
   */
  private final LongAdder count = new LongAdder();

  /**
   * whether or not section skylight is written for the chunk to be generated.
   */
  private final boolean doSkylight;

  /**
   * mapping of highest Y.
   */
  private final AtomicIntegerArray maxY = new AtomicIntegerArray(256);

  /**
   * the last random value, used for the PRNG generator.
   */
  @NotNull
  private final AtomicLong random;

  /**
   * list of chunk sections.
   */
  private final AtomicReferenceArray<AnvilChunkSection> sections = new AtomicReferenceArray<>(16);

  /**
   * the seed to be used for generation.
   */
  private final long seed;

  /**
   * queue of generation tasks to be handle upon command.
   */
  private final Queue<Consumer<CdlUnchecked>> tasks = new ConcurrentLinkedQueue<>();

  /**
   * ctor.
   *
   * @param container where to run generator tasks.
   * @param seed the seed.
   * @param doSkylight whether to generate skylight.
   */
  public ShirukaGeneratorContext(@NotNull final Executor container, final long seed, final boolean doSkylight) {
    this.container = container;
    this.seed = seed;
    this.doSkylight = doSkylight;
    this.random = new AtomicLong(seed);
  }

  /**
   * builds the given block getState given the ID number and the metadata value.
   *
   * @param id the block ID.
   * @param meta the block meta.
   *
   * @return the block getState.
   */
  private static short build(final int id, final byte meta) {
    return (short) (id << 4 | meta);
  }

  /**
   * http://minecraft.gamepedia.com/Chunk_format
   * int BlockPos = y*16*16 + z*16 + x;
   * <p>
   * return (y * (2^8)) + (z * (2^4)) + x;
   * use OR instead because bitwise ops are faster and
   * provides the same results as addition
   * <p>
   * max size of this array is blocks in section, 4096
   * 16*16*16
   */
  private static int idx(final int x, final int y, final int z) {
    return y << 8 | z << 4 | x;
  }

  /**
   * obtains the section number for the given Y value.
   *
   * @param y the y value.
   *
   * @return the section number for that Y value.
   */
  private static int section(final int y) {
    return y >> 4;
  }

  /**
   * copies the height map to the given array.
   *
   * @param array the array to copy to.
   */
  public void copyHeights(@NotNull final AtomicIntegerArray array) {
    for (int i = 0; i < array.length(); i++) {
      array.set(i, this.maxY.get(i));
    }
  }

  /**
   * obtains the collection of chunk sections that were generated by the context as an array.
   *
   * @param sections the array of chunk sections which to copy the generated.
   */
  public void copySections(@NotNull final AtomicReferenceArray<AnvilChunkSection> sections) {
    IntStream.range(0, this.sections.length())
      .forEach(i -> sections.set(i, this.sections.get(i)));
  }

  /**
   * sends the command for the container to handle the tasks that were scheduled by the terrain generator.
   *
   * @param latch the count down latch used to await for the generation to finish before proceeding.
   */
  public void doRun(@NotNull final CdlUnchecked latch) {
    this.tasks.stream()
      .<Runnable>map(consumer -> () -> consumer.accept(latch))
      .forEach(this.container::execute);
  }

  /**
   * obtains the latch in order to determine the amount of runs necessary to complete all of
   * the scheduled generation tasks.
   *
   * @return the count down latch argument.
   */
  @NotNull
  public CdlUnchecked getCount() {
    return new CdlUnchecked(this.count.intValue());
  }

  @Override
  public int maxHeight(final int x, final int z) {
    return this.maxY.get(x << 4 | z & 0xF);
  }

  @Override
  public int nextInt() {
    return this.nextInt(Integer.MAX_VALUE);
  }

  @Override
  public int nextInt(final int max) {
    return (int) this.nextLong() % max;
  }

  @Override
  public long nextLong() {
    while (true) {
      final long l = this.random.get();
      long x = l;
      x ^= x << 21;
      x ^= x >>> 35;
      x ^= x << 4;
      if (x != 0 && this.random.compareAndSet(l, x)) {
        return x;
      }
    }
  }

  @Override
  public long nextLong(final long max) {
    return this.nextLong() % max;
  }

  @Override
  public void run(@NotNull final Runnable r) {
    this.count.increment();
    this.tasks.offer(cdl -> {
      r.run();
      cdl.countDown();
    });
  }

  @Override
  public long seed() {
    return this.seed;
  }

  @Override
  public void set(final int x, final int y, final int z, @NotNull final Material material, final byte meta) {
    this.set(x, y, z, ShirukaGeneratorContext.build(material.getId(), meta));
  }

  @Override
  public void set(final int x, final int y, final int z, @NotNull final Material material) {
    this.set(x, y, z, ShirukaGeneratorContext.build(material.getId(), (byte) 0));
  }

  @Override
  public void set(final int x, final int y, final int z, final int id, final byte meta) {
    this.set(x, y, z, ShirukaGeneratorContext.build(id, meta));
  }

  /**
   * resets the task runner and the available tasks left counter in order to reuse the same context for prop generators.
   */
  public void reset() {
    this.count.reset();
    this.tasks.clear();
  }

  /**
   * sets the block at the given coordinates to the given block getState value.
   *
   * @param x the x coordinate.
   * @param y the y coordinate.
   * @param z the z coordinate.
   * @param state the block to set.
   */
  private void set(final int x, final int y, final int z, final short state) {
    final var sectionIdx = ShirukaGeneratorContext.section(y);
    final var idx = ShirukaGeneratorContext.idx(x, y & 15, z);
    final var xz = x << 4 | z & 0xF;
    var section = this.sections.get(sectionIdx);
    if (section == null) {
      final var newSec = new AnvilChunkSection(this.doSkylight);
      if (this.sections.compareAndSet(sectionIdx, null, newSec)) {
        section = newSec;
      } else {
        section = this.sections.get(sectionIdx);
      }
    }
    int lastMax;
    do {
      lastMax = this.maxY.get(xz);
      if (y <= lastMax) {
        break;
      }
    } while (!this.maxY.compareAndSet(xz, lastMax, y));
    section.set(idx, state);
  }
}