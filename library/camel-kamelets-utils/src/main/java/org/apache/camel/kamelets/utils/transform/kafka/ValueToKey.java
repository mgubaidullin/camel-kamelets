/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.kamelets.utils.transform.kafka;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.util.ObjectHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValueToKey {

    public void process(@ExchangeProperty("fields") String fields, Exchange ex) throws InvalidPayloadException {
        List<String> splittedFields = new ArrayList<>();
        Map<Object, Object> body = ex.getMessage().getMandatoryBody(Map.class);
        if (ObjectHelper.isNotEmpty(fields)) {
            splittedFields = Arrays.stream(fields.split(",")).collect(Collectors.toList());
        }

        Map<Object, Object> key = new HashMap<>();
        for (Map.Entry entry:
             body.entrySet()) {
            final String fieldName = (String) entry.getKey();
            if (filterNames(fieldName, splittedFields)) {
                final Object fieldValue = entry.getValue();
                key.put(entry.getKey(), fieldValue);
            }
        }

        ex.getMessage().setHeader("kafka.KEY", key);
    }

    boolean filterNames(String fieldName, List<String> splittedFields) {
        return splittedFields.contains(fieldName);
    }
}
