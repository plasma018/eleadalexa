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

import java.io.Serializable;

import com.amazon.alexa.message.Payload;

public class VolumeStatePayload extends Payload implements Serializable {
  private Long volume = null;
  private Boolean muted = null;

  public VolumeStatePayload(Long volume, Boolean muted) {
    this.volume = volume;
    this.muted = muted;
  }

  public VolumeStatePayload(Long volume) {
    this.volume = volume;
  }

  public VolumeStatePayload(Boolean muted) {
    this.muted = muted;
  }

  public long getVolume() {
    return volume;
  }

  public boolean getMuted() {
    return muted;
  }
}
