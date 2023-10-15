/*
 * The Apache License 2.0 Copyright (c) 2023 r4tings.com and contributors
 * https://github.com/r4tings/r4tings-recommender/LICENSE.md
 */
package com.r4tings.recommender.common.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;

@Slf4j
public class SysPathUtils {
  /*
  https://github.com/Transkribus/TranskribusInterfaces/blob/master/src/main/java/eu/transkribus/interfaces/util/SysPathUtils.java

  https://stackoverflow.com/questions/15409223/adding-new-paths-for-native-libraries-at-runtime-in-java
   */

  public static void setJavaLibraryPath(String path)
      throws NoSuchFieldException, IllegalAccessException {
    System.setProperty("java.library.path", path);

    // set sys_paths to null so that java.library.path will be reevalueted next time it is needed
    final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
    sysPathsField.setAccessible(true);
    sysPathsField.set(null, null);
  }

  public static String getJavaLibraryPath() {
    String path = System.getProperty("java.library.path");
    return path == null ? "" : path;
  }

  public static void addLibraryPath(String pathToAdd) throws Exception {
    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");

    usrPathsField.setAccessible(true);

    // get array of paths
    final String[] paths = (String[]) usrPathsField.get(null);

    // check if the path to add is already present
    for (String path : paths) {
      if (path.equals(pathToAdd)) {
        return;
      }
    }

    // add the new path
    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
    newPaths[newPaths.length - 1] = pathToAdd;
    usrPathsField.set(null, newPaths);
  }
}
