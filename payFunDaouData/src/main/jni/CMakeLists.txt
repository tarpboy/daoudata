cmake_minimum_required(VERSION 3.4.1)

add_library(daou-sign-jni SHARED
            daou-sign-jni.cpp)

# Include libraries needed for daou-sign-jni lib
target_link_libraries(daou-sign-jni
                      android
                      log)
