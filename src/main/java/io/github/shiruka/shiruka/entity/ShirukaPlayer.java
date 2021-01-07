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

package io.github.shiruka.shiruka.entity;

import io.github.shiruka.api.entity.Player;
import io.github.shiruka.api.events.LoginDataEvent;
import io.github.shiruka.api.metadata.MetadataValue;
import io.github.shiruka.api.plugin.Plugin;
import io.github.shiruka.shiruka.ShirukaServer;
import io.github.shiruka.shiruka.misc.GameProfile;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an implementation for {@link Player}.
 */
public final class ShirukaPlayer implements Player {

  /**
   * the chain data.
   */
  @NotNull
  private final LoginDataEvent.ChainData chainData;

  /**
   * the connection.
   */
  @NotNull
  private final ShirukaPlayerConnection connection;

  /**
   * the profile.
   */
  @NotNull
  private final GameProfile profile;

  /**
   * ctor.
   *
   * @param chainData the chain data.
   * @param connection the connection.
   * @param profile the profile.
   */
  public ShirukaPlayer(@NotNull final LoginDataEvent.ChainData chainData,
                       @NotNull final ShirukaPlayerConnection connection, @NotNull final GameProfile profile) {
    this.chainData = chainData;
    this.connection = connection;
    this.profile = profile;
  }

  @Override
  public void disconnect(@Nullable final String reason) {
    this.connection.disconnect(reason);
    this.getServer().removePlayer(this);
  }

  @NotNull
  @Override
  public ShirukaServer getServer() {
    return this.connection.getServer();
  }

  @NotNull
  @Override
  public List<MetadataValue> getMetadata(@NotNull final String key) {
    return Collections.emptyList();
  }

  @Override
  public boolean hasMetadata(@NotNull final String key) {
    return false;
  }

  @Override
  public void removeAllMetadata(@NotNull final String key) {
    // @todo #1:15m Implement removeAllMetadata method.
  }

  @Override
  public void removeMetadata(@NotNull final String key, @NotNull final Plugin plugin) {
    // @todo #1:15m Implement removeMetadata method.
  }

  @Override
  public void setMetadata(@NotNull final String key, @NotNull final MetadataValue value) {
    // @todo #1:15m Implement setMetadata method.
  }

  /**
   * obtains the player connection.
   *
   * @return player connection.
   */
  @NotNull
  public ShirukaPlayerConnection getPlayerConnection() {
    return this.connection;
  }

  @NotNull
  @Override
  public String name() {
    return "null";
  }

  @Override
  public void sendMessage(@NotNull final String message) {
    // @todo #1:15m Implement sendMessage method.
    System.out.println(message);
  }

  @Override
  public void tick() {
  }
}
