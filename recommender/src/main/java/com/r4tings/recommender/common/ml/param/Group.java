/*
 * The Apache License 2.0  Copyright (c) 2023 r4tings.com
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.ml.param;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.r4tings.recommender.common.Constants.COL;

@Slf4j
@RequiredArgsConstructor
public enum Group {
  USER(COL.USER),
  ITEM(COL.ITEM);

  @Getter private final String description;

  private static final Map<String, Group> ENUM_MAP =
      Collections.unmodifiableMap(
          Stream.of(values())
              .collect(Collectors.toMap(Group::getDescription, Function.identity())));

  public static Group get(String description) {
    return Optional.ofNullable(ENUM_MAP.get(description.toLowerCase()))
        .orElseThrow(
            () -> new IllegalArgumentException(String.format("Unknown: '%s'", description)));
  }

  public static List<String> descriptions() {
    return new ArrayList<>(ENUM_MAP.keySet());
  }

  @Override
  public String toString() {
    return this.description;
  }

  // TODO 평점 아이템 데이터 등에서 쓰는 가변 상수값(기본값)은 어떻게?
  private static class Constants {
    public static final String COL_USER = "user";
  }
}
