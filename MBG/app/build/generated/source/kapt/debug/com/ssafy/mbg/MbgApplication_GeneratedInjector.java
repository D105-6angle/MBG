package com.ssafy.mbg;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = MbgApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface MbgApplication_GeneratedInjector {
  void injectMbgApplication(MbgApplication mbgApplication);
}
