#!/usr/bin/env bash
gradle demoJarRelease
cp build/outputs/outputjar/*.jar DemoA/libs/
cp build/outputs/outputjar/*.jar DemoB/libs/
cp build/outputs/outputjar/*.jar DemoC/libs/