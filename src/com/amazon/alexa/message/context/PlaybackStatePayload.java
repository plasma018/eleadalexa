/**
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.amazon.alexa.message.context;

import com.amazon.alexa.message.Payload;

public final class PlaybackStatePayload extends Payload {
  private final String token;
  private final long offsetInMilliseconds;
  private final PlayerActivity playerActivity;

  public PlaybackStatePayload(String token, long offsetInMilliseconds,
      PlayerActivity playerActivity) {
    this.token = token;
    this.offsetInMilliseconds = offsetInMilliseconds;
    this.playerActivity = playerActivity;
  }

  public String getToken() {
    return token;
  }

  public long getOffsetInMilliseconds() {
    return offsetInMilliseconds;
  }

  public PlayerActivity getPlayerActivity() {
    return playerActivity;
  }

  public enum PlayerActivity {
    IDLE, PLAYING, BUFFER_UNDERRUN, FINISHED, STOPPED
  }

}
