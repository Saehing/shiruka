/*
 * MIT License
 *
 * Copyright (c) 2021 Shiru ka
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

package io.github.shiruka.shiruka.concurrent;

import io.github.shiruka.api.Server;
import io.github.shiruka.shiruka.misc.JiraExceptionCatcher;
import io.github.shiruka.shiruka.scheduler.SyncTaskManager;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents the server heartbeat pulse called "tick" which occurs every 1/20th of a second.
 */
public final class ShirukaTick implements Runnable {

  /**
   * the logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("ShirukaTick");

  /**
   * the amount of time taken by a single tick
   */
  private static final long TICK_MILLIS = TimeUnit.SECONDS.toMillis(1) / 20;

  /**
   * the server.
   */
  @NotNull
  private final Server server;

  /**
   * ctor.
   *
   * @param server the server.
   */
  public ShirukaTick(@NotNull final Server server) {
    this.server = server;
  }

  /**
   * starts the heartbeat.
   */
  @Override
  public void run() {
    while (this.server.isRunning()) {
      final var start = System.currentTimeMillis();
      SyncTaskManager.update(start);
      // @todo #1:15m Add more tick operations.
      final var end = System.currentTimeMillis();
      final var elapsed = end - start;
      final var waitTime = ShirukaTick.TICK_MILLIS - elapsed;
      if (waitTime < 0) {
        ShirukaTick.LOGGER.debug("Server running behind {}ms, skipped {} ticks",
          -waitTime, -waitTime / ShirukaTick.TICK_MILLIS);
        continue;
      }
      try {
        Thread.sleep(waitTime);
      } catch (final InterruptedException e) {
        this.server.stopServer();
      } catch (final Exception e) {
        JiraExceptionCatcher.serverException(e);
        break;
      }
    }
  }
}
