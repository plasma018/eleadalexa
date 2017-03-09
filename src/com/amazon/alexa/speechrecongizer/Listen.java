/** 
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *   http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package com.amazon.alexa.speechrecongizer;

import com.amazon.alexa.message.Payload;

public class Listen extends Payload {
    // duration of wait for the customer to open the microphone before issuing a ListenTimeout event
    private Long timeoutInMilliseconds;

    public Long getTimeoutIntervalInMillis() {
        return timeoutInMilliseconds;
    }

    public void setTimeoutIntervalInMillis(Long timeoutIntervalInMillis) {
        this.timeoutInMilliseconds = timeoutIntervalInMillis;
    }
}